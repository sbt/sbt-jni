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

/*
 * Class:      simple_Library_00024
 * Method:     say
 * Signature:  (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_simple_Library_00024_say__Ljava_lang_String_2I
(JNIEnv *env, jobject clazz, jstring message, jint i) {
	const char* msg = (*env)->GetStringUTFChars(env, message, 0);
	fprintf(stdout, "Printing from native library: %s,%d\n", msg, i);
	fflush(stdout);
	(*env)->ReleaseStringUTFChars(env, message, msg);
	return 43;
}

/*
 * Class:      simple_Library_00024
 * Method:     say
 * Signature:  (Ljava/lang/String;IJ)I
 */
JNIEXPORT jint JNICALL Java_simple_Library_00024_say__Ljava_lang_String_2IJ
(JNIEnv *env, jobject clazz, jstring message, jint i, jlong l) {
	const char* msg = (*env)->GetStringUTFChars(env, message, 0);
	fprintf(stdout, "Printing from native library: %s,%d,%d\n", msg, i, l);
	fflush(stdout);
	(*env)->ReleaseStringUTFChars(env, message, msg);
	return 44;
}

/*
 * Class:      simple_Library_00024
 * Method:     say
 * Signature:  ([Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_simple_Library_00024_say___3Ljava_lang_String_2
(JNIEnv *env, jobject clazz, jobjectArray messages) {
  for (jint i = 0; i < (*env)->GetArrayLength(env, messages); i++) {
    jstring message = (jstring) ((*env)->GetObjectArrayElement(env, messages, i));
    const char *msg = (*env)->GetStringUTFChars(env, message, 0);
    fprintf(stdout, "Printing from native library: %s\n", msg);
  }
  return 45;
}
