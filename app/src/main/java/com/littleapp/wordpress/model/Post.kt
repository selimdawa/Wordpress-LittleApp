package com.littleapp.wordpress.model

import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("featured_media")
    val featuredMedia: Int = 0,

    @SerializedName("title")
    val title: Rendered? = null,

    @SerializedName("excerpt")
    val excerpt: Rendered? = null,

    @SerializedName("content")
    val content: Rendered? = null,

    val sqLiteId: Int = 0,
    val wpPostId: Int = 0,
    val wpTitle: String? = null,
    val wpExcerpt: String? = null,
    val wpContent: String? = null,
    val isFavorite: Boolean = false
)