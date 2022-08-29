package com.android.guobao.liao.apptweak.plugin;

import java.io.IOException;
import android.util.Log;

import com.android.guobao.liao.apptweak.JavaTweakBridge;
import com.android.guobao.liao.apptweak.util.*;

@SuppressWarnings({ "unused" })
public class JavaTweak_ssllog {
    static public void defineJavaClass(Class<?> clazz) {
        String name = clazz.getName();
        if (name.equals("okhttp3.internal.http.CallServerInterceptor") || name.equals("okhttp3.OkHttpClient")) {
            JavaTweakBridge.hookJavaMethod(clazz.getClassLoader(), "okhttp3.internal.http.CallServerInterceptor", "(okhttp3.Interceptor$Chain)okhttp3.Response", "CallServerInterceptor");
        }
    }

    static private Object CallServerInterceptor(Object thiz, Object chain) throws Exception {
        Object req = JavaTweak_LogHelper.printOkHttpRequest(thiz, ReflectUtil.getObjectField(chain, "okhttp3.Request"));
        Object rsp = JavaTweak_LogHelper.printOkHttpResponse(thiz, JavaTweakBridge.nologOriginalMethod(thiz, chain));
        return TweakUtil.returnWithException(rsp, rsp == null ? new IOException(thiz.toString()) : null);
    }
}

class JavaTweak_LogHelper {
    static public Object printOkHttpRequest(Object thiz, Object request) {
        String method = ReflectUtil.getObjectField(request, "java.lang.String").toString();
        String url = ReflectUtil.getObjectField(request, "okhttp3.HttpUrl").toString();
        String headers = ReflectUtil.getObjectField(request, "okhttp3.Headers").toString();
        Object body = ReflectUtil.getObjectField(request, "okhttp3.RequestBody");

        String head = "";
        head += method + " " + url.substring(url.indexOf('/', url.indexOf("://") + 3)) + " " + "HTTP/1.1" + "\r\n";
        head += headers.replaceAll("\n", "\r\n") + "\r\n";
        JavaTweakBridge.writeToLogcat(Log.INFO, "OKHTTP_write: key = 0x%08X, headlen = %d, head = \r\n{\r\n%s\r\n}\r\n", thiz.hashCode(), head.length(), head);

        boolean ishex = false;
        byte[] bodydata = null;
        if (body != null) {
            Class<?> Buffer = ReflectUtil.classForName("okio.Buffer", true, request.getClass().getClassLoader());
            Object buffer = ReflectUtil.newClassInstance(Buffer);

            ReflectUtil.callObjectMethod(body, "(okio.BufferedSink)void", buffer); //writeTo
            bodydata = (byte[]) ReflectUtil.callObjectMethod(buffer, "()byte[]"); //readByteArray

            Object type = ReflectUtil.callObjectMethod(body, "()okhttp3.MediaType"); //contentType
            body = ReflectUtil.callClassMethod(body.getClass(), "(okhttp3.MediaType,byte[])okhttp3.RequestBody", type, bodydata); //create
            ReflectUtil.setObjectField(request, "okhttp3.RequestBody", body);

            String ct = type.toString();
            if (ZlibUtil.isZip(bodydata) || ct.startsWith("image/") || ct.startsWith("audio/") || ct.startsWith("video/") || ct.startsWith("text/") || ct.startsWith("application/json") || ct.startsWith("application/x-msdownload") || ct.startsWith("application/octet-stream")) {
                ishex = true;
            }
        }
        byte[] infdata = ZlibUtil.inflate(bodydata);
        bodydata = (infdata != null ? infdata : bodydata);
        JavaTweakBridge.writeToLogcat(Log.INFO, "OKHTTP_write: key = 0x%08X, bodylen = %d, isgzip = %s, body = \r\n{\r\n%s\r\n}\r\n", thiz.hashCode(), bodydata == null ? 0 : bodydata.length, infdata != null, ishex ? new String(bodydata) : StringUtil.hexToVisible(bodydata));
        return request;
    }

    static public Object printOkHttpResponse(Object thiz, Object response) {
        String protocol = ReflectUtil.getObjectField(response, "okhttp3.Protocol").toString();
        String code = ReflectUtil.getObjectField(response, ".int").toString();
        String message = ReflectUtil.getObjectField(response, "java.lang.String").toString();
        String headers = ReflectUtil.getObjectField(response, "okhttp3.Headers").toString();
        Object body = ReflectUtil.getObjectField(response, "okhttp3.ResponseBody");

        String head = "";
        head += protocol + " " + code + " " + message + "\r\n";
        head += headers.replaceAll("\n", "\r\n") + "\r\n";
        JavaTweakBridge.writeToLogcat(Log.INFO, "OKHTTP_read: key = 0x%08X, headlen = %d, head = \r\n{\r\n%s\r\n}\r\n", thiz.hashCode(), head.length(), head);

        boolean ishex = false;
        byte[] bodydata = null;
        if (body != null) {
            Object source = ReflectUtil.callObjectMethod(body, "()okio.BufferedSource"); //source
            ReflectUtil.callObjectMethod(source, "(long)boolean", Long.MAX_VALUE); //request

            Object buffer = ReflectUtil.callObjectMethod(source, "()okio.Buffer"); //buffer
            bodydata = (byte[]) ReflectUtil.callObjectMethod(buffer, "()byte[]"); //readByteArray

            Object type = ReflectUtil.callObjectMethod(body, "()okhttp3.MediaType"); //contentType
            body = ReflectUtil.callClassMethod(body.getClass(), "(okhttp3.MediaType,byte[])okhttp3.ResponseBody", type, bodydata); //create
            ReflectUtil.setObjectField(response, "okhttp3.ResponseBody", body); //body

            String ct = type.toString();
            if (ZlibUtil.isZip(bodydata) || ct.startsWith("image/") || ct.startsWith("audio/") || ct.startsWith("video/") || ct.startsWith("text/") || ct.startsWith("application/json") || ct.startsWith("application/x-msdownload") || ct.startsWith("application/octet-stream")) {
                ishex = true;
            }
        }
        byte[] infdata = ZlibUtil.inflate(bodydata);
        bodydata = (infdata != null ? infdata : bodydata);
        JavaTweakBridge.writeToLogcat(Log.INFO, "OKHTTP_read: key = 0x%08X, bodylen = %d, isgzip = %s, body = \r\n{\r\n%s\r\n}\r\n", thiz.hashCode(), bodydata == null ? 0 : bodydata.length, infdata != null, ishex ? new String(bodydata) : StringUtil.hexToVisible(bodydata));
        return response;
    }
}
