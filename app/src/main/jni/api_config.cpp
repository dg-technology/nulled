#include <jni.h>
#include <string>


std::string SERVER_URL = "https://heplay.dghub.in/rest-api/";
std::string API_KEY = "o6d7q0j5x4qq87tk1mn5ubgp";
std::string PURCHASE_CODE = "xxxxxxxxxxx-xxxxxx-xxxxxx";
std::string YOUTUBE_API_KEY = "xxxxxxxxxxx-xxxxxx-xxxxxx";

extern "C" JNIEXPORT jstring JNICALL
// Change "com.mmovie.dghub.in" with your package name. // I.e "com_package_name" // DO NOT CHANGE OTHER THINGS
Java_com_oxoo_spagreen_AppConfig_getApiServerUrl(
        JNIEnv* env,
        jclass clazz) {
    return env->NewStringUTF(SERVER_URL.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
// Change "com.mmovie.dghub.in" with your package name. // I.e "com_package_name" // DO NOT CHANGE OTHER THINGS

Java_com_oxoo_spagreen_AppConfig_getApiKey(
        JNIEnv* env,
jclass clazz) {
return env->NewStringUTF(API_KEY.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
// Change "com.mmovie.dghub.in" with your package name. // I.e "com_package_name" // DO NOT CHANGE OTHER THINGS

Java_com_oxoo_spagreen_AppConfig_getPurchaseCode(
        JNIEnv* env,
        jclass clazz) {
    return env->NewStringUTF(PURCHASE_CODE.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
// Change "com.mmovie.dghub.in" with your package name. // I.e "com_package_name" // DO NOT CHANGE OTHER THINGS

Java_com_oxoo_spagreen_AppConfig_getYouTubeApiKey(
        JNIEnv* env,
        jclass clazz) {
    return env->NewStringUTF(YOUTUBE_API_KEY.c_str());
}
