package com.littleapp.wordpress.utils

import android.webkit.WebView
import android.webkit.WebViewClient

fun WebView.loadWordPressContent(content: String?) {
    val htmlContent = """
        <link rel="stylesheet" type="text/css" href="style.css" />
        <script src="prism.js"></script>
        <div class="content">$content</div>
    """.trimIndent()

    this.apply {
        settings.apply {
            loadsImagesAutomatically = true
            javaScriptEnabled = false
        }
        webViewClient = WebViewClient()
        loadDataWithBaseURL(
            "file:///android_asset/*",
            htmlContent,
            "text/html; charset=utf-8",
            "UTF-8",
            null
        )
    }
}