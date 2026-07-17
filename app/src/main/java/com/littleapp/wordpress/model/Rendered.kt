package com.littleapp.wordpress.model

import com.google.gson.annotations.SerializedName

data class Rendered(
    @SerializedName("rendered")
    val rendered: String? = ""
) {
    override fun toString(): String {
        return rendered ?: ""
    }
}