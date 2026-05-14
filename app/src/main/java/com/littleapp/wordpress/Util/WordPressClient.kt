package com.littleapp.wordpress.Util

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WordPressClient {

    private const val BASE_URL = "https://www.wpexplorer.com/wp-json/wp/v2/"
    val retroInstance: Retrofit
        get() = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val apiService: WPApiService
        get() = retroInstance.create(WPApiService::class.java)
}