package com.android.guobao.liao.apptweak;

import java.io.IOException;
import android.util.Log;

@SuppressWarnings("unused")
public class JavaTweak_demo { //替换方法所属的类，类名必须有统一的前缀【com.android.guobao.liao.apptweak.JavaTweak_***】
    static protected void loadDexFile(ClassLoader loader, String dex) {
        //此函数内可以做一些初始化操作，比如加载native动态库，配置sotweak插件，拦截android.jar包中的系统函数等等
        //插件配置不是必须的，如果重打包后app能正常运行就无需配置。
        //如果重打包后app出现闪退现象，可以尝试依次放开下面的配置代码，在某些加固（比如梆梆）中会修复闪退现象，但不保证能修复所有闪退问题。
        //JavaTweakBridge.setPluginFlags(JavaTweakBridge.PLUGIN_FLAG_DISABLE_SYSCALL);
        //JavaTweakBridge.setPluginFlags(JavaTweakBridge.PLUGIN_FLAG_DISABLE_OPENAT);
        //JavaTweakBridge.setPluginFlags(JavaTweakBridge.PLUGIN_FLAG_DISABLE_SYSCALL|JavaTweakBridge.PLUGIN_FLAG_DISABLE_OPENAT);

        long handle = JavaTweakBridge.nativeLoadLib("libsodemo.so");
        JavaTweakBridge.writeToLogcat(Log.INFO, "nativeLoadLib: libname = libsodemo.so, handle = 0x%x", handle);

        JavaTweakBridge.hookJavaMethod("android.app.ActivityThread", "performLaunchActivity");
        JavaTweakBridge.hookJavaMethod("javax.net.ssl.SSLContext", "init");
        //JavaTweakBridge.hookJavaMethod("javax.crypto.Cipher", "getInstance(java.lang.String)"); //static方法hook例子，日志比较多，默认不hook
        //JavaTweakBridge.hookJavaMethod("javax.crypto.spec.SecretKeySpec", "(byte[],java.lang.String)"); //constructor方法hook例子，日志比较多，默认不hook
    }

    static protected void defineJavaClass(Class<?> clazz) {
        String name = clazz.getName();
        if (name.equals("okhttp3.RealCall") || name.equals("okhttp3.internal.connection.RealCall") || name.equals("okhttp3.OkHttpClient")) {
            String RealCall = null;
            if (!name.equals("okhttp3.OkHttpClient")) {
                RealCall = name;
            } else if (ReflectUtil.classForName("okhttp3.RealCall", false, clazz.getClassLoader()) != null) {
                RealCall = "okhttp3.RealCall";
            } else if (ReflectUtil.classForName("okhttp3.internal.connection.RealCall", false, clazz.getClassLoader()) != null) {
                RealCall = "okhttp3.internal.connection.RealCall";
            }
            JavaTweakBridge.hookJavaMethod(clazz.getClassLoader(), RealCall, "()okhttp3.Response", "execute");
            JavaTweakBridge.hookJavaMethod(clazz.getClassLoader(), RealCall, "(okhttp3.Callback)void", "enqueue");
            return;
        }
        if (name.equals("org.apache.http.impl.client.DefaultRequestDirector") || name.equals("org.apache.http.impl.client.DefaultHttpClient")) {
            JavaTweakBridge.hookJavaMethod(clazz.getClassLoader(), "org.apache.http.impl.client.DefaultRequestDirector", "execute(org.apache.http.HttpHost,org.apache.http.HttpRequest,org.apache.http.protocol.HttpContext)", "executing");
        }
    }

    static private Object performLaunchActivity(Object thiz, Object record, Object intent) {
        JavaTweakBridge.writeToLogcat(Log.INFO, "currentClassLoader: %s", TweakUtil.currentClassLoader());
        return JavaTweakBridge.callOriginalMethod(thiz, record, intent);
    }

    static private Object getInstance(String transformation) { //静态方法没有this参数
        return JavaTweakBridge.callStaticOriginalMethod(transformation);
    }

    static private void SecretKeySpec(Object thiz, byte[] key, String algorithm) { //构造方法没有返回值
        JavaTweakBridge.callOriginalMethod(thiz, key, algorithm);
    }

    static private void init(Object thiz, Object km, Object tm, Object random) {
        JavaTweakBridge.callOriginalMethod(thiz, km, tm, random);
    }

    static private Object execute(Object thiz) throws Exception {
        //JavaTweakBridge.writeToLogcat(Log.INFO, Log.getStackTraceString(new Throwable()));
        Object hr = JavaTweakBridge.callOriginalMethod(thiz);
        return TweakUtil.returnWithException(hr, hr == null ? new IOException(thiz.toString()) : null);
    }

    static private void enqueue(Object thiz, Object callback) {
        //JavaTweakBridge.writeToLogcat(Log.INFO, Log.getStackTraceString(new Throwable()));
        JavaTweakBridge.callOriginalMethod(thiz, callback);
    }

    static private Object executing(Object thiz, Object target, Object request, Object context) throws Exception {
        //JavaTweakBridge.writeToLogcat(Log.INFO, Log.getStackTraceString(new Throwable()));
        Object hr = JavaTweakBridge.callOriginalMethod(thiz, target, request, context);
        return TweakUtil.returnWithException(hr, hr == null ? new IOException(thiz.toString()) : null);
    }
}
