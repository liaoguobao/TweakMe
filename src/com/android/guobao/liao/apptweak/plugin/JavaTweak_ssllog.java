package com.android.guobao.liao.apptweak.plugin;

import android.util.Log;

import com.android.guobao.liao.apptweak.JavaTweakBridge;
import com.android.guobao.liao.apptweak.JavaTweakHook;
import com.android.guobao.liao.apptweak.JavaTweakPlugin;
import com.android.guobao.liao.apptweak.util.*;

public class JavaTweak_ssllog extends JavaTweakPlugin {
    protected void defineJavaClass(Class<?> clazz) {
        String name = clazz.getName();
        if (name.equals("okhttp3.internal.http.CallServerInterceptor") || name.equals("okhttp3.OkHttpClient")) {
            JavaTweakBridge.hookJavaMethod(clazz.getClassLoader(), "okhttp3.internal.http.CallServerInterceptor", "(okhttp3.Interceptor$Chain)okhttp3.Response", new JavaTweakHook(true) {
                protected void beforeHookedMethod(Object thiz, Object[] args) {
                    JavaTweak_LogHelper.printOkHttpRequest(thiz, ReflectUtil.getObjectField(args[0], "okhttp3.Request"));
                }

                protected void afterHookedMethod(Object thiz, Object[] args) {
                    JavaTweak_LogHelper.printOkHttpResponse(thiz, getResult());
                }
            });
        }
    }
}

class JavaTweak_LogHelper {
    static public Object printOkHttpRequest(Object thiz, Object request) {
        if (request == null) {
            return request;
        }
        String method = ReflectUtil.getObjectField(request, "java.lang.String").toString();
        String url = ReflectUtil.getObjectField(request, "okhttp3.HttpUrl").toString();
        String headers = ReflectUtil.getObjectField(request, "okhttp3.Headers").toString();
        Object body = ReflectUtil.getObjectField(request, "okhttp3.RequestBody");

        String head = "";
        head += method + " " + url.substring(url.indexOf('/', url.indexOf("://") + 3)) + " " + "HTTP/1.1" + "\r\n";
        head += headers.replaceAll("\n", "\r\n") + "\r\n";
        JavaTweakBridge.writeToLogcat(Log.INFO, "OKHTTP_write: key = 0x%08X, headlen = %d, head = \r\n{\r\n%s\r\n}\r\n", thiz.hashCode(), head.length(), head);

        byte[] bodydata = null;
        if (body != null) {
            Class<?> Buffer = ReflectUtil.classForName(request.getClass().getClassLoader(), "okio.Buffer");
            Object buffer = ReflectUtil.newClassInstance(Buffer);

            ReflectUtil.callObjectMethod(body, "(okio.BufferedSink)void", buffer); //writeTo
            bodydata = (byte[]) ReflectUtil.callObjectMethod(buffer, "()byte[]"); //readByteArray

            Object type = ReflectUtil.callObjectMethod(body, "()okhttp3.MediaType"); //contentType
            body = ReflectUtil.callClassMethod(body.getClass(), "(okhttp3.MediaType,byte[])okhttp3.RequestBody", type, bodydata); //create
            ReflectUtil.setObjectField(request, "okhttp3.RequestBody", body);
        }
        byte[] infdata = ZlibUtil.inflate(bodydata);
        bodydata = (infdata != null ? infdata : bodydata);
        bodydata = (bodydata != null ? bodydata : "".getBytes());
        JavaTweakBridge.writeToLogcat(Log.INFO, "OKHTTP_write: key = 0x%08X, bodylen = %d, isgzip = %s, body = \r\n{\r\n%s\r\n}\r\n", thiz.hashCode(), bodydata == null ? 0 : bodydata.length, infdata != null, new String(bodydata));
        return request;
    }

    static public Object printOkHttpResponse(Object thiz, Object response) {
        if (response == null) {
            return response;
        }
        String protocol = ReflectUtil.getObjectField(response, "okhttp3.Protocol").toString();
        String code = ReflectUtil.getObjectField(response, ".int").toString();
        String message = ReflectUtil.getObjectField(response, "java.lang.String").toString();
        String headers = ReflectUtil.getObjectField(response, "okhttp3.Headers").toString();
        Object body = ReflectUtil.getObjectField(response, "okhttp3.ResponseBody");

        String head = "";
        head += protocol + " " + code + " " + message + "\r\n";
        head += headers.replaceAll("\n", "\r\n") + "\r\n";
        JavaTweakBridge.writeToLogcat(Log.INFO, "OKHTTP_read: key = 0x%08X, headlen = %d, head = \r\n{\r\n%s\r\n}\r\n", thiz.hashCode(), head.length(), head);

        byte[] bodydata = null;
        if (body != null) {
            Object source = ReflectUtil.callObjectMethod(body, "()okio.BufferedSource"); //source
            ReflectUtil.callObjectMethod(source, "(long)boolean", Long.MAX_VALUE); //request

            Object buffer = ReflectUtil.callObjectMethod(source, "()okio.Buffer"); //buffer
            bodydata = (byte[]) ReflectUtil.callObjectMethod(buffer, "()byte[]"); //readByteArray

            Object type = ReflectUtil.callObjectMethod(body, "()okhttp3.MediaType"); //contentType
            body = ReflectUtil.callClassMethod(body.getClass(), "(okhttp3.MediaType,byte[])okhttp3.ResponseBody", type, bodydata); //create
            ReflectUtil.setObjectField(response, "okhttp3.ResponseBody", body); //body
        }
        byte[] infdata = ZlibUtil.inflate(bodydata);
        bodydata = (infdata != null ? infdata : bodydata);
        bodydata = (bodydata != null ? bodydata : "".getBytes());
        JavaTweakBridge.writeToLogcat(Log.INFO, "OKHTTP_read: key = 0x%08X, bodylen = %d, isgzip = %s, body = \r\n{\r\n%s\r\n}\r\n", thiz.hashCode(), bodydata == null ? 0 : bodydata.length, infdata != null, new String(bodydata));
        return response;
    }
}
