
# The namespace in Java file, with dots replaced with underscores
MINIMAL_JAVA_PACKAGE_PATH := org_kivy_android

# Path to files with application data - they should be downloaded from Internet on first app run inside
# Java sources, or unpacked from resources (TODO)
# Typically /sdcard/alienblaster 
# Or /data/data/de.schwardtnet.alienblaster/files if you're planning to unpack data in application private folder
# Your application will just set current directory there
MINIMAL_CURDIR_PATH := org.kivy.android

APPLICATION_ADDITIONAL_CFLAGS := -finline-functions -O2

APPLICATION_ADDITIONAL_LDFLAGS := -Xlinker -export-dynamic -Wl,-O1 -Wl,-Bsymbolic-functions

include $(call all-subdir-makefiles)
