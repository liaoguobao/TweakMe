package com.android.guobao.liao.apptweak;

public class JavaTweak {
    static protected void loadDexFile(ClassLoader loader, String dex) {
        JavaTweak_demo.loadDexFile(loader, dex);
    }

    static protected void defineJavaClass(Class<?> clazz) {
        JavaTweak_demo.defineJavaClass(clazz);
    }
}
