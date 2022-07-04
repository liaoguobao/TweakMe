//tweakbridge.cpp

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>

#include "tweakbridge.h"

static JavaVM  * g_vm = 0;
static jclass    g_bridge = 0;
static jmethodID g_nativeHookSymbol = 0;
static jmethodID g_nativeLoadLib = 0;
static jmethodID g_nativePrintLog = 0;

int  TweakBridge_init(JavaVM *vm)
{
    g_vm = vm;

    JNIEnv *env = 0;
    g_vm->GetEnv((void **)&env, JNI_VERSION_1_4);

    jclass bridge = env->FindClass("com/android/guobao/liao/apptweak/JavaTweakBridge");
    g_bridge = (jclass)env->NewGlobalRef(bridge);
    env->DeleteLocalRef(bridge);

    g_nativeHookSymbol = env->GetStaticMethodID(g_bridge, "nativeHookSymbol", "(JJ)J");
    g_nativeLoadLib = env->GetStaticMethodID(g_bridge, "nativeLoadLib", "(Ljava/lang/String;)J");
    g_nativePrintLog = env->GetStaticMethodID(g_bridge, "nativePrintLog", "(IILjava/lang/String;Ljava/lang/String;)V");
    return 0;
}

void *TweakBridge_loadLib(const char *libname)
{
    JNIEnv *env = 0;
    g_vm->GetEnv((void **)&env, JNI_VERSION_1_4);

    jstring jlibname = env->NewStringUTF(libname);
    void *dlopen_handle = (void *)env->CallStaticLongMethod(g_bridge, g_nativeLoadLib, jlibname);

    env->DeleteLocalRef(jlibname);
    return dlopen_handle;
}

void *TweakBridge_hookSymbol(void *symbol, void *detour)
{
    JNIEnv *env = 0;
    g_vm->GetEnv((void **)&env, JNI_VERSION_1_4);

    void *origin_symbol = (void *)env->CallStaticLongMethod(g_bridge, g_nativeHookSymbol, (jlong)symbol, (jlong)detour);
    return origin_symbol;
}

int   TweakBridge_printLog(int prio, const char *tag, const char *format, ...)
{
    va_list args;
    va_start(args, format);
    int buflen = vsnprintf(0, 0, format, args) + 1;
    char *logbuf = (char *)malloc(buflen);
    vsnprintf(logbuf, buflen, format, args);
    va_end(args);

    JNIEnv *env = 0;
    g_vm->GetEnv((void **)&env, JNI_VERSION_1_4);

    jstring TAG = env->NewStringUTF(tag);
    jstring msg = env->NewStringUTF(logbuf);
    env->CallStaticVoidMethod(g_bridge, g_nativePrintLog, 0, prio, TAG, msg);

    env->DeleteLocalRef(msg);
    env->DeleteLocalRef(TAG);
    free(logbuf);
    return 0;
}
