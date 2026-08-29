package com.littleapp.wordpress.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.littleapp.wordpress.model.Media
import com.littleapp.wordpress.R
import com.littleapp.wordpress.sqlite.PostDB
import com.littleapp.wordpress.utils.applyAppTheme
import com.littleapp.wordpress.utils.isNetworkAvailable
import com.littleapp.wordpress.utils.loadWordPressContent
import com.littleapp.wordpress.utils.WPApiService
import com.littleapp.wordpress.utils.WordPressClient
import com.littleapp.wordpress.databinding.ActivityWordpressDetailsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WordpressDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWordpressDetailsBinding
    private val context: Context = this
    private var isItemSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        applyAppTheme()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityWordpressDetailsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val id = intent.getIntExtra("postId", -1)
        val featuredMedia = intent.getIntExtra("featuredMedia", -1)
        val title = intent.getStringExtra("postTitle").orEmpty()
        val contentPost = intent.getStringExtra("postContent").orEmpty()
            .replace("\\\\n".toRegex(), "<br>")
            .replace("\\\\r".toRegex(), "")
            .replace("\\\\".toRegex(), "")

        initToolbar(title, id)
        binding.content.webview.loadWordPressContent(contentPost)

        if (isNetworkAvailable()) {
            val api: WPApiService = WordPressClient.apiService
            val call: Call<Media?>? = api.getPostThumbnail(featuredMedia)

            call?.enqueue(object : Callback<Media?> {
                override fun onResponse(call: Call<Media?>, response: Response<Media?>) {
                    if (response.code() != 404) {
                        val media: Media? = response.body()
                        val mediaUrl = media?.guid?.rendered.orEmpty()

                        binding.postBackdrop.load(mediaUrl) {
                            crossfade(true)
                        }
                    }
                }

                override fun onFailure(call: Call<Media?>, t: Throwable) {}
            })
        } else {
            Snackbar.make(binding.root, "Can't connect to the Internet", Snackbar.LENGTH_INDEFINITE)
                .show()
        }

        binding.content.postTitle.text = title
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        val id = intent.getIntExtra("postId", -1)
        val title = intent.getStringExtra("postTitle").orEmpty()
        val excerpt = intent.getStringExtra("postExcerpt").orEmpty()

        if (!isItemSelected) {
            item.icon = ContextCompat.getDrawable(context, R.drawable.ic_heart_selected)
            isItemSelected = true
            PostDB.getInstance(applicationContext)?.insert(id, title, excerpt, isItemSelected)
        } else {
            item.icon = ContextCompat.getDrawable(context, R.drawable.ic_heart_unselected)
            isItemSelected = false
            PostDB.getInstance(applicationContext)?.delete(id)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar(title: String, id: Int) {
        setSupportActionBar(binding.postToolbar)
        binding.postCollapsingToolbarLayout.title = title

        isItemSelected = PostDB.getInstance(applicationContext)?.getDbPostIsFav(id) ?: false
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.postToolbar.setNavigationOnClickListener { finish() }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.add_to_favorite_menu, menu)
        val favoriteItem = menu.findItem(R.id.add_as_favorite)

        if (isItemSelected) {
            favoriteItem.icon = ContextCompat.getDrawable(context, R.drawable.ic_heart_selected)
        } else {
            favoriteItem.icon = ContextCompat.getDrawable(context, R.drawable.ic_heart_unselected)
        }
        return true
    }

    companion object {
        fun createIntent(
            context: Context?, id: Int, featuredMedia: Int, title: String?,
            excerpt: String?, content: String?,
        ): Intent {
            return Intent(context, WordpressDetailsActivity::class.java).apply {
                putExtra("postId", id)
                putExtra("featuredMedia", featuredMedia)
                putExtra("postExcerpt", excerpt)
                putExtra("postTitle", title)
                putExtra("postContent", content)
            }
        }
    }
}