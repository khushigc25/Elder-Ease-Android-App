package com.example.elderease.chatbot;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GeminiAPI {
    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    Call<GeminiResponse> generateContent(
            @Query("key") String apiKey, // API Key passed as query parameter
            @Body GeminiRequest request  // Request body containing user input
    );
}
