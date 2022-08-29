package com.android.guobao.liao.apptweak;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import android.util.Log;

import com.android.guobao.liao.apptweak.util.*;

public class JavaTweakBridge {
    static public final int PLUGIN_FLAG_DISABLE_OPENAT = 0x00000001;
    static public final int PLUGIN_FLAG_DISABLE_SYSCALL = 0x00000002;
    static public final int PLUGIN_FLAG_DISABLE_THREAD = 0x00000004;

    static private/*final*/int pluginFlags = 0;
    static private final String pluginPackage = JavaTweakBridge.class.getPackage().getName() + "." + "plugin";
    static private final String hookClassPrefix = pluginPackage + "." + "JavaTweak_";
    static private final ConcurrentHashMap<String, Method> backupMethods = new ConcurrentHashMap<String, Method>();

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
     * tweak_class: 被替换方法所属的类
     * tweak_method：被替换方法
     * hook_class: 替换方法所属的类
     * hook_method：替换方法
     * 假设类C中有一个名为【List<String> doCommand(int i1, byte[] b2, boolean z3, String s4, HashMap<int, Object> o5)】的方法
     * 对于非构造方法，tweak_method的定义方式支持以下三种  
         1、【doCommand】 //通过名称查找非构造方法,这种方式要确保方法名在类中唯一
         2、【doCommand(int,byte[],boolean,java.lang.String,java.util.HashMap)】 //通过完整声明查找非构造方法
         3、【(int,byte[],boolean,java.lang.String,java.util.HashMap<int,java.lang.Object>)java.util.List<java.lang.String>】 //通过方法签名查找非构造方法,这种方式要确保签名在类中唯一，且泛型类参数要写明实例类型
     * 假设类C中有一个名为【C(int i1, byte[] b2, boolean z3, String s4, HashMap<int, Object> o5)】的构造方法
     * 对于构造方法，tweak_method的定义方式支持以下一种
         4、【(int,byte[],boolean,java.lang.String,java.util.HashMap)】 //通过完整声明查找构造方法
     * hook_method的定义方式和tweak_method一样，但由于hook_method是由我们自己编写，所以通常采用方式1，设置为一个类中唯一的方法名即可。
     * 2、3、4三种方式参数类型申明必须精确定义，不能有空格，参数与参数之间用逗号分隔
     * 被替换方法如果是非静态方法时，替换方法的第一个参数必须是被替换方法的this指针，其它参数依次后移（即替换方法会多一个this参数）
     * 替换方法必须是静态方法
     * 被替换方法不能是native、interface、abstract等方法
     * 替换方法所属的类，类名必须有统一的前缀【com.android.guobao.liao.apptweak.JavaTweak_***】
     * return: 返回值为被替换方法的备份方法,用来调用原方法
     */
    static private native Method nativeHookMethod(Class<?> tweak_class, String tweak_method, Class<?> hook_class, String hook_method);

    static public boolean hookJavaMethod(Class<?> tweak_class, String tweak_method, String hook_method) {
        boolean hr = hookJavaMethod(tweak_class.getClassLoader(), tweak_class.getName(), tweak_method, null, hook_method);
        return hr;
    }

    static public boolean hookJavaMethod(Class<?> tweak_class, String tweak_method) {
        boolean hr = hookJavaMethod(tweak_class.getClassLoader(), tweak_class.getName(), tweak_method, null, null);
        return hr;
    }

    static public boolean hookJavaMethod(String tweak_class, String tweak_method, String hook_method) {
        boolean hr = hookJavaMethod(null, tweak_class, tweak_method, null, hook_method);
        return hr;
    }

    static public boolean hookJavaMethod(String tweak_class, String tweak_method) {
        boolean hr = hookJavaMethod(null, tweak_class, tweak_method, null, null);
        return hr;
    }

    static public boolean hookJavaMethod(ClassLoader tweak_loader, String tweak_class, String tweak_method, String hook_method) {
        boolean hr = hookJavaMethod(tweak_loader, tweak_class, tweak_method, null, hook_method);
        return hr;
    }

    static public boolean hookJavaMethod(ClassLoader tweak_loader, String tweak_class, String tweak_method) {
        boolean hr = hookJavaMethod(tweak_loader, tweak_class, tweak_method, null, null);
        return hr;
    }

