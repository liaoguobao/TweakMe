package com.android.guobao.liao.apptweak;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import android.util.Log;

import com.android.guobao.liao.apptweak.util.*;

public abstract class JavaTweakHook {
    private Method backup_;
    private Object result_;
    private Throwable except_;
    private boolean return_;
    private boolean nolog_;
    private String name_;

    public JavaTweakHook() {
        this(false, "");
    }

    public JavaTweakHook(boolean nolog) {
        this(nolog, "");
    }

    public JavaTweakHook(String name) {
        this(false, name);
    }

    public JavaTweakHook(boolean nolog, String name) {
        backup_ = null;
        result_ = null;
        except_ = null;
        return_ = false;
        nolog_ = nolog;
        name_ = name == null ? "" : name;
    }

    protected void beforeHookedMethod(Object thiz, Object[] args) {
    }

    protected void afterHookedMethod(Object thiz, Object[] args) {
    }

    synchronized //此函数必须要同步执行，因为很有可能会在多线程中被频繁调用
    protected final Object handleHookedMethod(Object thiz, Object[] args) throws Throwable {
        result_ = null;
        except_ = null;
        return_ = false;
        if (!return_) {
            try {
                beforeHookedMethod(thiz, args);
            } catch (Throwable e) {
                JavaTweakBridge.writeToLogcat(Log.ERROR, "beforeHookedMethod: %s: %s", backup_, e);
            }
        }
        if (!return_) {
            try {
                result_ = backup_.invoke(thiz, args);
            } catch (InvocationTargetException e) {
                except_ = e.getCause();
                JavaTweakBridge.writeToLogcat(Log.WARN, "invoke: %s: %s", backup_, except_);
            }
        }
        if (!return_) {
            try {
                afterHookedMethod(thiz, args);
            } catch (Throwable e) {
                JavaTweakBridge.writeToLogcat(Log.ERROR, "afterHookedMethod: %s: %s", backup_, e);
            }
        }
        if (!nolog_) {
            JavaTweakBridge.writeToLogcat(Log.INFO, paramsToString(name_, backup_, result_, thiz, args));
        }
        if (except_ != null) {
            throw except_;
        }
        return result_;
    }

    private String paramsToString(String name, Method m, Object hr, Object thiz, Object[] args) {
        final int maxlen = 4096;
        Class<?> type = m.getReturnType();
        Class<?>[] types = m.getParameterTypes();

        String log = String.format("%s::%s%s->{\r\n", m.getDeclaringClass().getName(), m.getName(), !name.equals("") && !m.getName().equals(name) ? "@" + name : "");
        log += String.format("\t_this_ = %s->%s\r\n", m.getDeclaringClass().getName(), thiz);
        for (int i = 0; i < args.length; i++) {
            String byteArr = ((args[i] instanceof byte[]) ? StringUtil.hexToVisible(((byte[]) args[i]).length > maxlen ? Arrays.copyOf((byte[]) args[i], maxlen) : (byte[]) args[i]) : null);
            String objArr = ((args[i] instanceof Object[]) ? Arrays.asList((Object[]) args[i]).toString() : null);
            log += String.format("\tparam%d = %s->%s\r\n", i + 1, types[i].getName(), byteArr != null ? byteArr : (objArr != null ? objArr : args[i]));
        }
        String byteArr = ((hr instanceof byte[]) ? StringUtil.hexToVisible(((byte[]) hr).length > maxlen ? Arrays.copyOf((byte[]) hr, maxlen) : (byte[]) hr) : null);
        String objArr = ((hr instanceof Object[]) ? Arrays.asList((Object[]) hr).toString() : null);

        log += String.format("\treturn = %s->%s\r\n}\r\n", type.getName(), byteArr != null ? byteArr : (objArr != null ? objArr : hr));
        return log;
    }

    public Method getBackup() {
        return backup_;
    }

    public void setBackup(Method backup) {
        backup_ = backup;
    }

    public Object getResult() {
        return result_;
    }

    public void setResult(Object result) {
        result_ = result;
        except_ = null;
        return_ = true;
    }

    public Throwable getThrowable() {
        return except_;
    }

    public void setThrowable(Throwable except) {
        result_ = null;
        except_ = except;
        return_ = true;
    }

    public static JavaTweakHook nameLogHook(String name) {
        return new JavaTweakHook(name) {
        };
    }

    public static JavaTweakHook onlyLogHook() {
        return nameLogHook(null);
    }
}
