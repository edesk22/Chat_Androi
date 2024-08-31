package com.example.chat_anthropic.network

import com.example.chat_anthropic.model.AnthropicRequest
import com.example.chat_anthropic.model.AnthropicResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import com.example.chat_anthropic.BuildConfig


private const val BASE_URL = "https://api.anthropic.com/"

//Remove loggingInterceptor in prodduction
private val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}
private val client = OkHttpClient.Builder()
    .addInterceptor { chain ->
        val original = chain.request()
        val request = original.newBuilder()
            .header("x-api-key", BuildConfig.API_KEY)
            .header("anthropic-version", "2023-06-01")
            .header("content-type", "application/json")
            .method(original.method, original.body)
            .build()
        chain.proceed(request)
    }
    .addInterceptor(loggingInterceptor)
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface AnthropicApiService {
    @POST("v1/messages")
    @Headers("Content-Type: application/json")
    suspend fun sendMessage(@Body request: AnthropicRequest): AnthropicResponse
}

object AnthropicApi {
    val retrofitService: AnthropicApiService by lazy {
        retrofit.create(AnthropicApiService::class.java)
    }
}