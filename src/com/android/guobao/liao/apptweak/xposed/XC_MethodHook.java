package com.android.guobao.liao.apptweak.xposed;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import com.android.guobao.liao.apptweak.JavaTweakHook;

public abstract class XC_MethodHook extends JavaTweakHook {
    private MethodHookParam param;

    public XC_MethodHook() {
        super(true);
        param = new MethodHookParam();
    }

    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
    }

    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
    }

    @Override
    protected final void beforeHookedMethod(Object thiz, Object[] args) {
        param.thisObject = thiz;
        param.args = args;
        try {
            beforeHookedMethod(param);
        } catch (Throwable e) {
            setThrowable(e);
        }
    }

    @Override
    protected final void afterHookedMethod(Object thiz, Object[] args) {
        param.thisObject = thiz;
        param.args = args;
        try {
            afterHookedMethod(param);
        } catch (Throwable e) {
            setThrowable(e);
        }
    }

    @Override
    public final void setBackup(Method backup) {
        super.setBackup(backup);
        param.method = backup;
    }

    public final class MethodHookParam {
        public Member method;
        public Object thisObject;
        public Object[] args;

        public Object getResult() {
            return XC_MethodHook.this.getResult();
        }

        public void setResult(Object result) {
            XC_MethodHook.this.setResult(result);
        }

        public Throwable getThrowable() {
            return XC_MethodHook.this.getThrowable();
        }

        public void setThrowable(Throwable throwable) {
            XC_MethodHook.this.setThrowable(throwable);
        }

        public boolean hasThrowable() {
            return XC_MethodHook.this.getThrowable() != null;
        }

        public Object getResultOrThrowable() throws Throwable {
            Throwable tr = XC_MethodHook.this.getThrowable();
            if (tr != null) {
                throw tr;
            } else {
                return XC_MethodHook.this.getResult();
            }
        }
    }
}
