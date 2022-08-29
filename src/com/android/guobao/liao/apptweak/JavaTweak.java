package com.android.guobao.liao.apptweak;

import com.android.guobao.liao.apptweak.plugin.*;

public class JavaTweak {
    static protected void loadDexFile(ClassLoader loader, String dex) {
        JavaTweak_config.loadDexFile(loader, dex);
        //JavaTweak_proxy.loadDexFile(loader, dex);
        JavaTweak_demo.loadDexFile(loader, dex);
        //JavaTweak_algo.loadDexFile(loader, dex);
    }

    static protected void defineJavaClass(Class<?> clazz) {
        //JavaTweak_webview.defineJavaClass(clazz);
        //JavaTweak_proxy.defineJavaClass(clazz);
        //JavaTweak_ssllog.defineJavaClass(clazz);
        JavaTweak_demo.defineJavaClass(clazz);
    }
}
