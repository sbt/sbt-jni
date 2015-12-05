#include <stdio.h>
#include "org_example_jni_demo_Library__.h"

/*
 * Class:     org_example_jni_demo_Library__
 * Method:    print
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_example_jni_demo_Library_00024_print
(JNIEnv *env, jobject clazz, jstring message) {
	const char* msg = (*env)->GetStringUTFChars(env, message, 0);
	fprintf(stdout, "Printing from native library: %s", msg);
	fflush(stdout);
	(*env)->ReleaseStringUTFChars(env, message, msg);
	return 0;
}
