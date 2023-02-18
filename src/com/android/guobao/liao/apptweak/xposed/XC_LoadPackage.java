package com.android.guobao.liao.apptweak.xposed;

import android.content.pm.ApplicationInfo;

public abstract class XC_LoadPackage implements IXposedHookLoadPackage {
    public static final class LoadPackageParam {
        public String packageName;
        public String processName;
        public ClassLoader classLoader;
        public ApplicationInfo appInfo;
        public boolean isFirstApplication;
    }
}
