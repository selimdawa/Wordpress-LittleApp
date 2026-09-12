package com.littleapp.wordpress.utils

import android.webkit.WebView
import android.webkit.WebViewClient

fun WebView.loadWordPressContent(content: String?, textColor: String? = null) {
    val style = if (textColor != null) {
        "<style>body { color: $textColor; font-family: sans-serif; line-height: 1.6; } img { max-width: 100%; height: auto; }</style>"
    } else {
        "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />"
    }

    val htmlContent = """
        <html>
        <head>
            $style
            <script src="prism.js"></script>
        </head>
        <body>
            <div class="content">$content</div>
        </body>
        </html>
    """.trimIndent()

    this.apply {
        settings.apply {
            loadsImagesAutomatically = true
            javaScriptEnabled = false
        }
        webViewClient = WebViewClient()
        setBackgroundColor(0)
        loadDataWithBaseURL(
            "file:///android_asset/*",
            htmlContent,
            "text/html; charset=utf-8",
            "UTF-8",
            null,
        )
    }
}