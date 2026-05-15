package com.example.nammapustaka.network

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GeminiClient {
    private const val BASE = "https://generativelanguage.googleapis.com/"

    fun create(): GeminiApi {
        val gson = GsonBuilder().serializeNulls().create()
        return Retrofit.Builder()
            .baseUrl(BASE)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(GeminiApi::class.java)
    }
}
