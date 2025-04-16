package com.example.elderease.news;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GNewsApiService {
    @GET("v4/top-headlines")
    Call<NewsResponse> getTopHeadlines(
            @Query("token") String apiKey,
            @Query("lang") String language,
            @Query("country") String country,
            @Query("category") String category
    );

}
