package com.android.guobao.liao.apptweak;

public abstract class JavaTweakPlugin { //所有插件类都必须继承此类
    protected void loadDexFile(String dex) {
        //此回调函数主要用来hook系统类方法，也可以做一些初始化操作，比如加载native动态库
    }

    protected void defineClassLoader(ClassLoader loader) {
        //此回调函数主要用来hook动态延迟加载的dex中的方法，或者defineJavaClass中没有回调的类方法
    }

    protected void defineJavaClass(Class<?> clazz) {
        //此回调函数主要用来hook非系统类方法
    }
}
