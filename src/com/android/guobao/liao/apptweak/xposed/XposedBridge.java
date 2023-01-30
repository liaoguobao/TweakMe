package com.android.guobao.liao.apptweak.xposed;

import java.lang.reflect.Member;

import com.android.guobao.liao.apptweak.JavaTweakBridge;
import com.android.guobao.liao.apptweak.util.ReflectUtil;

public final class XposedBridge {
    public static boolean hookAllConstructors(Class<?> hookClass, XC_MethodHook callback) {
        return JavaTweakBridge.hookAllJavaConstructors(hookClass, callback);
    }

    public static boolean hookAllMethods(Class<?> hookClass, String methodName, XC_MethodHook callback) {
        return JavaTweakBridge.hookAllJavaMethods(hookClass, methodName, callback);
    }

    public static boolean hookMethod(Member hookMethod, XC_MethodHook callback) {
        return JavaTweakBridge.hookJavaMethod(hookMethod.getDeclaringClass(), ReflectUtil.getMemberDeclare(hookMethod, true), callback);
    }
}
