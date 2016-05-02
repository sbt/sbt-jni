#include <jni.h>
#include "multiclasses_Multiplier.h"

/*
 * Class:     multiclasses_Multiplier
 * Method:    times
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_multiclasses_Multiplier_times
  (JNIEnv* env, jobject instance, jint factor)
{
	jclass clazz = (*env)->GetObjectClass(env, instance);
	jmethodID method = (*env)->GetMethodID(env, clazz, "base", "()I");
	jint base = (*env)->CallIntMethod(env, instance, method);
	return base * factor;
}
