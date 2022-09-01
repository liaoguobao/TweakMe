package com.android.guobao.liao.apptweak.plugin;

import java.io.File;
import android.content.pm.ApplicationInfo;
import android.os.Environment;
import android.util.Log;

import com.android.guobao.liao.apptweak.JavaTweakBridge;
import com.android.guobao.liao.apptweak.util.*;

public class JavaTweak_gadget {
    static private final String GADGET_NAME = "libgadget-15.1.3-android-arm.so";
    //listen:  address, port, on_load
    //connect: address, port
    //script:           path
    //script-directory: path
    static private final String GADGET_CONF = "{\"interaction\":{\"type\":\"listen\",\"address\":\"0.0.0.0\",\"port\":33026,\"path\":\"/sdcard/tweak/com.android.tweakme/hello.js\",\"on_load\":\"resume\"},\"teardown\":\"minimal\",\"runtime\":\"default\",\"code_signing\":\"optional\"}";

    static public void loadDexFile(String dex) {
        String pn = TweakUtil.currentPackageName();
        ApplicationInfo ai = TweakUtil.getApplicationInfo(0);

        String sddir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tweak/" + pn;
        String libdir = ai.nativeLibraryDir;
        String tweakdir = ai.dataDir + "/app_tweak";

        String gadget = GADGET_NAME;
        if (gadget.indexOf('/') == -1) {
            if (new File(sddir + "/" + gadget).exists()) {
                gadget = sddir + "/" + gadget; //在 /sdcard/tweak/$PACKAGE/目录下找到了动态库
            } else if (new File(libdir + "/" + gadget).exists()) {
                gadget = libdir + "/" + gadget; //在 /data/data/$PACKAGE/lib/目录下找到了动态库
            } else if (new File(tweakdir + "/" + gadget).exists()) {
                gadget = tweakdir + "/" + gadget; //在 /data/data/$PACKAGE/app_tweak/目录下找到了动态库
            }
        }
        if (!new File(gadget).exists()) {
            JavaTweakBridge.writeToLogcat(Log.ERROR, "gadget: %s no exists.", gadget);
            return;
        }
        if (!new File(gadget).canRead()) {
            JavaTweakBridge.writeToLogcat(Log.ERROR, "gadget: %s can not read.", gadget);
            return;
        }
        String copyto = tweakdir + gadget.substring(gadget.lastIndexOf('/'));
        if (gadget.indexOf("/app_tweak/") == -1 && TweakUtil.isMainProcess()) {
            if (new File(gadget).length() != new File(copyto).length()) {
                FileUtil.copyFile(gadget, copyto); //libgadget.so文件非常大，这里简单比较一下文件大小，判断是否需要覆盖
            }
        }
        gadget = copyto;
        String conf = gadget.substring(0, gadget.length() - 3) + ".config" + ".so";
        FileUtil.writeFile(conf, GADGET_CONF.getBytes()); //写配置文件到/data/data/$PACKAGE/app_tweak/目录下

        long handle = JavaTweakBridge.nativeLoadLib(gadget);
        JavaTweakBridge.writeToLogcat(handle == 0 ? Log.ERROR : Log.INFO, "nativeLoadLib: libname = %s, handle = 0x%x", gadget, handle);
    }
}
