package com.android.guobao.liao.apptweak.plugin;

import android.util.Log;

import com.android.guobao.liao.apptweak.JavaTweakBridge;
import com.android.guobao.liao.apptweak.util.*;

@SuppressWarnings("unused")
public class JavaTweak_webview {
    static public void defineJavaClass(Class<?> clazz) {
        String name = clazz.getName();
        if (name.equals("android.webkit.WebView")) { //谷歌浏览器的实现类
            JavaTweakBridge.hookJavaMethod(clazz, "(android.content.Context,android.util.AttributeSet,int,int,java.util.Map,boolean)");
            return;
        }
        if (name.equals("android.webkit.WebViewClient")) { //谷歌浏览器的实现类
            JavaTweakBridge.hookJavaMethod(clazz, "onReceivedError(android.webkit.WebView,int,java.lang.String,java.lang.String)");
            JavaTweakBridge.hookJavaMethod(clazz, "onReceivedSslError(android.webkit.WebView,android.webkit.SslErrorHandler,android.net.http.SslError)");
            return;
        }
        if (name.equals("com.uc.webview.export.WebView")) { //阿里给第三方用的实现类
            JavaTweakBridge.hookJavaMethod(clazz, "(android.content.Context,android.util.AttributeSet,int,boolean,byte)", "WebViewAP");
            return;
        }
        if (name.equals("com.uc.webview.export.WebViewClient")) { //阿里给第三方用的实现类
            JavaTweakBridge.hookJavaMethod(clazz, "onReceivedError(com.uc.webview.export.WebView,int,java.lang.String,java.lang.String)", "onReceivedErrorAP");
            JavaTweakBridge.hookJavaMethod(clazz, "onReceivedSslError(com.uc.webview.export.WebView,com.uc.webview.export.SslErrorHandler,android.net.http.SslError)", "onReceivedSslErrorAP");
            return;
        }
        if (name.equals("com.alipay.mobile.nebulacore.web.H5WebView")) { //阿里自己用的实现类（旧）
            JavaTweakBridge.hookJavaMethod(clazz, "(android.app.Activity,com.alipay.mobile.h5container.api.H5Page,android.os.Bundle,com.alipay.mobile.nebula.webview.APWebView,com.alipay.mobile.nebula.webview.APWebViewListener)", "WebViewH5");
            return;
        }
        if (name.equals("com.alipay.mobile.nebulacore.web.H5WebViewClient")) { //阿里自己用的实现类（旧）
            JavaTweakBridge.hookJavaMethod(clazz, "onReceivedError(com.alipay.mobile.nebula.webview.APWebView,int,java.lang.String,java.lang.String)", "onReceivedErrorH5");
            JavaTweakBridge.hookJavaMethod(clazz, "onReceivedSslError(com.alipay.mobile.nebula.webview.APWebView,com.alipay.mobile.nebula.webview.APSslErrorHandler,android.net.http.SslError)", "onReceivedSslErrorH5");
            return;
        }
        if (name.equals("com.alipay.mobile.nebulauc.impl.UCWebView")) { //阿里自己用的实现类（新）
            JavaTweakBridge.hookJavaMethod(clazz, "(android.content.Context,com.alipay.mobile.nebulauc.impl.UCWebView$1)", "WebViewUC");
            return;
        }
        if (name.equals("com.alipay.mobile.nebulauc.impl.UCWebViewClient")) { //阿里自己用的实现类（新）
            JavaTweakBridge.hookJavaMethod(clazz, "onReceivedError(com.uc.webview.export.WebView,int,java.lang.String,java.lang.String)", "onReceivedErrorUC");
            JavaTweakBridge.hookJavaMethod(clazz, "onReceivedSslError(com.uc.webview.export.WebView,com.uc.webview.export.SslErrorHandler,android.net.http.SslError)", "onReceivedSslErrorUC");
            return;
        }
    }

