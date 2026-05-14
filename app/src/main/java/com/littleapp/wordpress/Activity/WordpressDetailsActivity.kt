package com.littleapp.wordpress.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.littleapp.wordpress.R
import com.littleapp.wordpress.Unit.CLASS
import com.littleapp.wordpress.Model.Media
import com.littleapp.wordpress.Sqlite.PostDB
import com.littleapp.wordpress.Util.InternetConnection
import com.littleapp.wordpress.Util.PageView
import com.littleapp.wordpress.Util.WPApiService
import com.littleapp.wordpress.Util.WordPressClient
import com.littleapp.wordpress.databinding.ActivityWordpressDetailsBinding
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WordpressDetailsActivity : AppCompatActivity() {

    private var binding: ActivityWordpressDetailsBinding? = null
    var context: Context = this@WordpressDetailsActivity
    var parentView: View? = null
    var isItemSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        //THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        binding = ActivityWordpressDetailsBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        //Get Intent
        val id = intent.getSerializableExtra("postId") as Int
        val featuredMedia = intent.getSerializableExtra("featuredMedia") as Int
        val title: String = intent.getSerializableExtra("postTitle").toString()
        val contentPost: String = intent.getSerializableExtra("postContent").toString()
            .replace("\\\\n".toRegex(), "<br>").replace("\\\\r".toRegex(), "")
            .replace("\\\\".toRegex(), "")
        initToolbar(title, id)
        PageView.initWebView(contentPost, context, binding!!.content.webview)

        //Call Media
        if (InternetConnection.checkInternetConnection(applicationContext)) {
            val api: WPApiService = WordPressClient.apiService
            val call: Call<Media?>? = api.getPostThumbnail(featuredMedia)
            call!!.enqueue(object : Callback<Media?> {
                override fun onResponse(call: Call<Media?>, response: Response<Media?>) {
                    if (response.code() != 404) {
                        val media: Media? = response.body()
                        val mediaUrl: String =
                            media!!.guid!!.get("rendered").toString().replace("\"", "")
                        Glide.with(applicationContext).load(mediaUrl)
                            .thumbnail(0.5f)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding!!.postBackdrop)
                    } else {
                    }
                }

                override fun onFailure(call: Call<Media?>, t: Throwable) {}
            })
        } else {
            Snackbar.make(parentView!!, "Can't connect to the Internet", Snackbar.LENGTH_INDEFINITE)
                .show()
        }
        binding!!.content.postTitle.text = title
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        //Get Intent
        val id = intent.getSerializableExtra("postId") as Int
        val title: String = intent.getSerializableExtra("postTitle").toString()
        val excerpt: String = intent.getSerializableExtra("postExcerpt").toString()
        val contentPost: String = intent.getSerializableExtra("postContent").toString()
            .replace("\\\\n".toRegex(), "").replace("\\\\r".toRegex(), "")
            .replace("\\\\".toRegex(), "")

        //Toggle Navigation icon
        if (!isItemSelected) {
            item.icon = resources.getDrawable(R.drawable.ic_heart_selected, theme)
            isItemSelected = true
            PostDB.getInstance(applicationContext)!!.insert(id, title, excerpt, isItemSelected)
        } else {
            item.icon = resources.getDrawable(R.drawable.ic_heart_unselected, theme)
            isItemSelected = false
            PostDB.getInstance(applicationContext)!!.delete(id)
        }
        return super.onOptionsItemSelected(item)
    }

    private inner class MyWebView : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            view.loadUrl(request.url.toString())
            return true
        }

        override fun onLoadResource(view: WebView, url: String) {
            super.onLoadResource(view, url)
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
        }
    }

    private fun initToolbar(title: String, id: Int) {
        window.statusBarColor = Color.TRANSPARENT
        setSupportActionBar(binding!!.postToolbar)
        initCollapsingToolbar(title)
        isItemSelected = PostDB.getInstance(applicationContext)!!.getDbPostIsFav(id)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding!!.postToolbar.setNavigationOnClickListener { finish() }
    }

    //Init CollapsingToolbarLayout
    private fun initCollapsingToolbar(title: String) {
        val collapsingToolbar: CollapsingToolbarLayout =
            findViewById<View>(R.id.post_collapsing_toolbarLayout) as CollapsingToolbarLayout
        collapsingToolbar.title = title
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.add_to_favorite_menu, menu)
        if (isItemSelected) {
            menu.findItem(R.id.add_as_favorite).icon =
                resources.getDrawable(R.drawable.ic_heart_selected, theme)
        } else {
            menu.findItem(R.id.add_as_favorite).icon =
                resources.getDrawable(R.drawable.ic_heart_unselected, theme)
        }
        return true
    }

    companion object {
        fun createIntent(
            context: Context?, id: Int, featuredMedia: Int, title: String?,
            excerpt: String?, content: String?,
        ): Intent {
            val intent = Intent(context, CLASS.WORDPRESS_DETAILS)

            intent.putExtra("postId", id)
            intent.putExtra("featuredMedia", featuredMedia)
            intent.putExtra("postExcerpt", excerpt)
            intent.putExtra("postTitle", title)
            intent.putExtra("postContent", content)
            return intent
        }
    }
}