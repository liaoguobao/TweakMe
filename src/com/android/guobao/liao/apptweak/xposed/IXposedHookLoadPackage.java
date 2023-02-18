package com.android.guobao.liao.apptweak.xposed;

import com.android.guobao.liao.apptweak.xposed.XC_LoadPackage.LoadPackageParam;

public abstract interface IXposedHookLoadPackage {
    public abstract void handleLoadPackage(LoadPackageParam paramLoadPackageParam) throws Throwable;
}