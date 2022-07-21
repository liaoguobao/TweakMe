
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := sodemo

LOCAL_CFLAGS += -fvisibility=hidden

LOCAL_SRC_FILES := \
	../somain.cpp \
	../tweakbridge.cpp \

LOCAL_LDLIBS := \
	-llog \

include $(BUILD_SHARED_LIBRARY)
