package com.android.guobao.liao.apptweak.util;

import java.lang.ref.WeakReference;
import java.util.Map;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

public class TweakUtil {
    static public ClassLoader currentClassLoader() {
        Class<?> clazz = ReflectUtil.classForName("android.app.ActivityThread", false, null);
        Object activityThread = ReflectUtil.callClassMethod(clazz, "currentActivityThread");
        Object packageName = ReflectUtil.callClassMethod(clazz, "currentPackageName");

        Object mPackages = ReflectUtil.getObjectField(activityThread, "mPackages");
        Object loadedApk = ((WeakReference<?>) ((Map<?, ?>) mPackages).get(packageName)).get();
        Object mClassLoader = ReflectUtil.getObjectField(loadedApk, "mClassLoader");
        return (ClassLoader) mClassLoader;
    }

    static public String currentPackageName() {
        Class<?> clazz = ReflectUtil.classForName("android.app.ActivityThread", false, null);
        String s = (String) ReflectUtil.callClassMethod(clazz, "currentPackageName");
        return s;
    }

    static public String currentProcessName() {
        Class<?> clazz = ReflectUtil.classForName("android.app.ActivityThread", false, null);
        String s = (String) ReflectUtil.callClassMethod(clazz, "currentProcessName");
        return s;
    }

    static public boolean isMainProcess() {
        String n1 = currentPackageName();
        String n2 = currentProcessName();
        return n1.equals(n2);
    }

    static public Application currentApplication() {
        Class<?> clazz = ReflectUtil.classForName("android.app.ActivityThread", false, null);
        Application app = (Application) ReflectUtil.callClassMethod(clazz, "currentApplication");
        return app;
    }

    static public Object returnWithException(Object hr, Exception e, boolean force) throws Exception {
        if (force) {
            e = (e == null ? new Exception(hr == null ? "returnWithException" : hr.toString()) : e);
            throw e;
        }
        if (e != null && hr == null) {
            throw e;
        }
        return hr; //(e == null || hr != null)
    }

    static public Object returnWithException(Object hr, Exception e) throws Exception {
        return returnWithException(hr, e, false);
    }

    static public Context getSystemContext() {
        Class<?> clazz = ReflectUtil.classForName("android.app.ActivityThread", false, null);
        Object at = ReflectUtil.callClassMethod(clazz, "currentActivityThread");
        Context sc = (Context) ReflectUtil.callObjectMethod(at, "getSystemContext");
        return sc;
    }

    static public PackageInfo getPackageInfo(int flags) {
        Context sc = getSystemContext();
        String pn = currentPackageName();
        Object pm = ReflectUtil.callObjectMethod(sc, "getPackageManager");
        PackageInfo pi = (PackageInfo) ReflectUtil.callObjectMethod(pm, "getPackageInfo(java.lang.String,int)", pn, flags);
        return pi;
    }

    static public ApplicationInfo getApplicationInfo(int flags) {
        Context sc = getSystemContext();
        String pn = currentPackageName();
        Object pm = ReflectUtil.callObjectMethod(sc, "getPackageManager");
        ApplicationInfo ai = (ApplicationInfo) ReflectUtil.callObjectMethod(pm, "getApplicationInfo(java.lang.String,int)", pn, flags);
        return ai;
    }
}
