//tweakbridge.h

#ifndef _TWEAK_BRIDGE_H_
#define _TWEAK_BRIDGE_H_

int   TweakBridge_init(JavaVM *vm);
void *TweakBridge_loadLib(const char *libname);
void *TweakBridge_hookSymbol(void *symbol, void *detour);
int   TweakBridge_printLog(int prio, const char *tag, const char *format, ...);

#endif
