package com.littleapp.wordpress.Util

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import com.littleapp.wordpress.R

object PageView {
    @SuppressLint("SetJavaScriptEnabled")
    fun initWebView(content: String?, page: Context, webView: WebView) {
        var content = content
        val progressDialog: ProgressDialog
        
        progressDialog = ProgressDialog(page)
        progressDialog.setTitle(page.getString(R.string.progressdialog_title))
        progressDialog.setMessage(page.getString(R.string.progressdialog_message))

        //Set Html content
        content = "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />" +
                "<script src=\"prism.js\"></script>" +
                "<div class=\"content\">" + content + "</div>"

        webView.settings.loadsImagesAutomatically = true
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressDialog.show()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressDialog.dismiss()
            }
        }

        webView.loadDataWithBaseURL(
            "file:///android_asset/*", content, "text/html; charset=utf-8", "UTF-8", null
        )
    }
}