LOCAL_PATH := $(call my-dir)
include $(call all-subdir-makefiles)

include $(CLEAR_VARS)
LOCAL_MODULE    := libpurplemetaproc 
LOCAL_SRC_FILES := detectors/armeabi-v7a/libpurplemetaproc.a
LOCAL_EXPORT_C_INCLUDES :=$(LOCAL_PATH)/detectors
LOCAL_EXPORT_C_INCLUDES +=$(LOCAL_PATH)/detectors/sphinxbase-0.7/include
LOCAL_ARM_MODE := arm
STLPORT_FORCE_REBUILD := true
LOCAL_LDFLAGS := /home/ndisanze/workspace/IBeatBox/android-ndk-r8d/sources/cxx-stl/gnu-libstdc++/4.7/libs/armeabi/libgnustl_static.a
LOCAL_LDFLAGS += /home/ndisanze/workspace/IBeatBox/android-ndk-r8d/sources/cxx-stl/gnu-libstdc++/4.7/libs/armeabi/libsupc++.a
LOCAL_CPPFLAGS :=-lstdc++ -fexceptions -Wall  -frtti  -Wno-unused-parameter
LOCAL_ARM_MODE := arm
TARGET_PLATFORM:=android-14
TARGET_ARCH_ABI:=armeabi-v7a
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := libstk 
LOCAL_SRC_FILES := libstk.a
LOCAL_EXPORT_C_INCLUDES :=$(LOCAL_PATH)/include
LOCAL_ARM_MODE := arm
STLPORT_FORCE_REBUILD := true
LOCAL_LDFLAGS := /home/ndisanze/workspace/IBeatBox/android-ndk-r8d/sources/cxx-stl/gnu-libstdc++/4.7/libs/armeabi/libgnustl_static.a
LOCAL_LDFLAGS += /home/ndisanze/workspace/IBeatBox/android-ndk-r8d/sources/cxx-stl/gnu-libstdc++/4.7/libs/armeabi/libsupc++.a
LOCAL_CPPFLAGS :=-lstdc++ -fexceptions -Wall  -frtti  -Wno-unused-parameter
LOCAL_ARM_MODE := arm
TARGET_PLATFORM:=android-14
TARGET_ARCH_ABI:=armeabi-v7a
include $(PREBUILT_STATIC_LIBRARY)




include $(CLEAR_VARS)

LOCAL_MODULE    := native-audio-jni 
LOCAL_SRC_FILES := \
native-audio-jni.cpp \
detectors/AudioToAudioEvent.cpp


LOCAL_STATIC_LIBRARIES := libstk
LOCAL_STATIC_LIBRARIES += libpurplemetaproc

# for native audio
LOCAL_LDLIBS    += -lOpenSLES

LOCAL_LDLIBS    += -lstdc++
# for logging
LOCAL_LDLIBS    += -llog

LOCAL_LDLIBS    += -lc
# for native asset manager
#LOCAL_SHARED_LIBRARIES := libstlport_shared
LOCAL_LDLIBS    += -landroid
LOCAL_ALLOW_UNDEFINED_SYMBOLS :=true
STLPORT_FORCE_REBUILD := true
LOCAL_LDFLAGS += /home/ndisanze/workspace/IBeatBox/android-ndk-r8d/sources/cxx-stl/gnu-libstdc++/4.7/libs/armeabi/libgnustl_static.a
LOCAL_LDFLAGS += /home/ndisanze/workspace/IBeatBox/android-ndk-r8d/sources/cxx-stl/gnu-libstdc++/4.7/libs/armeabi/libsupc++.a
LOCAL_CPPFLAGS :=-lstdc++ -libstk -libpurplemetaproc -fexceptions  -frtti  #-Wno-unused-parameter -Wall
#LOCAL_LDLIBS    += $(LOCAL_PATH)/libstk.a
include $(BUILD_SHARED_LIBRARY)

LOCAL_ARM_MODE := arm
 
TARGET_PLATFORM:=android-14
 
TARGET_ARCH_ABI:=armeabi-v7a
#TARGET_ARCH_ABI :=armeabi
TARGET_ABI:=$(TARGET_PLATFORM)-$(TARGET_ARCH_ABI)

