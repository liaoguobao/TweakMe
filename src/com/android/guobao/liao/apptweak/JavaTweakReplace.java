package com.android.guobao.liao.apptweak;

import android.util.Log;

public abstract class JavaTweakReplace extends JavaTweakHook {
    public JavaTweakReplace() {
        super();
    }

    public JavaTweakReplace(boolean nolog) {
        super(nolog);
    }

    public JavaTweakReplace(String name) {
        super(name);
    }

    public JavaTweakReplace(boolean nolog, String name) {
        super(nolog, name);
    }

    @Override
    protected final void beforeHookedMethod(Object thiz, Object[] args) {
        try {
            setResult(replaceHookedMethod(thiz, args));
        } catch (Throwable e) {
            JavaTweakBridge.writeToLogcat(Log.ERROR, "replaceHookedMethod: %s: %s", getBackup(), e);
        }
    }

    @Override
    protected final void afterHookedMethod(Object thiz, Object[] args) {
    }

    protected abstract Object replaceHookedMethod(Object thiz, Object[] args);
}
