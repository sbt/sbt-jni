#include <stdio.h>
#include "ch_jodersky_jni_basic_Library__.h"

/*
 * Class:     ch_jodersky_jni_basic_Library__
 * Method:    print
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_ch_jodersky_jni_basic_Library_00024_print
(JNIEnv *env, jobject clazz, jstring message) {
	const char* msg = (*env)->GetStringUTFChars(env, message, 0);
	fprintf(stdout, "Printing from native library: %s", msg);
	fflush(stdout);
	(*env)->ReleaseStringUTFChars(env, message, msg);
	return 0;
}
