#include <stdio.h>
#include "simple_Library_00024.h"

/*
 * Class:     simple_Library__
 * Method:    say
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_simple_Library_00024_say__Ljava_lang_String_2
(JNIEnv *env, jobject clazz, jstring message) {
	const char* msg = (*env)->GetStringUTFChars(env, message, 0);
	fprintf(stdout, "Printing from native library: %s\n", msg);
	fflush(stdout);
	(*env)->ReleaseStringUTFChars(env, message, msg);
	return 42;
}

JNIEXPORT jint JNICALL Java_simple_Library_00024_say__Ljava_lang_String_2I
(JNIEnv *env, jobject clazz, jstring message, jint i) {
	const char* msg = (*env)->GetStringUTFChars(env, message, 0);
	fprintf(stdout, "Printing from native library: %s,%d\n", msg, i);
	fflush(stdout);
	(*env)->ReleaseStringUTFChars(env, message, msg);
	return 43;
}

JNIEXPORT jint JNICALL Java_simple_Library_00024_say__Ljava_lang_String_2IJ
(JNIEnv *env, jobject clazz, jstring message, jint i, jlong l) {
	const char* msg = (*env)->GetStringUTFChars(env, message, 0);
	fprintf(stdout, "Printing from native library: %s,%d,%d\n", msg, i, l);
	fflush(stdout);
	(*env)->ReleaseStringUTFChars(env, message, msg);
	return 44;
}
