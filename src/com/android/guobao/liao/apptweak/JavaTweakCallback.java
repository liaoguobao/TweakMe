package com.android.guobao.liao.apptweak;

@SuppressWarnings("unused")
public class JavaTweakCallback {
    static private void loadDexFile(ClassLoader loader, String dex) {
        JavaTweak.loadDexFile(loader, dex); //javatweak.dex永远都是第一个被回调, 且一定会回调, 也是目前唯一的一个回调
    }

    static private void defineJavaClass(Class<?> clazz) {
        JavaTweak.defineJavaClass(clazz);
    }
}
