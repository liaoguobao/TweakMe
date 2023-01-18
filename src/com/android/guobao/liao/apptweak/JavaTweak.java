package com.android.guobao.liao.apptweak;

import com.android.guobao.liao.apptweak.plugin.*;

public class JavaTweak { //此类是插件配置入口类
    static private final JavaTweakPlugin[] plugins = new JavaTweakPlugin[] { //需要激活的插件列表
            new JavaTweak_config(), //config插件
            //new JavaTweak_proxy(), //proxy插件
            //new JavaTweak_gadget(), //gadget插件
            //new JavaTweak_algo(), //algo插件
            //new JavaTweak_webview(), //webview插件
            //new JavaTweak_ssllog(), //ssllog插件
            new JavaTweak_demo(), //demo插件
    };

    static protected void loadDexFile(String dex) {
        for (int i = 0; i < plugins.length; i++) {
            plugins[i].loadDexFile(dex);
        }
    }

    static protected void defineClassLoader(ClassLoader loader) {
        for (int i = 0; i < plugins.length; i++) {
            plugins[i].defineClassLoader(loader);
        }
    }

    static protected void defineJavaClass(Class<?> clazz) {
        for (int i = 0; i < plugins.length; i++) {
            plugins[i].defineJavaClass(clazz);
        }
    }
}
