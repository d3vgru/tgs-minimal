#ifdef ANDROID

#include <unistd.h>
#include <stdlib.h>
#include <jni.h>
#include <android/log.h>
#include "SDL_thread.h"
#include "minimal_main.h"

#define LOG(x) __android_log_write(ANDROID_LOG_INFO, "main", (x))

/* JNI-C wrapper stuff */

#ifdef __cplusplus
#define C_LINKAGE "C"
#else
#define C_LINKAGE
#endif


#ifndef MINIMAL_JAVA_PACKAGE_PATH
#error You have to define MINIMAL_JAVA_PACKAGE_PATH to your package path with dots replaced with underscores, for example "com_example_SanAngeles"
#endif
#define JAVA_EXPORT_NAME2(name,package) Java_##package##_##name
#define JAVA_EXPORT_NAME1(name,package) JAVA_EXPORT_NAME2(name,package)
#define JAVA_EXPORT_NAME(name) JAVA_EXPORT_NAME1(name,MINIMAL_JAVA_PACKAGE_PATH)


static int isSdcardUsed = 0;


// JNI env stuff from sdl/src/video/android/SDL_androidvideo.c
// Extremely wicked JNI environment to call Java functions from C code
static JNIEnv* JavaEnv = NULL;
static jobject JavaActivity = NULL;
static jclass JavaActivityClass = NULL;
static jmethodID JavaCheckPause = NULL;
static jmethodID JavaWaitForResume = NULL;

// for pyjnius
JNIEnv *SDL_ANDROID_GetJNIEnv()
{
    return JavaEnv;
}

int SDL_ANDROID_CheckPause()
{
    int rv;
    
    rv = (*JavaEnv)->CallIntMethod( JavaEnv, JavaActivityClass, JavaCheckPause );
    return rv;
}

void SDL_ANDROID_WaitForResume()
{
    (*JavaEnv)->CallVoidMethod(JavaEnv, JavaActivityClass, JavaWaitForResume);
}


// for PythonActivity
#define gref(x) (*env)->NewGlobalRef(env, (x))
extern C_LINKAGE void
JAVA_EXPORT_NAME(PythonActivity_nativeInit) ( JNIEnv*  env, jobject thiz )
{
	int argc = 1;
	char * argv[] = { "minimal" };

	// from sdl/src/video/android/SDL_androidvideo.c
	JavaEnv = env;
	JavaActivity = gref(thiz);
	JavaActivityClass = (*JavaEnv)->GetObjectClass(JavaEnv, thiz);
//	JavaCheckPause = (*JavaEnv)->GetMethodID(JavaEnv, JavaActivityClass, "checkPause", "()I");
//	JavaWaitForResume = (*JavaEnv)->GetMethodID(JavaEnv, JavaActivityClass, "waitForResume", "()V");

	main( argc, argv );
};

extern C_LINKAGE void
JAVA_EXPORT_NAME(PythonActivity_nativeIsSdcardUsed) ( JNIEnv*  env, jobject thiz, jint flag )
{
	isSdcardUsed = flag;
}

extern C_LINKAGE void
JAVA_EXPORT_NAME(PythonActivity_nativeSetEnv) ( JNIEnv*  env, jobject thiz, jstring j_name, jstring j_value )
{
    jboolean iscopy;
    const char *name = (*env)->GetStringUTFChars(env, j_name, &iscopy);
    const char *value = (*env)->GetStringUTFChars(env, j_value, &iscopy);
    setenv(name, value, 1);
    (*env)->ReleaseStringUTFChars(env, j_name, name);
    (*env)->ReleaseStringUTFChars(env, j_value, value);
}

#undef JAVA_EXPORT_NAME
#undef JAVA_EXPORT_NAME1
#undef JAVA_EXPORT_NAME2
#undef C_LINKAGE

#endif
