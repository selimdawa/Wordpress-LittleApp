package com.littleapp.wordpress.Model

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

class Post {
    //Setter
    // Getter
    @SerializedName("id")
    var id = 0

    @SerializedName("featured_media")
    var featured_media = 0

    @SerializedName("title")
    var title: JsonObject? = null

    @SerializedName("excerpt")
    var excerpt: JsonObject? = null

    @SerializedName("content")
    var content: JsonObject? = null

    //SetterSQLite
    //Getter SQLite
    //Variable for SQLite
    var sqLiteId = 0
    var wpPostId = 0
    var wpTitle: String? = null
    var wpExcerpt: String? = null
    var wpContent: String? = null
    var isFavorite = false

    constructor()

    constructor(
        sqLiteId: Int, wpPostId: Int, wpTitle: String?, wpExcerpt: String?, isFavorite: Int
    ) {
        this.sqLiteId = sqLiteId
        this.wpPostId = wpPostId
        this.wpTitle = wpTitle
        this.wpExcerpt = wpExcerpt
        wpContent = wpContent
        this.isFavorite = false
        if (isFavorite == 1) {
            this.isFavorite = true
        }
    }
}