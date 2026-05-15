package com.example.nammapustaka.network

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Google Gemini generateContent REST (v1beta).
 * API key is appended as query param per Google documentation.
 */
interface GeminiApi {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body body: GeminiRequest
    ): GeminiResponse
}
