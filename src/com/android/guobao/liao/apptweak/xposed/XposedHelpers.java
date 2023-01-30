package com.android.guobao.liao.apptweak.xposed;

import java.lang.reflect.Member;
import com.android.guobao.liao.apptweak.util.ReflectUtil;

public final class XposedHelpers {
    public static Object callMethod(Object obj, String methodName, Class<?>[] parameterTypes, Object... args) {
        return ReflectUtil.callObjectMethod(obj, methodName + ReflectUtil.getParamTypesDeclare(parameterTypes), args);
    }

    public static Object callMethod(Object obj, String methodName, Object... args) {
        return ReflectUtil.callObjectMethod(obj, methodName, args);
    }

    public static Object callStaticMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes, Object... args) {
        return ReflectUtil.callClassMethod(clazz, methodName + ReflectUtil.getParamTypesDeclare(parameterTypes), args);
    }

    public static Object callStaticMethod(Class<?> clazz, String methodName, Object... args) {
        return ReflectUtil.callClassMethod(clazz, methodName, args);
    }

    public static boolean findAndHookConstructor(Class<?> clazz, Object... parameterTypesAndCallback) {
        return findAndHookMethod(clazz, "", parameterTypesAndCallback);
    }

    public static boolean findAndHookConstructor(String className, ClassLoader classLoader, Object... parameterTypesAndCallback) {
        return findAndHookMethod(className, classLoader, "", parameterTypesAndCallback);
    }

    public static boolean findAndHookMethod(Class<?> clazz, String methodName, Object... parameterTypesAndCallback) {
        if (parameterTypesAndCallback.length == 0 || !XC_MethodHook.class.isInstance(parameterTypesAndCallback[parameterTypesAndCallback.length - 1])) {
            return false;
        }
        Class<?>[] parameterTypes = new Class<?>[parameterTypesAndCallback.length - 1];
        for (int i = 0; i < parameterTypesAndCallback.length - 1; i++) {
            if (!Class.class.isInstance(parameterTypesAndCallback[i])) {
                return false;
            }
            parameterTypes[i] = (Class<?>) parameterTypesAndCallback[i];
        }
        XC_MethodHook callback = (XC_MethodHook) parameterTypesAndCallback[parameterTypesAndCallback.length - 1];
        Member hookMethod = ReflectUtil.findClassMember(clazz, methodName + ReflectUtil.getParamTypesDeclare(parameterTypes), false);
        return XposedBridge.hookMethod(hookMethod, callback);
    }

    public static boolean findAndHookMethod(String className, ClassLoader classLoader, String methodName, Object... parameterTypesAndCallback) {
        return findAndHookMethod(ReflectUtil.classForName(classLoader, className), methodName, parameterTypesAndCallback);
    }

    public static Class<?> findClass(String className, ClassLoader classLoader) {
        return ReflectUtil.classForName(classLoader, className);
    }

    public static Object newInstance(Class<?> clazz, Class<?>[] parameterTypes, Object... args) {
        return ReflectUtil.newClassInstance(clazz, ReflectUtil.getParamTypesDeclare(parameterTypes), args);
    }

    public static Object newInstance(Class<?> clazz, Object... args) {
        return ReflectUtil.newClassInstance(clazz, "", args);
    }

    public static boolean getBooleanField(Object obj, String fieldName) {
        return (boolean) ReflectUtil.getObjectField(obj, fieldName);
    }

    public static byte getByteField(Object obj, String fieldName) {
        return (byte) ReflectUtil.getObjectField(obj, fieldName);
    }

    public static char getCharField(Object obj, String fieldName) {
        return (char) ReflectUtil.getObjectField(obj, fieldName);
    }

    public static short getShortField(Object obj, String fieldName) {
        return (short) ReflectUtil.getObjectField(obj, fieldName);
    }

    public static int getIntField(Object obj, String fieldName) {
        return (int) ReflectUtil.getObjectField(obj, fieldName);
    }

    public static long getLongField(Object obj, String fieldName) {
        return (long) ReflectUtil.getObjectField(obj, fieldName);
    }

    public static float getFloatField(Object obj, String fieldName) {
        return (float) ReflectUtil.getObjectField(obj, fieldName);
    }

    public static double getDoubleField(Object obj, String fieldName) {
        return (double) ReflectUtil.getObjectField(obj, fieldName);
    }

    public static Object getObjectField(Object obj, String fieldName) {
        return (Object) ReflectUtil.getObjectField(obj, fieldName);
    }

    public static boolean getStaticBooleanField(Class<?> clazz, String fieldName) {
        return (boolean) ReflectUtil.getClassField(clazz, fieldName);
    }

    public static byte getStaticByteField(Class<?> clazz, String fieldName) {
        return (byte) ReflectUtil.getClassField(clazz, fieldName);
    }

    public static char getStaticCharField(Class<?> clazz, String fieldName) {
        return (char) ReflectUtil.getClassField(clazz, fieldName);
    }

    public static short getStaticShortField(Class<?> clazz, String fieldName) {
        return (short) ReflectUtil.getClassField(clazz, fieldName);
    }

    public static int getStaticIntField(Class<?> clazz, String fieldName) {
        return (int) ReflectUtil.getClassField(clazz, fieldName);
    }

    public static long getStaticLongField(Class<?> clazz, String fieldName) {
        return (long) ReflectUtil.getClassField(clazz, fieldName);
    }

    public static float getStaticFloatField(Class<?> clazz, String fieldName) {
        return (float) ReflectUtil.getClassField(clazz, fieldName);
    }

    public static double getStaticDoubleField(Class<?> clazz, String fieldName) {
        return (double) ReflectUtil.getClassField(clazz, fieldName);
    }

    public static Object getStaticObjectField(Class<?> clazz, String fieldName) {
        return (Object) ReflectUtil.getClassField(clazz, fieldName);
    }

    public static void setBooleanField(Object obj, String fieldName, boolean value) {
        ReflectUtil.setObjectField(obj, fieldName, value);
    }

    public static void setByteField(Object obj, String fieldName, byte value) {
        ReflectUtil.setObjectField(obj, fieldName, value);
    }

    public static void setCharField(Object obj, String fieldName, char value) {
        ReflectUtil.setObjectField(obj, fieldName, value);
    }

    public static void setShortField(Object obj, String fieldName, short value) {
        ReflectUtil.setObjectField(obj, fieldName, value);
    }

    public static void setIntField(Object obj, String fieldName, int value) {
        ReflectUtil.setObjectField(obj, fieldName, value);
    }

    public static void setLongField(Object obj, String fieldName, long value) {
        ReflectUtil.setObjectField(obj, fieldName, value);
    }

    public static void setFloatField(Object obj, String fieldName, float value) {
        ReflectUtil.setObjectField(obj, fieldName, value);
    }

    public static void setDoubleField(Object obj, String fieldName, double value) {
        ReflectUtil.setObjectField(obj, fieldName, value);
    }

    public static void setObjectField(Object obj, String fieldName, Object value) {
        ReflectUtil.setObjectField(obj, fieldName, value);
    }

    public static void setStaticBooleanField(Class<?> clazz, String fieldName, boolean value) {
        ReflectUtil.setClassField(clazz, fieldName, value);
    }

    public static void setStaticByteField(Class<?> clazz, String fieldName, byte value) {
        ReflectUtil.setClassField(clazz, fieldName, value);
    }

    public static void setStaticCharField(Class<?> clazz, String fieldName, char value) {
        ReflectUtil.setClassField(clazz, fieldName, value);
    }

    public static void setStaticShortField(Class<?> clazz, String fieldName, short value) {
        ReflectUtil.setClassField(clazz, fieldName, value);
    }

    public static void setStaticIntField(Class<?> clazz, String fieldName, int value) {
        ReflectUtil.setClassField(clazz, fieldName, value);
    }

    public static void setStaticLongField(Class<?> clazz, String fieldName, long value) {
        ReflectUtil.setClassField(clazz, fieldName, value);
    }

    public static void setStaticFloatField(Class<?> clazz, String fieldName, float value) {
        ReflectUtil.setClassField(clazz, fieldName, value);
    }

    public static void setStaticDoubleField(Class<?> clazz, String fieldName, double value) {
        ReflectUtil.setClassField(clazz, fieldName, value);
    }

    public static void setStaticObjectField(Class<?> clazz, String fieldName, Object value) {
        ReflectUtil.setClassField(clazz, fieldName, value);
    }

    public static final class ClassNotFoundError extends Error {
        private static final long serialVersionUID = -1070936889459514628L;

        public ClassNotFoundError(Throwable cause) {
            super(cause);
        }

        public ClassNotFoundError(String detailMessage, Throwable cause) {
            super(detailMessage, cause);
        }
    }

    public static final class InvocationTargetError extends Error {
        private static final long serialVersionUID = -1070936889459514628L;

        public InvocationTargetError(Throwable cause) {
            super(cause);
        }
    }
}
