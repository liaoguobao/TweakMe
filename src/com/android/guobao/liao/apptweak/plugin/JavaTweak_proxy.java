package com.android.guobao.liao.apptweak.plugin;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import android.util.Log;

import com.android.guobao.liao.apptweak.JavaTweakBridge;
import com.android.guobao.liao.apptweak.util.*;

@SuppressWarnings({ "unused" })
public class JavaTweak_proxy {
    static public void loadDexFile(String dex) {
        JavaTweak_ProxyHelper.proxyIsOk();
        JavaTweakBridge.hookJavaMethod("javax.net.ssl.SSLContext", "init");
        JavaTweakBridge.hookJavaMethod("java.net.URL", "openConnection()");
    }

    static public void defineJavaClass(Class<?> clazz) {
        String name = clazz.getName();
        if (name.equals("okhttp3.internal.http.RetryAndFollowUpInterceptor") || name.equals("okhttp3.OkHttpClient")) {
            JavaTweakBridge.hookJavaMethod(clazz.getClassLoader(), "okhttp3.internal.http.RetryAndFollowUpInterceptor", "(okhttp3.Interceptor$Chain)okhttp3.Response", "RetryAndFollowUpInterceptor");
        }
        if (name.equals("org.apache.http.impl.client.DefaultRequestDirector") || name.equals("org.apache.http.impl.client.DefaultHttpClient")) {
            JavaTweakBridge.hookJavaMethod(clazz.getClassLoader(), "org.apache.http.impl.client.DefaultRequestDirector", "(org.apache.http.HttpHost,org.apache.http.HttpRequest,org.apache.http.protocol.HttpContext)org.apache.http.HttpResponse", "execute");
        }
    }

    static private Object RetryAndFollowUpInterceptor(Object thiz, Object chain) throws Exception {
        //JavaTweakBridge.writeToLogcat(Log.INFO, Log.getStackTraceString(new Throwable()));
        Object hr = JavaTweakBridge.nologOriginalMethod(JavaTweak_ProxyHelper.modifyOkHttpClient(thiz), chain);
        return TweakUtil.returnWithException(hr, hr == null ? new IOException(thiz.toString()) : null);
    }

    static private Object openConnection(Object thiz) throws Exception {
        JavaTweakBridge.writeToLogcat(Log.INFO, "proxy: url: %s", thiz);
        Object hr = !JavaTweak_ProxyHelper.proxyIsOk() ? JavaTweakBridge.nologOriginalMethod(thiz) : ReflectUtil.callObjectMethod(thiz, "openConnection(java.net.Proxy)", JavaTweak_ProxyHelper.newProxy());
        return TweakUtil.returnWithException(hr, hr == null ? new IOException(thiz.toString()) : null);
    }

    static private void init(Object thiz, Object km, Object[] tm, Object random) {
        Object[] tms = new TrustManager[] { new JavaTweak_X509TrustManager() };
        JavaTweakBridge.writeToLogcat(Log.INFO, "proxy: init: %s--->%s", tm == null ? null : tm[0], tms[0]);
        JavaTweakBridge.nologOriginalMethod(thiz, km, tms, random);
    }

    static private Object execute(Object thiz, Object target, Object request, Object context) throws Exception {
        //JavaTweakBridge.writeToLogcat(Log.INFO, Log.getStackTraceString(new Throwable()));
        Object hr = JavaTweakBridge.nologOriginalMethod(JavaTweak_ProxyHelper.modifyApacheHttpClient(thiz, request), target, request, context);
        return TweakUtil.returnWithException(hr, hr == null ? new IOException(thiz.toString()) : null);
    }
}

@SuppressWarnings({ "unchecked" })
class JavaTweak_ProxyHelper {
    static private String proxyhost = "10.108.3.195|192.168.1.108"; //多个ip地址用|隔开
    static private int proxyport = 8888;
    static private int proxyok = -1;

    static public void modifyCertificatePinner(Object client) {
        Object cp = ReflectUtil.getObjectField(client, "okhttp3.CertificatePinner");
        Set<Object> sp = (Set<Object>) ReflectUtil.getObjectField(cp, "java.util.Set<okhttp3.CertificatePinner$Pin>");

        if (!sp.isEmpty()) {
            JavaTweakBridge.writeToLogcat(Log.INFO, "proxy: pinner: %s--->empty", sp);
            sp.clear();
        }
    }

    static public void modifyHostnameVerifier(Object client) {
        Object hv = ReflectUtil.getObjectField(client, "javax.net.ssl.HostnameVerifier");

        if (!JavaTweak_HostnameVerifier.class.isInstance(hv)) {
            Object verify = new JavaTweak_HostnameVerifier();
            JavaTweakBridge.writeToLogcat(Log.INFO, "proxy: verifier: %s--->%s", hv, verify);
            ReflectUtil.setObjectField(client, "javax.net.ssl.HostnameVerifier", verify);
        }
    }

