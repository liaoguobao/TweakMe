//somain.cpp

#include <jni.h>
#include <dlfcn.h>
#include <android/log.h>

#include "tweakbridge.h"

#define ANDROID_LOG_TAG "WriteToLogcat"

//[>=7.0]void Instrumentation::UpdateMethodsCodeImpl(    ArtMethod* method, const void* quick_code);
static void  new_ART_Instrumentation_UpdateMethodsCodeImpl (void *thiz, void *method, void *quick_code);
static void(*old_ART_Instrumentation_UpdateMethodsCodeImpl)(void *thiz, void *method, void *quick_code) = 0;

extern "C" JNIEXPORT int JNI_OnLoad(JavaVM *vm, void *reserved)
{
    TweakBridge_init(vm);

    void *libart = TweakBridge_loadLib("libart.so"); //用dlopen方式获取句柄，在高版本中会直接返回null，低版本中即使有值返回，也会有namespace的限制，导致dlsym调用返回null
    __android_log_print(ANDROID_LOG_INFO, ANDROID_LOG_TAG, "nativeLoadLib: libname = libart.so, handle = %p\r\n", libart);

    void *symbol = dlsym(libart, "_ZN3art15instrumentation15Instrumentation21UpdateMethodsCodeImplEPNS_9ArtMethodEPKv");
    *(void **)&old_ART_Instrumentation_UpdateMethodsCodeImpl = TweakBridge_hookSymbol(symbol, (void *)new_ART_Instrumentation_UpdateMethodsCodeImpl);
    __android_log_print(ANDROID_LOG_INFO, ANDROID_LOG_TAG, "nativeHookSymbol: symbol = %p, detour = %p, origin = %p\r\n", symbol, new_ART_Instrumentation_UpdateMethodsCodeImpl, old_ART_Instrumentation_UpdateMethodsCodeImpl);
    return JNI_VERSION_1_4;
}

static void  new_ART_Instrumentation_UpdateMethodsCodeImpl (void *thiz, void *method, void *quick_code)
{
    old_ART_Instrumentation_UpdateMethodsCodeImpl(thiz, method, quick_code);

    //有些加固会禁止__android_log_print函数打印日志，如果发现此条日志没有输出请换用JavaTweakBridge_writeToLogcat函数。
    __android_log_print(ANDROID_LOG_INFO, ANDROID_LOG_TAG, "UpdateMethodsCodeImpl: thiz = %p, method = %p, quick_code = %p\r\n", thiz, method, quick_code);
    //TweakBridge_printLog(ANDROID_LOG_INFO, ANDROID_LOG_TAG, "UpdateMethodsCodeImpl: thiz = %p, method = %p, quick_code = %p\r\n", thiz, method, quick_code);
}
