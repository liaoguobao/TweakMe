package com.android.guobao.liao.apptweak;

@SuppressWarnings("unused")
public class JavaTweakCallback {
    static private void loadDexFile(String dex) { //javatweak.dex被加载时回调
        JavaTweak.loadDexFile(dex);
    }

    static private void defineClassLoader(ClassLoader loader) { //类加载器第一次被创建时回调
        JavaTweak.defineClassLoader(loader);
    }

    static private void defineJavaClass(Class<?> clazz) { //类第一次被创建时回调
        JavaTweak.defineJavaClass(clazz);
    }
}