    static public void modifyProtocol(Object client) {
        List<Object> protos = (List<Object>) ReflectUtil.getObjectField(client, "java.util.List<okhttp3.Protocol>");

        if (protos.size() != 1 || !protos.get(0).toString().equals("http/1.1")) {
            List<Object> proto = new ArrayList<Object>();
            Class<?> Protocol = ReflectUtil.classForName("okhttp3.Protocol", false, client.getClass().getClassLoader());
            proto.add(ReflectUtil.callClassMethod(Protocol, "(java.lang.String)okhttp3.Protocol", "http/1.1"));

            JavaTweakBridge.writeToLogcat(Log.INFO, "proxy: protocol: %s--->%s", protos, proto);
            ReflectUtil.setObjectField(client, "java.util.List<okhttp3.Protocol>", Collections.unmodifiableList(proto));
        }
    }

    static public Object modifyOkHttpClient(Object thiz) {
        Object client = ReflectUtil.getObjectField(thiz, "okhttp3.OkHttpClient");

        setHttpClientProxy(client, "OkHttpClient");
        modifySSLSocketFactory(client, "OkHttpClient");

        modifyProtocol(client);
        modifyHostnameVerifier(client);
        modifyCertificatePinner(client);
        return thiz;
    }

    static public Object modifyApacheHttpClient(Object thiz, Object request) {
        setHttpClientProxy(request, "ApacheHttpClient");
        modifySSLSocketFactory(thiz, "ApacheHttpClient");
        return thiz;
    }

    static public boolean proxyIsOk() {
        if (proxyok != -1) {
            return proxyok == 1;
        }
        proxyok = 0;
        String[] hosts = proxyhost.split("\\|");

        for (int i = 0; i < hosts.length && !hosts[i].equals(""); i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String host = Thread.currentThread().getName();
                    try {
                        Socket s = new Socket(host, proxyport);
                        s.close();
                        JavaTweakBridge.writeToLogcat(Log.INFO, "proxy: route: %s@%d: ok", host, proxyport);

                        proxyhost = host;
                        proxyok = 1;
                    } catch (Exception e) {
                        JavaTweakBridge.writeToLogcat(Log.INFO, "proxy: route: %s@%d: %s", host, proxyport, e);
                    }
                }
            }, hosts[i]).start();
        }
        return false;
    }

    static public Object newProxy() {
        Class<?> Proxy = ReflectUtil.classForName("java.net.Proxy");
        Class<?> Type = ReflectUtil.classForName("java.net.Proxy$Type");

        Object http = ReflectUtil.getClassField(Type, "HTTP");
        Object addr = new InetSocketAddress(proxyhost, proxyport);
        Object proxy = ReflectUtil.newClassInstance(Proxy, "(java.net.Proxy$Type,java.net.SocketAddress)", http, addr);
        return proxy;
    }

    static public void setHttpClientProxy(Object client, String hctype) {
        if (!proxyIsOk()) {
            return;
        }
        if (hctype.equals("ApacheHttpClient")) {
            Object params = ReflectUtil.callObjectMethod(client, "getParams");
            URI uri = (URI) ReflectUtil.callObjectMethod(client, "getURI");

            Class<?> HttpHost = ReflectUtil.classForName("org.apache.http.HttpHost", false, client.getClass().getClassLoader());
            Object proxy = ReflectUtil.newClassInstance(HttpHost, "(java.lang.String,int,java.lang.String)", proxyhost, proxyport, "http");
            Object target = ReflectUtil.newClassInstance(HttpHost, "(java.lang.String,int,java.lang.String)", uri.getHost(), uri.getPort(), uri.getScheme());

            Class<?> HttpRoute = ReflectUtil.classForName("org.apache.http.conn.routing.HttpRoute", false, client.getClass().getClassLoader());
            Object route = ReflectUtil.newClassInstance(HttpRoute, "(org.apache.http.HttpHost,java.net.InetAddress,org.apache.http.HttpHost,boolean)", target, null, proxy, uri.getScheme() == "https");

            ReflectUtil.callObjectMethod(params, "setParameter", /*ConnRoutePNames.FORCED_ROUTE*/"http.route.forced-route", route); //这一步必须设置
            ReflectUtil.callObjectMethod(params, "setParameter", /*ConnRoutePNames.DEFAULT_PROXY*/"http.route.default-proxy", proxy); //这一步可以不设置
        } else if (hctype.equals("OkHttpClient")) {
            ReflectUtil.setObjectField(client, "java.net.Proxy", newProxy());
        }
    }

    static public void modifySSLSocketFactory(Object thiz, String hctype) {
        try {
            if (hctype.equals("ApacheHttpClient")) {
                Object connManager = ReflectUtil.getObjectField(thiz, "connManager");
                Object schemeRegistry = ReflectUtil.callObjectMethod(connManager, "getSchemeRegistry");

                Object scheme = ReflectUtil.callObjectMethod(schemeRegistry, "get", "https");
                Object factory = ReflectUtil.callObjectMethod(scheme, "getSocketFactory");
                if (scheme == null || JavaTweak_SSLSocketFactory_apache.class.isInstance(factory)) {
                    return; //scheme为null代表走的http协议，可以直接返回
                }
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);

                Object sf = new JavaTweak_SSLSocketFactory_apache(trustStore);
                ReflectUtil.callObjectMethod(sf, "setHostnameVerifier", new Object[] { null }); //SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER
                Object sch = ReflectUtil.newClassInstance(scheme.getClass(), "(java.lang.String,org.apache.http.conn.scheme.SocketFactory,int)", "https", sf, 443);

                JavaTweakBridge.writeToLogcat(Log.INFO, "proxy: factory: %s--->%s", factory, sf);
                ReflectUtil.callObjectMethod(schemeRegistry, "register", sch);
            } else if (hctype.equals("OkHttpClient")) {
                Object factory = ReflectUtil.getObjectField(thiz, "javax.net.ssl.SSLSocketFactory");
                if (JavaTweak_SSLSocketFactory_okhttp.class.isInstance(factory)) {
                    return;
                }
                Object sf = new JavaTweak_SSLSocketFactory_okhttp();
                ReflectUtil.setObjectField(thiz, "javax.net.ssl.SSLSocketFactory", sf);
                JavaTweakBridge.writeToLogcat(Log.INFO, "proxy: factory: %s--->%s", factory, sf);

                Class<?> Cleaner = ReflectUtil.classForName("okhttp3.internal.tls.CertificateChainCleaner", true, thiz.getClass().getClassLoader());
                Class<?> Companion = ReflectUtil.classForName("okhttp3.internal.tls.CertificateChainCleaner$Companion", true, thiz.getClass().getClassLoader()); //如果是4.x版本，这里应该找这个类的内部类，因为接下来调用的get方法被实现在了内部类中

                Object xxx = ReflectUtil.getObjectField(thiz, "okhttp3.internal.tls.CertificateChainCleaner");
                Object cmp = Companion != null ? ReflectUtil.getClassField(Cleaner, "okhttp3.internal.tls.CertificateChainCleaner$Companion") : xxx;
                Object ccc = Companion != null ? ReflectUtil.callObjectMethod(cmp, "(javax.net.ssl.X509TrustManager)okhttp3.internal.tls.CertificateChainCleaner", new JavaTweak_X509TrustManager()) : ReflectUtil.callClassMethod(Cleaner, "(javax.net.ssl.X509TrustManager)okhttp3.internal.tls.CertificateChainCleaner", new JavaTweak_X509TrustManager()); //get

                ReflectUtil.setObjectField(thiz, "okhttp3.internal.tls.CertificateChainCleaner", ccc);
                JavaTweakBridge.writeToLogcat(Log.INFO, "proxy: chain: %s--->%s", xxx, ccc);
            }
        } catch (Exception e) {
            JavaTweakBridge.writeToLogcat(Log.ERROR, "proxy: modifySSLSocketFactory: %s", e);
        }
    }
}

