package com.example.elderease.news;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GNewsRetrofitClient {
    private static final String BASE_URL = "https://gnews.io/api/";
    private static Retrofit retrofit = null;

    public static GNewsApiService getNewsService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(GNewsApiService.class);
    }
}
