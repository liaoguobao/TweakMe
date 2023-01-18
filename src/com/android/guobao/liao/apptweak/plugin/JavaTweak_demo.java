package com.android.guobao.liao.apptweak.plugin;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import com.android.guobao.liao.apptweak.JavaTweakBridge;
import com.android.guobao.liao.apptweak.JavaTweakPlugin;
import com.android.guobao.liao.apptweak.util.*;

public class JavaTweak_demo extends JavaTweakPlugin {
    protected void loadDexFile(String dex) {
        //此回调函数主要用来hook系统类方法，也可以做一些初始化操作，比如加载native动态库
        long handle = JavaTweakBridge.nativeLoadLib("libsodemo.so");
        JavaTweakBridge.writeToLogcat(Log.INFO, "nativeLoadLib: libname = libsodemo.so, handle = 0x%x", handle);

        JavaTweakBridge.hookJavaMethod(Dialog.class, "show"); //实例方法hook例子, 如果方法在类中没有被重载，可以只写方法名
        JavaTweakBridge.hookJavaMethod(Activity.class, "startActivityForResult(android.content.Intent,int,android.os.Bundle)"); //实例方法hook例子, 如果方法在类中有被重载，必须指明参数列表

        //JavaTweakBridge.hookJavaMethod(Cipher.class, "getInstance(java.lang.String)"); //静态方法hook例子
        //JavaTweakBridge.hookJavaMethod(SecretKeySpec.class, "(byte[],java.lang.String)"); //构造方法hook例子

        //JavaTweakBridge.hookJavaMethod(Thread.class, "currentThread"); //native方法hook例子（先注册后hook）

        //JavaTweakBridge.hookAllJavaConstructors(Thread.class); //hook类的所有构造方法
        //JavaTweakBridge.hookAllJavaMethods(AssetManager.class, "open"); //hook方法的所有重载方法
    }

    protected void defineClassLoader(ClassLoader loader) {
        //此回调函数主要用来hook动态延迟加载的dex中的方法，或者defineJavaClass中没有回调的类方法
        JavaTweakBridge.writeToLogcat(Log.INFO, "defineClassLoader: %s", loader);
        JavaTweakBridge.hookJavaMethod(loader, "com.taobao.wireless.security.adapter.JNICLibrary", "doCommandNative"); //native方法hook例子（先hook后注册）
    }

    protected void defineJavaClass(Class<?> clazz) {
        //此回调函数主要用来hook非系统类方法
        String name = clazz.getName();
        if (name.equals("okhttp3.RealCall") || name.equals("okhttp3.internal.connection.RealCall") || name.equals("okhttp3.OkHttpClient")) {
            Class<?> RealCall = null;
            if (RealCall == null) {
                RealCall = ReflectUtil.classForName(clazz.getClassLoader(), "okhttp3.RealCall");
            }
            if (RealCall == null) {
                RealCall = ReflectUtil.classForName(clazz.getClassLoader(), "okhttp3.internal.connection.RealCall");
            }
            if (RealCall == null) {
                JavaTweakBridge.writeToLogcat(Log.WARN, "%s: class RealCall no exist", name);
            }
            if (RealCall != null) {
                JavaTweakBridge.hookJavaMethod(RealCall, "()okhttp3.Response");
                JavaTweakBridge.hookJavaMethod(RealCall, "(okhttp3.Callback)void");
            }
        }
    }
}
