LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := minimal_main

ifndef MINIMAL_JAVA_PACKAGE_PATH
$(error Please define MINIMAL_JAVA_PACKAGE_PATH to the path of your Java package with dots replaced with underscores, for example "com_example_SanAngeles")
endif

LOCAL_CFLAGS :=  -I$(LOCAL_PATH)/../../../jni/sdl/include \
	-I$(LOCAL_PATH)/../include \
	-DMINIMAL_JAVA_PACKAGE_PATH=$(MINIMAL_JAVA_PACKAGE_PATH) \
	-DMINIMAL_CURDIR_PATH=\"$(MINIMAL_CURDIR_PATH)\"

LOCAL_CPP_EXTENSION := .cpp

#LOCAL_SRC_FILES := minimal_main.c minimal_dummy_main.c
LOCAL_SRC_FILES := minimal_main.c

LOCAL_SHARED_LIBRARIES := application
LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)
