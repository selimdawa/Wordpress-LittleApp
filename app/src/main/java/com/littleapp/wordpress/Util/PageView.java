package com.littleapp.wordpress.Util;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.littleapp.wordpress.R;

public class PageView {

    @SuppressLint("SetJavaScriptEnabled")
    public static void initWebView(String content, Context page, WebView webView) {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(page);
        progressDialog.setTitle(page.getString(R.string.progressdialog_title));
        progressDialog.setMessage(page.getString(R.string.progressdialog_message));

        //Set Html content
        content = "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />" +
                "<script src=\"prism.js\"></script>" +
                "<div class=\"content\">" + content + "</div>";

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressDialog.dismiss();
            }
        });

        webView.loadDataWithBaseURL("file:///android_asset/*", content, "text/html; charset=utf-8", "UTF-8", null);
    }
}