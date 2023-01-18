package com.android.guobao.liao.apptweak.plugin;

import android.util.Log;

import com.android.guobao.liao.apptweak.JavaTweakBridge;
import com.android.guobao.liao.apptweak.JavaTweakHook;
import com.android.guobao.liao.apptweak.JavaTweakPlugin;
import com.android.guobao.liao.apptweak.JavaTweakReplace;
import com.android.guobao.liao.apptweak.util.*;

public class JavaTweak_webview extends JavaTweakPlugin {
    protected void defineJavaClass(Class<?> clazz) {
        String name = clazz.getName();
        if (name.equals("android.webkit.WebView")) {
            JavaTweakBridge.hookJavaMethod(clazz, "(android.content.Context,android.util.AttributeSet,int,int,java.util.Map,boolean)", new JavaTweakHook(true) {
                protected void afterHookedMethod(Object thiz, Object[] args) {
                    JavaTweakBridge.writeToLogcat(Log.INFO, "webview: WebView: %s", args[0]);
                    //ReflectUtil.callClassMethod(thiz.getClass(), "setWebContentsDebuggingEnabled(boolean)", true);
                }
            });
            return;
        }
        if (name.equals("android.webkit.WebViewClient")) {
            JavaTweakBridge.hookJavaMethod(clazz, "onReceivedError(android.webkit.WebView,int,java.lang.String,java.lang.String)", new JavaTweakReplace(true) {
                protected Object replaceHookedMethod(Object thiz, Object[] args) {
                    JavaTweakBridge.writeToLogcat(Log.INFO, "webview: onReceivedError: %s: %s: %s", args[1], args[2], args[3]);
                    return null;
                }
            });
            JavaTweakBridge.hookJavaMethod(clazz, "onReceivedSslError(android.webkit.WebView,android.webkit.SslErrorHandler,android.net.http.SslError)", new JavaTweakReplace(true) {
                protected Object replaceHookedMethod(Object thiz, Object[] args) {
                    JavaTweakBridge.writeToLogcat(Log.INFO, "webview: onReceivedSslError: %s", args[2]);
                    ReflectUtil.callObjectMethod(args[1], "proceed");
                    return null;
                }
            });
            return;
        }
        if (name.equals("com.uc.webview.export.WebView")) {
            JavaTweakBridge.hookJavaMethod(clazz, "(android.content.Context,android.util.AttributeSet,int,boolean,byte)", new JavaTweakHook(true) {
                protected void afterHookedMethod(Object thiz, Object[] args) {
                    JavaTweakBridge.writeToLogcat(Log.INFO, "webview: WebViewAP: %s", args[0]);
                    //ReflectUtil.callClassMethod(thiz.getClass(), "setWebContentsDebuggingEnabled(boolean)", true);
                }
            });
            return;
        }
        if (name.equals("com.uc.webview.export.WebViewClient")) {
            JavaTweakBridge.hookJavaMethod(clazz, "onReceivedError(com.uc.webview.export.WebView,int,java.lang.String,java.lang.String)", new JavaTweakReplace(true) {
                protected Object replaceHookedMethod(Object thiz, Object[] args) {
                    JavaTweakBridge.writeToLogcat(Log.INFO, "webview: onReceivedErrorAP: %s: %s: %s", args[1], args[2], args[3]);
                    return null;
                }
            });
            JavaTweakBridge.hookJavaMethod(clazz, "onReceivedSslError(com.uc.webview.export.WebView,com.uc.webview.export.SslErrorHandler,android.net.http.SslError)", new JavaTweakReplace(true) {
                protected Object replaceHookedMethod(Object thiz, Object[] args) {
                    JavaTweakBridge.writeToLogcat(Log.INFO, "webview: onReceivedSslErrorAP: %s", args[2]);
                    ReflectUtil.callObjectMethod(args[1], "proceed");
                    return null;
                }
            });
            return;
        }
        if (name.equals("com.alipay.mobile.nebulacore.web.H5WebView")) {
            JavaTweakBridge.hookJavaMethod(clazz, "(android.app.Activity,com.alipay.mobile.h5container.api.H5Page,android.os.Bundle,com.alipay.mobile.nebula.webview.APWebView,com.alipay.mobile.nebula.webview.APWebViewListener)", new JavaTweakHook(true) {
                protected void afterHookedMethod(Object thiz, Object[] args) {
                    JavaTweakBridge.writeToLogcat(Log.INFO, "webview: WebViewH5: %s", args[1]);
                    //ReflectUtil.callClassMethod(thiz.getClass(), "setWebContentsDebuggingEnabled(boolean)", true);
                }
            });
            return;
        }
        if (name.equals("com.alipay.mobile.nebulacore.web.H5WebViewClient")) {
            JavaTweakBridge.hookJavaMethod(clazz, "onReceivedError(com.alipay.mobile.nebula.webview.APWebView,int,java.lang.String,java.lang.String)", new JavaTweakReplace(true) {
                protected Object replaceHookedMethod(Object thiz, Object[] args) {
                    JavaTweakBridge.writeToLogcat(Log.INFO, "webview: onReceivedErrorH5: %s: %s: %s", args[1], args[2], args[3]);
                    return null;
                }
            });
            JavaTweakBridge.hookJavaMethod(clazz, "onReceivedSslError(com.alipay.mobile.nebula.webview.APWebView,com.alipay.mobile.nebula.webview.APSslErrorHandler,android.net.http.SslError)", new JavaTweakReplace(true) {
                protected Object replaceHookedMethod(Object thiz, Object[] args) {
                    JavaTweakBridge.writeToLogcat(Log.INFO, "webview: onReceivedSslErrorH5: %s", args[2]);
                    ReflectUtil.callObjectMethod(args[1], "proceed");
                    return null;
                }
            });
            return;
        }
        if (name.equals("com.alipay.mobile.nebulauc.impl.UCWebView")) {
            JavaTweakBridge.hookJavaMethod(clazz, "(android.content.Context,com.alipay.mobile.nebulauc.impl.UCWebView$1)", new JavaTweakHook(true) {
                protected void afterHookedMethod(Object thiz, Object[] args) {
                    JavaTweakBridge.writeToLogcat(Log.INFO, "webview: WebViewUC: %s", args[1]);
                    //ReflectUtil.callClassMethod(thiz.getClass(), "setWebContentsDebuggingEnabled(boolean)", true);
                }
            });
            return;
        }
        if (name.equals("com.alipay.mobile.nebulauc.impl.UCWebViewClient")) {
            JavaTweakBridge.hookJavaMethod(clazz, "onReceivedError(com.uc.webview.export.WebView,int,java.lang.String,java.lang.String)", new JavaTweakReplace(true) {
                protected Object replaceHookedMethod(Object thiz, Object[] args) {
                    JavaTweakBridge.writeToLogcat(Log.INFO, "webview: onReceivedErrorUC: %s: %s: %s", args[1], args[2], args[3]);
                    return null;
                }
            });
            JavaTweakBridge.hookJavaMethod(clazz, "onReceivedSslError(com.uc.webview.export.WebView,com.uc.webview.export.SslErrorHandler,android.net.http.SslError)", new JavaTweakReplace(true) {
                protected Object replaceHookedMethod(Object thiz, Object[] args) {
                    JavaTweakBridge.writeToLogcat(Log.INFO, "webview: onReceivedSslErrorUC: %s", args[2]);
                    ReflectUtil.callObjectMethod(args[1], "proceed");
                    return null;
                }
            });
            return;
        }
    }
}
