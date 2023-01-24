package com.example.inventorydashboard.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {
    private static String BASE_URL = "https://v3421074.mhs.d3tiuns.com/mobile-backend/public/api/";
    private static Retrofit retrofit;
    public static ApiEndpoint endPoint() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ApiEndpoint.class);
    }
}
