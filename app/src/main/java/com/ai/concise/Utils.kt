package com.ai.concise

object Utils {

    init {
        System.loadLibrary("native-lib")
    }
    external fun getEncryptedKey() : String

    val API_KEY = getEncryptedKey() + ""
}