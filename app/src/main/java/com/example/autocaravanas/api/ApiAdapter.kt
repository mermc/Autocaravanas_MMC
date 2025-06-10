package com.example.autocaravanas.api


import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiAdapter {
    private const val BASE_URL = "https://caravanas.milanmc.me/"

    //privada para almacenar el token
    private var _token: String = ""
    // getter del token
    val token: String
        get() = _token

    fun setToken(newToken: String) {
        _token = newToken
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(request)
        }.build()
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}