package com.android.guobao.liao.apptweak;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import android.util.Log;
import com.android.guobao.liao.apptweak.util.ReflectUtil;

public class JavaTweakBridge {
    static public final int PLUGIN_FLAG_DISABLE_OPENAT = 0x00000001;
    static public final int PLUGIN_FLAG_DISABLE_SYSCALL = 0x00000002;
    static public final int PLUGIN_FLAG_DISABLE_THREAD = 0x00000004;
    static public final int PLUGIN_FLAG_CAN_HOOK_CHAIN = 0x00010000;

    static private/*final*/int pluginFlags = 0;
    static private final ConcurrentHashMap<String, JavaTweakHook> backupMethods = new ConcurrentHashMap<String, JavaTweakHook>();

    /*
     * nativeHookSymbol函数可以hook动态库中的符号（此符号可以是导出的也可是没导出的），为第三方so开发提供hook入口，专为native层hook函数而设计（通过反射调用），java层不可调用，支持armeabi、armeabi-v7a、arm64-v8a三种ABI，支持thumb、arm两种指令集。
     * symbol: 需要hook的函数符号地址
     * detour: 跳板方法的函数符号地址
     * return: 成功返回原始方法的调用地址，失败返回0
     */
    static private native long nativeHookSymbol(long symbol, long detour);

    /*
     * nativeLoadLib函数可以加载已经加载的so，也可以加载尚未加载的so，可以加载系统so，也可以加载非系统so，可以加载自己/data目录下的so，也可以加载/sdcard目录下的so
     * libname: 可以写全路径，也可以只写库名字，如果只写库名字，优先查找/sdcard/tweak/$PACKAGE目录，其次查找/data/data/$PACKAGE/lib目录，最后查找系统目录
     * return: 加载成功返回dlopen句柄，失败返回0
     * nativeLoadLib返回的句柄，没有namespace限制，可以调用dlsym定位任意so（比如libart.so）的符号地址,如果是首次加载so，会主动调用JNI_OnLoad方法（如果有的话）
     */
    static public native long nativeLoadLib(String libname);

    static private native void nativePrintLog(int bufid, int prio, String tag, String msg);

    static public void writeToLogcat(int prio, String msg) {
        nativePrintLog(0, prio, "WriteToLogcat", msg + "\r\n");
    }

    static public void writeToLogcat(int prio, String format, Object... args) {
        writeToLogcat(prio, String.format(format, args));
    }

    /*
     * hook_class: 被替换方法所属的类
     * hook_method：被替换方法
     * 假设类C中有一个名为【List<String> doCommand(int i1, byte[] b2, boolean z3, String s4, HashMap<int, Object> o5)】的方法
     * 对于非构造方法，hook_method的定义方式支持以下三种  
         1、【doCommand】 //通过名称查找非构造方法,这种方式要确保方法名在类中唯一
         2、【doCommand(int,byte[],boolean,java.lang.String,java.util.HashMap)】 //通过完整声明查找非构造方法
         3、【(int,byte[],boolean,java.lang.String,java.util.HashMap<int,java.lang.Object>)java.util.List<java.lang.String>】 //通过方法签名查找非构造方法,这种方式要确保签名在类中唯一，且泛型类参数要写明实例类型
     * 假设类C中有一个名为【C(int i1, byte[] b2, boolean z3, String s4, HashMap<int, Object> o5)】的构造方法
     * 对于构造方法，hook_method的定义方式支持以下一种
         4、【(int,byte[],boolean,java.lang.String,java.util.HashMap)】 //通过完整声明查找构造方法
     * 2、3、4三种方式参数类型申明必须精确定义，不能有空格，参数与参数之间用逗号分隔
     * 被替换方法不能是interface、abstract等方法
     * return: 返回值为被替换方法的备份方法,用来调用原方法
     */
    static private native Method nativeHookMethod(Class<?> hook_class, String hook_method, Object hook_data, boolean can_hook_chain);

