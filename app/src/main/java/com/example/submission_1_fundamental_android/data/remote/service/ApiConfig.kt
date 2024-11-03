package com.example.submission_1_fundamental_android.data.remote.service

import com.example.submission_1_fundamental_android.BuildConfig as BC
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        private const val BASE_URL = BC.BASE_URL
        private const val IS_DEBUG = true

        private val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (IS_DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        private val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
//            .addInterceptor(ChuckerInterceptor(applicationContext))
            .build()

        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        val apiService: ApiService by lazy {
            retrofit.create(ApiService::class.java)
        }
//        val call = apiService
    }
}