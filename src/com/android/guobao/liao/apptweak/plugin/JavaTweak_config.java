package com.android.guobao.liao.apptweak.plugin;

import com.android.guobao.liao.apptweak.JavaTweakBridge;
import com.android.guobao.liao.apptweak.util.*;

@SuppressWarnings("unused")
public class JavaTweak_config {
    static public void loadDexFile(String dex) {
        //插件配置不是必须的，如果重打包后app能正常运行就无需配置。
        //如果重打包后app出现闪退现象，可以尝试依次放开下面的配置代码，在某些加固（比如梆梆）中会修复闪退现象，但不保证能修复所有闪退问题。
        //JavaTweakBridge.setPluginFlags(JavaTweakBridge.PLUGIN_FLAG_DISABLE_SYSCALL);
        //JavaTweakBridge.setPluginFlags(JavaTweakBridge.PLUGIN_FLAG_DISABLE_OPENAT);
        //JavaTweakBridge.setPluginFlags(JavaTweakBridge.PLUGIN_FLAG_DISABLE_SYSCALL|JavaTweakBridge.PLUGIN_FLAG_DISABLE_OPENAT);
        //JavaTweakBridge.setPluginFlags(JavaTweakBridge.PLUGIN_FLAG_DISABLE_THREAD);
    }
}
