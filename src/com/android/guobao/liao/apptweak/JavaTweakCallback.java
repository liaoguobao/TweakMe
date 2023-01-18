package com.android.guobao.liao.apptweak;

@SuppressWarnings("unused")
public class JavaTweakCallback { //此类的所有方法都由native层回调
    static private void loadDexFile(String dex) { //javatweak.dex被加载时回调
        JavaTweak.loadDexFile(dex);
    }

    static private void defineClassLoader(ClassLoader loader) { //类加载器第一次被创建时回调
        JavaTweak.defineClassLoader(loader);
    }

    static private void defineJavaClass(Class<?> clazz) { //类第一次被创建时回调
        JavaTweak.defineJavaClass(clazz);
    }

    static private Object handleHookedMethod(Object thiz, Object[] args, Object data) throws Throwable { //方法被调用时回调
        return ((JavaTweakHook) data).handleHookedMethod(thiz, args);
    }
}
