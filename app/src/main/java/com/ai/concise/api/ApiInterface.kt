package com.ai.concise.api

import com.ai.concise.models.chat.ChatModel
import com.ai.concise.models.imageresponse.GenerateImageModel
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {

    @POST("/v1/images/generations")
    suspend fun generateImage(

        @Header("Content-Type") contentType: String,
        @Header("Authorization") authorization: String,
        @Body requestBody: RequestBody
    ) : GenerateImageModel

}