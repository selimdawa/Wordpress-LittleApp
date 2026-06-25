package com.littleapp.wordpress.Model

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("featured_media")
    val featured_media: Int = 0,

    @SerializedName("title")
    val title: JsonObject? = null,

    @SerializedName("excerpt")
    val excerpt: JsonObject? = null,

    @SerializedName("content")
    val content: JsonObject? = null,

    val sqLiteId: Int = 0,
    val wpPostId: Int = 0,
    val wpTitle: String? = null,
    val wpExcerpt: String? = null,
    val wpContent: String? = null,
    val isFavorite: Boolean = false
)