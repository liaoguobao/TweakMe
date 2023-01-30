package com.android.guobao.liao.apptweak.plugin;

import android.content.Context;
import android.util.Log;
import com.android.guobao.liao.apptweak.JavaTweakBridge;
import com.android.guobao.liao.apptweak.JavaTweakHook;
import com.android.guobao.liao.apptweak.JavaTweakPlugin;
import com.android.guobao.liao.apptweak.util.ReflectUtil;
import com.android.guobao.liao.apptweak.util.TweakUtil;
import com.android.guobao.liao.apptweak.xposed.IXposedHookLoadPackage;

public class JavaTweak_xposed extends JavaTweakPlugin {
    private boolean handled_;
    private IXposedHookLoadPackage[] modules_;

    public JavaTweak_xposed(IXposedHookLoadPackage[] modules) {
        handled_ = false;
        modules_ = modules;
    }

    protected void loadDexFile(String dex) {
        //开启hook链，因为多个xposed模块同时启用时可能有些方法会被多次hook
        JavaTweakBridge.setPluginFlags(JavaTweakBridge.getPluginFlags() | JavaTweakBridge.PLUGIN_FLAG_CAN_HOOK_CHAIN);

        JavaTweakBridge.hookJavaMethod(ReflectUtil.classForName("android.app.ContextImpl"), "createActivityContext", new JavaTweakHook(true) {
            protected void afterHookedMethod(Object thiz, Object[] args) {
                handleLoadPackage((Context) getResult());
            }
        });
        JavaTweakBridge.hookJavaMethod(ReflectUtil.classForName("android.app.ContextImpl"), "createAppContext", new JavaTweakHook(true) {
            protected void afterHookedMethod(Object thiz, Object[] args) {
                handleLoadPackage((Context) getResult());
            }
        });
        handleLoadPackage((Context) TweakUtil.currentApplication());
    }

    public void handleLoadPackage(Context context) {
        if (handled_ || context == null) {
            return;
        }
        handled_ = true;
        IXposedHookLoadPackage.LoadPackageParam lpp = new IXposedHookLoadPackage.LoadPackageParam();

        lpp.isFirstApplication = false;
        lpp.packageName = TweakUtil.currentPackageName();
        lpp.processName = TweakUtil.currentProcessName();
        lpp.classLoader = context.getClassLoader();
        lpp.appInfo = context.getApplicationInfo();
        //JavaTweakBridge.writeToLogcat(Log.INFO, ReflectUtil.peekObject(context));

        for (int i = 0; i < modules_.length; i++) {
            try {
                modules_[i].handleLoadPackage(lpp);
            } catch (Throwable e) {
                JavaTweakBridge.writeToLogcat(Log.ERROR, "module: %s, handleLoadPackage: %s", modules_[i].getClass().getSimpleName(), e);
            }
        }
    }
}
