package com.android.guobao.liao.apptweak.xposed;

import android.content.pm.ApplicationInfo;

public abstract interface IXposedHookLoadPackage {
    public final class LoadPackageParam {
        public String packageName;
        public String processName;
        public ClassLoader classLoader;
        public ApplicationInfo appInfo;
        public boolean isFirstApplication;
    }

    public abstract void handleLoadPackage(LoadPackageParam paramLoadPackageParam) throws Throwable;
}