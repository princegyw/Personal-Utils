LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := JNIfunctions
LOCAL_SRC_FILES := jni_JNIfunctions.cpp

#dynamically link log
LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)
