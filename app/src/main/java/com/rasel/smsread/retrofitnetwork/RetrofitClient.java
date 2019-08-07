package com.rasel.smsread.retrofitnetwork;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "base url";
    private static RetrofitClient retrofitClient;
    private Retrofit retrofit;

    private RetrofitClient(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public static synchronized RetrofitClient getInstance(){
        if( retrofitClient==null){
            retrofitClient = new RetrofitClient();
        }
        return retrofitClient;
    }

    public ApiEndPoint getApi(){
        return retrofit.create(ApiEndPoint.class);
    }
}
