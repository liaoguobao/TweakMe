package com.android.guobao.liao.apptweak.util;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

public class TweakUtil {
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

    static public Object returnWithException(Object hr, String name, String msg) throws Exception {
        if (!hasException(hr)) {
            return hr;
        }
        if (msg == null) {
            msg = "";
        }
        Exception e = (Exception) ReflectUtil.newClassInstance(ReflectUtil.classForName(name), "(java.lang.String)", msg);
        if (e == null) {
            e = new Exception(msg);
        }
        throw e;
    }

    static public Object returnWithException(Object hr, String name) throws Exception {
        return returnWithException(hr, name, null);
    }

    static public void voidWithException(Object hr, String name) throws Exception {
        returnWithException(hr, name, null);
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

    static public boolean hasException(Object hr) {
        return hr != null && Exception.class.isInstance(hr) && ((Exception) hr).getMessage().equals("JavaTweakBridge");
    }
}