    static public boolean hookJavaMethod(Class<?> hook_class, String hook_method, JavaTweakHook hook_data) {
        try {
            boolean can_hook_chain = (pluginFlags & PLUGIN_FLAG_CAN_HOOK_CHAIN) != 0;
            if (hook_class == null || hook_method == null || hook_method.equals("")) {
                return false;
            }
            Member hook_member = ReflectUtil.findClassMember(hook_class, hook_method, false);
            if (hook_member == null) {
                writeToLogcat(Log.ERROR, "hookJavaMethod: method<%s> no exist.", hook_method);
                return false;
            }
            String method_decl = ReflectUtil.getMemberDeclare(hook_member, false);
            if (!can_hook_chain && backupMethods.containsKey(method_decl)) {
                //writeToLogcat(Log.WARN, "hookJavaMethod: method<%s> hook repeat.", hook_method);
                return false;
            }
            if (hook_data == null) {
                hook_data = JavaTweakHook.onlyLogHook();
            }
            Method m = nativeHookMethod(hook_class, hook_method, hook_data, can_hook_chain);
            if (m == null) {
                writeToLogcat(Log.ERROR, "hookJavaMethod: method<%s> hook error.", hook_method);
                return false;
            }
            hook_data.setBackup(m);
            backupMethods.put(method_decl, hook_data);
            writeToLogcat(Log.INFO, "hookJavaMethod: method<%s> hook ok.", hook_method);
            return true;
        } catch (Throwable e) {
            writeToLogcat(Log.ERROR, "hookJavaMethod: method<%s> hook exception: %s.", hook_method, e);
            return false;
        }
    }

    static public boolean hookJavaMethod(Class<?> hook_class, String hook_method) {
        return hookJavaMethod(hook_class, hook_method, (JavaTweakHook) null);
    }

    static public boolean hookAllJavaMethods(Class<?> hook_class, String method_name, JavaTweakHook hook_data) {
        Method[] ms = hook_class.getDeclaredMethods();
        for (int i = 0; i < ms.length; i++) {
            if (ms[i].getName().equals(method_name)) {
                hookJavaMember(ms[i], hook_data);
            }
        }
        return true;
    }

    static public boolean hookAllJavaMethods(Class<?> hook_class, String method_name) {
        return hookAllJavaMethods(hook_class, method_name, null);
    }

    static public boolean hookAllJavaConstructors(Class<?> hook_class, JavaTweakHook hook_data) {
        Constructor<?>[] cs = hook_class.getDeclaredConstructors();
        for (int i = 0; i < cs.length; i++) {
            if (true) {
                hookJavaMember(cs[i], hook_data);
            }
        }
        return true;
    }

    static public boolean hookAllJavaConstructors(Class<?> hook_class) {
        return hookAllJavaConstructors(hook_class, null);
    }

    static private boolean hookJavaMember(Member hook_member, JavaTweakHook hook_data) {
        Class<?> clazz = hook_member.getDeclaringClass();
        String decl = ReflectUtil.getMemberDeclare(hook_member, true);
        if (hook_data == null) {
            hookJavaMethod(clazz, decl);
            return true;
        }
        Constructor<?> hook_constr = hook_data.getClass().getDeclaredConstructors()[0];
        String cons = ReflectUtil.getMemberDeclare(hook_constr, true);

        Object thiz = ReflectUtil.getObjectField(hook_data, "this$0");
        Object name_ = ReflectUtil.getObjectField(hook_data, "name_");
        Object nolog_ = ReflectUtil.getObjectField(hook_data, "nolog_");

        ArrayList<Object> args = new ArrayList<Object>();
        Class<?>[] ts = hook_constr.getParameterTypes();
        for (int i = 0; i < ts.length; i++) {
            String t = ts[i].getName();
            if (i == 0 && thiz != null) {
                args.add(thiz);
            } else if (t.equals("java.lang.String")) {
                args.add(name_);
            } else if (t.equals("boolean")) {
                args.add(nolog_);
            }
        }
        Object data = ReflectUtil.newClassInstance(hook_data.getClass(), cons, args.toArray());
        hookJavaMethod(clazz, decl, (JavaTweakHook) data);
        return true;
    }

    static public void setPluginFlags(int flags) {
        pluginFlags = flags;
    }

    static public int getPluginFlags() {
        return pluginFlags;
    }

    static public boolean hookJavaMethod(ClassLoader hook_loader, String hook_class, String hook_method, JavaTweakHook hook_data) {
        return hookJavaMethod(ReflectUtil.classForName(hook_loader, hook_class), hook_method, hook_data);
    }

    static public boolean hookJavaMethod(ClassLoader hook_loader, String hook_class, String hook_method) {
        return hookJavaMethod(hook_loader, hook_class, hook_method, null);
    }

    static public boolean hookJavaMethod(Class<?> hook_class, String hook_method, String friendly_method_name_for_log) {
        return hookJavaMethod(hook_class, hook_method, JavaTweakHook.nameLogHook(friendly_method_name_for_log));
    }
}