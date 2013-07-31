# Application.mk
# Copyright (c) 2012 Jacek Marchwicki
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#presets - do not tuch this
FEATURE_NEON:=
LIBRARY_PROFILER:=
MODULE_ENCRYPT:=

#settings



#if armeabi-v7a
ifeq ($(TARGET_ARCH_ABI), armeabi-v7a)
	# add neon optimization code (only armeabi-v7a)
	FEATURE_NEON:=yes
else

endif


include $(CLEAR_VARS)
LOCAL_MODULE := ffmpeg-prebuilt
LOCAL_SRC_FILES := ffmpeg-build/$(TARGET_ARCH_ABI)/libffmpeg.so
LOCAL_EXPORT_C_INCLUDES := ffmpeg-build/$(TARGET_ARCH_ABI)/include
LOCAL_EXPORT_LDLIBS := ffmpeg-build/$(TARGET_ARCH_ABI)/libffmpeg.so
LOCAL_PRELINK_MODULE := true
include $(PREBUILT_SHARED_LIBRARY)

ifdef FEATURE_NEON
include $(CLEAR_VARS)
LOCAL_MODULE := ffmpeg-prebuilt-neon
LOCAL_SRC_FILES := ffmpeg-build/$(TARGET_ARCH_ABI)/libffmpeg-neon.so
LOCAL_EXPORT_C_INCLUDES := ffmpeg-build/$(TARGET_ARCH_ABI)-neon/include
LOCAL_EXPORT_LDLIBS := ffmpeg-build/$(TARGET_ARCH_ABI)/libffmpeg-neon.so
LOCAL_PRELINK_MODULE := true
include $(PREBUILT_SHARED_LIBRARY)
endif

#ffmpeg-jni library
include $(CLEAR_VARS)
LOCAL_ALLOW_UNDEFINED_SYMBOLS=false
LOCAL_MODULE := ffmpeg-jni
LOCAL_SRC_FILES := ffmpeg-jni.c player.c queue.c helpers.c jni-protocol.c convert.cpp editor.c
LOCAL_C_INCLUDES := $(LOCAL_PATH)/ffmpeg-build/$(TARGET_ARCH_ABI)/include
LOCAL_SHARED_LIBRARY := ffmpeg-prebuilt


LOCAL_CFLAGS += -DLIBYUV
LOCAL_C_INCLUDES += $(LOCAL_PATH)/libyuv/include
LOCAL_CPP_INCLUDES += $(LOCAL_PATH)/libyuv/include
LOCAL_STATIC_LIBRARIES += libyuv_static
LOCAL_REQUIRED_MODULES += libyuv_static


LOCAL_LDLIBS    += -landroid
LOCAL_LDLIBS += -llog -ljnigraphics -lz -lm -g $(LOCAL_PATH)/ffmpeg-build/$(TARGET_ARCH_ABI)/libffmpeg.so
include $(BUILD_SHARED_LIBRARY)


ifdef FEATURE_NEON
include $(CLEAR_VARS)
LOCAL_ALLOW_UNDEFINED_SYMBOLS=false
LOCAL_MODULE := ffmpeg-jni-neon
LOCAL_SRC_FILES := ffmpeg-jni.c player.c queue.c helpers.c jni-protocol.c convert.cpp editor.c
LOCAL_C_INCLUDES := $(LOCAL_PATH)/ffmpeg-build/$(TARGET_ARCH_ABI)/include
LOCAL_SHARED_LIBRARY := ffmpeg-prebuilt-neon


LOCAL_CFLAGS += -DLIBYUV
LOCAL_C_INCLUDES += $(LOCAL_PATH)/libyuv/include
LOCAL_CPP_INCLUDES += $(LOCAL_PATH)/libyuv/include
LOCAL_STATIC_LIBRARIES += libyuv_static
LOCAL_REQUIRED_MODULES += libyuv_static


LOCAL_LDLIBS    += -landroid
LOCAL_LDLIBS += -llog -ljnigraphics -lz -lm -g $(LOCAL_PATH)/ffmpeg-build/$(TARGET_ARCH_ABI)/libffmpeg-neon.so
include $(BUILD_SHARED_LIBRARY)
endif


#nativetester-jni library
include $(CLEAR_VARS)

ifdef FEATURE_VFPV3
LOCAL_CFLAGS += -DFEATURE_VFPV3
endif

ifdef FEATURE_NEON
LOCAL_CFLAGS += -DFEATURE_NEON
endif

LOCAL_ALLOW_UNDEFINED_SYMBOLS=false
LOCAL_MODULE := nativetester-jni
LOCAL_SRC_FILES := nativetester-jni.c nativetester.c
LOCAL_STATIC_LIBRARIES := cpufeatures
LOCAL_LDLIBS  := -llog
include $(BUILD_SHARED_LIBRARY)


include $(call all-makefiles-under,$(LOCAL_PATH))
$(call import-module,cpufeatures)
