#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring

JNICALL
Java_com_ai_concise_Utils_getEncryptedKey(JNIEnv *env, jobject object) {
 std::string encrypted_key = "Bearer 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";
 return env->NewStringUTF(encrypted_key.c_str());
}