class JavaTweak_X509TrustManager implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        JavaTweakBridge.writeToLogcat(Log.INFO, "proxy: checkClientTrusted: %s: %s", chain[0].getIssuerDN(), authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        JavaTweakBridge.writeToLogcat(Log.INFO, "proxy: checkServerTrusted: %s: %s", chain[0].getIssuerDN(), authType);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}

class JavaTweak_HostnameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String hostname, SSLSession session) {
        JavaTweakBridge.writeToLogcat(Log.INFO, "proxy: verify: %s: %s", hostname, session);
        return true;
    }
}

class JavaTweak_SSLSocketFactory_apache extends org.apache.http.conn.ssl.SSLSocketFactory {
    private javax.net.ssl.SSLContext ctx;
    private javax.net.ssl.SSLSocketFactory sf;

    public JavaTweak_SSLSocketFactory_apache(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(truststore);
        ctx = SSLContext.getInstance("TLS");
        ctx.init(null, null, null); //TrustManager[] tms = new TrustManager[] { new JavaTweak_X509TrustManager() };
        sf = ctx.getSocketFactory();
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        return sf.createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sf.createSocket();
    }
}

class JavaTweak_SSLSocketFactory_okhttp extends javax.net.ssl.SSLSocketFactory {
    private javax.net.ssl.SSLContext ctx;
    private javax.net.ssl.SSLSocketFactory sf;

    public JavaTweak_SSLSocketFactory_okhttp() throws NoSuchAlgorithmException, KeyManagementException {
        ctx = SSLContext.getInstance("TLS");
        ctx.init(null, null, null); //TrustManager[] tms = new TrustManager[] { new JavaTweak_X509TrustManager() };
        sf = ctx.getSocketFactory();
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return sf.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return sf.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return sf.createSocket(s, host, port, autoClose);
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return sf.createSocket(host, port);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return sf.createSocket(host, port, localHost, localPort);
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return sf.createSocket(host, port);
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return sf.createSocket(address, port, localAddress, localPort);
    }
}
