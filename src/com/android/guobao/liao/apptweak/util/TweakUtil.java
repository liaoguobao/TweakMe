package com.android.guobao.liao.apptweak.util;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

public class TweakUtil {
    static public String currentPackageName() {
        Class<?> clazz = ReflectUtil.classForName("android.app.ActivityThread");
        String s = (String) ReflectUtil.callClassMethod(clazz, "currentPackageName");
        return s;
    }

    static public String currentProcessName() {
        Class<?> clazz = ReflectUtil.classForName("android.app.ActivityThread");
        String s = (String) ReflectUtil.callClassMethod(clazz, "currentProcessName");
        return s;
    }

    static public boolean isMainProcess() {
        String n1 = currentPackageName();
        String n2 = currentProcessName();
        return n1.equals(n2);
    }

    static public Application currentApplication() {
        Class<?> clazz = ReflectUtil.classForName("android.app.ActivityThread");
        Application app = (Application) ReflectUtil.callClassMethod(clazz, "currentApplication");
        return app;
    }

    static public Context getSystemContext() {
        Class<?> clazz = ReflectUtil.classForName("android.app.ActivityThread");
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
