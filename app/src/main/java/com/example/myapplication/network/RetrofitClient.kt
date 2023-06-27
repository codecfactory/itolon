package com.example.myapplication.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
class RetrofitClient {
    val okHttpClient = OkHttpClient.Builder()
        .readTimeout(15, TimeUnit.SECONDS)
        .connectTimeout(15, TimeUnit.SECONDS)
        .build()
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("http://44.231.47.188/").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).addCallAdapterFactory(LiveDataCallAdapterFactory()).build()
    }
}