package com.example.autocaravanas.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiAdapter {
    private var API_SERVICE: ApiService? = null
    private const val BASE_URL = "https://caravanas.milanmc.me/"
    public var API_TOKEN: String = ""

    @get:Synchronized
    val instance: ApiService?
        get() {
            if (API_SERVICE == null) {
                val client = OkHttpClient.Builder().addInterceptor(Interceptor { chain ->
                    val request = chain.request().newBuilder().addHeader("Authorization", "Bearer ${API_TOKEN}").build()

                    chain.proceed(request)
                }).build()

                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()

                API_SERVICE = retrofit.create(ApiService::class.java)
            }
            return API_SERVICE
        }
}