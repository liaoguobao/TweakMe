package com.android.guobao.liao.apptweak;

import android.app.Application;

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

    static public Application currentApplication() {
        Class<?> clazz = ReflectUtil.classForName("android.app.ActivityThread", false, null);
        Application s = (Application) ReflectUtil.callClassMethod(clazz, "currentApplication");
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
}
