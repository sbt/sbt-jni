#include <jni.h>
#include "multiclasses_Adder.h"
#include "multiclasses_Adder__.h"

/*
 * Class:     multiclasses_Adder
 * Method:    plus
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_multiclasses_Adder_plus
  (JNIEnv* env, jobject instance, jint term)
{
	jclass clazz = (*env)->GetObjectClass(env, instance);
	jfieldID field = (*env)->GetFieldID(env, clazz, "base", "I");
	jint base = (*env)->GetIntField(env, instance, field);
	return base + term;
}

/*
 * Class:     multiclasses_Adder__
 * Method:    sum
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_multiclasses_Adder_00024_sum
  (JNIEnv* env, jobject instance, jint term1, jint term2)
{
	return term1 + term2;
}

/* Class:     multiclasses_Adder
 * Method:    plusValue
 * Signature: (Lmulticlasses/Value;)I
 */
JNIEXPORT jint JNICALL Java_multiclasses_Adder_plusValue
  (JNIEnv* env, jobject instance, jobject value)
{
	jclass adderClass = (*env)->GetObjectClass(env, instance);
	jfieldID baseField = (*env)->GetFieldID(env, adderClass, "base", "I");
	jint base = (*env)->GetIntField(env, instance, baseField);

	// Value.x
	jclass valueClass = (*env)->GetObjectClass(env, value);
	jmethodID xGetter = (*env)->GetMethodID(env, valueClass, "x", "()I");
	jint x = (*env)->CallIntMethod(env, value, xGetter);

	return base + x;
}
