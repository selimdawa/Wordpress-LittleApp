package com.littleapp.wordpress.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WordPressClient {

    private const val BASE_URL = "https://techcrunch.com/wp-json/wp/v2/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: WPApiService by lazy {
        retrofit.create(WPApiService::class.java)
    }
}