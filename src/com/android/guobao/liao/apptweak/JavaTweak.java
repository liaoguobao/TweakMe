package com.android.guobao.liao.apptweak;

import com.android.guobao.liao.apptweak.plugin.*;

public class JavaTweak {
    static protected void loadDexFile(String dex) {
        JavaTweak_config.loadDexFile(dex);
        //JavaTweak_proxy.loadDexFile(dex);
        //JavaTweak_gadget.loadDexFile(dex);
        //JavaTweak_algo.loadDexFile(dex);
        JavaTweak_demo.loadDexFile(dex);
    }

    static protected void defineClassLoader(ClassLoader loader) {
        JavaTweak_demo.defineClassLoader(loader);
    }

    static protected void defineJavaClass(Class<?> clazz) {
        //JavaTweak_webview.defineJavaClass(clazz);
        //JavaTweak_proxy.defineJavaClass(clazz);
        //JavaTweak_ssllog.defineJavaClass(clazz);
        JavaTweak_demo.defineJavaClass(clazz);
    }
}
