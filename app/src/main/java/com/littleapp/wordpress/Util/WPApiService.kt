package com.littleapp.wordpress.Util

import com.littleapp.wordpress.Model.Media
import com.littleapp.wordpress.Model.Post
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WPApiService {
    @GET("posts")
    fun getPosts(): Call<List<Post?>?>?

    @GET("posts/{id}")
    fun getPostById(@Path("id") postId: Int): Call<Post?>?

    @GET("media/{featured_media}")
    fun getPostThumbnail(@Path("featured_media") media: Int): Call<Media?>?
}