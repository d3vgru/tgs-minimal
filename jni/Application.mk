APP_PROJECT_PATH := $(call my-dir)/..

APP_MODULES := application minimal_main sqlite3

APP_ABI := $(ARCH)
APP_STL := gnustl_static
APP_CFLAGS += $(OFLAG)
