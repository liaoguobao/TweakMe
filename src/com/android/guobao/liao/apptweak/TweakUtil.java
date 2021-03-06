package com.android.guobao.liao.apptweak;

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
        Object pm = ReflectUtil.callObjectMethod(sc, "getPackageManager");
        PackageInfo pi = (PackageInfo) ReflectUtil.callObjectMethod(pm, "getPackageInfo(java.lang.String,int)", sc.getPackageName(), flags);
        return pi;
    }

    static public PackageInfo getPackageInfo() {
        PackageInfo pi = getPackageInfo(0);
        return pi;
    }

    static public ApplicationInfo getApplicationInfo() {
        PackageInfo pi = getPackageInfo();
        return pi.applicationInfo;
    }
}