    static private void WebView(Object thiz, Object context, Object attrs, int defStyleAttr, int defStyleRes, Object javaScriptInterfaces, boolean privateBrowsing) {
        JavaTweakBridge.writeToLogcat(Log.INFO, "webview: WebView: %s", context);
        JavaTweakBridge.nologOriginalMethod(thiz, context, attrs, defStyleAttr, defStyleRes, javaScriptInterfaces, privateBrowsing);
        ReflectUtil.callClassMethod(thiz.getClass(), "setWebContentsDebuggingEnabled(boolean)", true);
    }

    static private void onReceivedError(Object thiz, Object view, int errorCode, String description, String failingUrl) {
        JavaTweakBridge.writeToLogcat(Log.INFO, "webview: onReceivedError: %d: %s: %s", errorCode, description, failingUrl);
    }

    static private void onReceivedSslError(Object thiz, Object view, Object handler, Object error) {
        JavaTweakBridge.writeToLogcat(Log.INFO, "webview: onReceivedSslError: %s", error);
        ReflectUtil.callObjectMethod(handler, "proceed");
    }

    static private void WebViewAP(Object thiz, Object context, Object attrs, int i, boolean z, byte b) {
        JavaTweakBridge.writeToLogcat(Log.INFO, "webview: WebViewAP: %s", context);
        JavaTweakBridge.nologOriginalMethod(thiz, context, attrs, i, z, b);
        ReflectUtil.callClassMethod(thiz.getClass(), "setWebContentsDebuggingEnabled(boolean)", true);
    }

    static private void onReceivedErrorAP(Object thiz, Object view, int errorCode, String description, String failingUrl) {
        JavaTweakBridge.writeToLogcat(Log.INFO, "webview: onReceivedErrorAP: %d: %s: %s", errorCode, description, failingUrl);
    }

    static private void onReceivedSslErrorAP(Object thiz, Object view, Object handler, Object error) {
        JavaTweakBridge.writeToLogcat(Log.INFO, "webview: onReceivedSslErrorAP: %s", error);
        ReflectUtil.callObjectMethod(handler, "proceed");
    }

    static private void WebViewH5(Object thiz, Object activity, Object page, Object bundle, Object view, Object listener) {
        JavaTweakBridge.writeToLogcat(Log.INFO, "webview: WebViewH5: %s", page);
        JavaTweakBridge.nologOriginalMethod(thiz, activity, page, bundle, view, listener);
        ReflectUtil.callClassMethod(thiz.getClass(), "setWebContentsDebuggingEnabled(boolean)", true);
    }

    static private void onReceivedErrorH5(Object thiz, Object view, int errorCode, String description, String failingUrl) {
        JavaTweakBridge.writeToLogcat(Log.INFO, "webview: onReceivedErrorH5: %d: %s: %s", errorCode, description, failingUrl);
    }

    static private void onReceivedSslErrorH5(Object thiz, Object view, Object handler, Object error) {
        JavaTweakBridge.writeToLogcat(Log.INFO, "webview: onReceivedSslErrorH5: %s", error);
        ReflectUtil.callObjectMethod(handler, "proceed");
    }

    static private void WebViewUC(Object thiz, Object context, Object view) {
        JavaTweakBridge.writeToLogcat(Log.INFO, "webview: WebViewUC: %s", view);
        JavaTweakBridge.nologOriginalMethod(thiz, context, view);
        ReflectUtil.callClassMethod(thiz.getClass(), "setWebContentsDebuggingEnabled(boolean)", true);
    }

    static private void onReceivedErrorUC(Object thiz, Object view, int errorCode, String description, String failingUrl) {
        JavaTweakBridge.writeToLogcat(Log.INFO, "webview: onReceivedErrorUC: %d: %s: %s", errorCode, description, failingUrl);
    }

    static private void onReceivedSslErrorUC(Object thiz, Object view, Object handler, Object error) {
        JavaTweakBridge.writeToLogcat(Log.INFO, "webview: onReceivedSslErrorUC: %s", error);
        ReflectUtil.callObjectMethod(handler, "proceed");
    }
}