    static private boolean hookJavaMethod(ClassLoader tweak_loader, String tweak_class, String tweak_method, String hook_class, String hook_method) {
        try {
            Class<?> tweak_class_ = null;
            Class<?> hook_class_ = null;
            String hook_method_name = null;

            if (tweak_loader == null) {
                tweak_class_ = Class.forName(tweak_class);
            } else {
                tweak_class_ = Class.forName(tweak_class, true, tweak_loader);
            }
            if (hook_class == null) {
                StackTraceElement[] stes = new Throwable().getStackTrace();
                //stes[0].getClassName()=="com.android.guobao.liao.apptweak.JavaTweakBridge", skip it.
                for (int i = 1; i < stes.length; i++) {
                    if (stes[i].getClassName().startsWith(hookClassPrefix)) {
                        hook_class_ = Class.forName(stes[i].getClassName());
                        break;
                    }
                }
            } else {
                hook_class_ = Class.forName(hook_class);
            }
            if (hook_method == null) {
                int index = tweak_method.indexOf('(');
                hook_method_name = (index == -1 ? tweak_method : tweak_method.substring(0, index));
                hook_method_name = (index == 0 ? tweak_class_.getSimpleName() : hook_method_name);
                hook_method = hook_method_name;
            } else {
                int index = hook_method.indexOf('(');
                hook_method_name = (index == -1 ? hook_method : hook_method.substring(0, index));
            }
            if (backupMethods.containsKey(hook_method_name)) {
                //writeToLogcat(Log.WARN, "hookJavaMethod: method<%s> hook repeat.", tweak_method);
                return false;
            }
            Method m = nativeHookMethod(tweak_class_, tweak_method, hook_class_, hook_method);
            if (m == null) {
                writeToLogcat(Log.ERROR, "hookJavaMethod: method<%s> hook error.", tweak_method);
                return false;
            }
            backupMethods.put(hook_method_name, m);
            writeToLogcat(Log.INFO, "hookJavaMethod: method<%s> hook ok.", tweak_method);
            return true;
        } catch (Throwable e) {
            writeToLogcat(Log.ERROR, e.toString());
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    static private <T> T callOriginalMethod(boolean log, String name, Object receiver, Object... args) {
        T hr = null;
        Method m = null;
        try {
            if (name == null) {
                StackTraceElement[] stes = new Throwable().getStackTrace();
                //stes[0].getClassName()=="com.android.guobao.liao.apptweak.JavaTweakBridge", skip it.
                //stes[0].getMethodName()=="callOriginalMethod", skip it.
                for (int i = 1; i < stes.length; i++) {
                    if (stes[i].getClassName().startsWith(hookClassPrefix)) {
                        name = stes[i].getMethodName();
                        break;
                    }
                }
            }
            m = backupMethods.get(name);
            hr = (T) m.invoke(receiver, args);
        } catch (Throwable e) {
            writeToLogcat(Log.ERROR, "callOriginalMethod: name<%s> error<%s>.", name, e); //如果原函数可能抛出异常，进到这里说明此次反射调用触发了异常。
        }
        if (log) {
            writeToLogcat(Log.INFO, paramsToString(name, m, hr, receiver, args));
        }
        return hr;
    }

    static public <T> T callStaticOriginalMethod(Object... args) {
        T hr = callOriginalMethod(true, null, null, args);
        return hr;
    }

    static public <T> T callOriginalMethod(Object receiver, Object... args) {
        T hr = callOriginalMethod(true, null, receiver, args);
        return hr;
    }

    static public <T> T nologStaticOriginalMethod(Object... args) {
        T hr = callOriginalMethod(false, null, null, args);
        return hr;
    }

    static public <T> T nologOriginalMethod(Object receiver, Object... args) {
        T hr = callOriginalMethod(false, null, receiver, args);
        return hr;
    }

    static private String paramsToString(String name, Method m, Object hr, Object receiver, Object... args) {
        final int maxlen = 4096;
        Class<?> type = m.getReturnType();
        Class<?>[] types = m.getParameterTypes();

        String log = String.format("%s::%s%s->{\r\n", m.getDeclaringClass().getName(), m.getName(), !m.getName().equals(name) ? "@" + name : "");
        log += String.format("\t_this_ = %s->%s\r\n", m.getDeclaringClass().getName(), receiver);
        for (int i = 0; i < args.length; i++) {
            String byteArr = ((args[i] instanceof byte[]) ? StringUtil.hexToVisible(((byte[]) args[i]).length > maxlen ? Arrays.copyOf((byte[]) args[i], maxlen) : (byte[]) args[i]) : null);
            String objArr = ((args[i] instanceof Object[]) ? Arrays.deepToString((Object[]) args[i]) : null);
            log += String.format("\tparam%d = %s->%s\r\n", i + 1, types[i].getName(), byteArr != null ? byteArr : (objArr != null ? objArr : args[i]));
        }
        String byteArr = ((hr instanceof byte[]) ? StringUtil.hexToVisible(((byte[]) hr).length > maxlen ? Arrays.copyOf((byte[]) hr, maxlen) : (byte[]) hr) : null);
        String objArr = ((hr instanceof Object[]) ? Arrays.deepToString((Object[]) hr) : null);

        log += String.format("\treturn = %s->%s\r\n}\r\n", type.getName(), byteArr != null ? byteArr : (objArr != null ? objArr : hr));
        return log;
    }

    static public void setPluginFlags(int flags) {
        pluginFlags = flags;
    }
}
