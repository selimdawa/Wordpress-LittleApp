package com.littleapp.wordpress.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.R.attr.colorError
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.littleapp.wordpress.R
import com.littleapp.wordpress.databinding.ActivityWordpressDetailsBinding
import com.littleapp.wordpress.model.Media
import com.littleapp.wordpress.sqlite.PostDB
import com.littleapp.wordpress.utils.DATA
import com.littleapp.wordpress.utils.WPApiService
import com.littleapp.wordpress.utils.WordPressClient
import com.littleapp.wordpress.utils.isNetworkAvailable
import com.littleapp.wordpress.utils.loadWordPressContent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WordpressDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWordpressDetailsBinding
    private val context: Context = this
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityWordpressDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val margin16 = (16 * resources.displayMetrics.density).toInt()
            binding.backButton.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = systemBars.top + margin16
            }
            binding.favoriteButton.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = systemBars.top + margin16
            }
            insets
        }

        val id = intent.getIntExtra(DATA.POST_ID, -1)
        val featuredMedia = intent.getIntExtra(DATA.FEATURED_MEDIA, -1)
        val title = intent.getStringExtra(DATA.POST_TITLE).orEmpty()
        val excerpt = intent.getStringExtra(DATA.POST_EXCERPT).orEmpty()
        val contentPost =
            intent.getStringExtra(DATA.POST_CONTENT).orEmpty().replace("\\\\n".toRegex(), "<br>")
                .replace("\\\\r".toRegex(), "").replace("\\\\".toRegex(), "")

        val typedValue = TypedValue()
        theme.resolveAttribute(colorError, typedValue, true)
        val hexColor = String.format("#%06X", 0xFFFFFF and typedValue.data)

        binding.content.postTitle.text = title
        binding.content.webview.loadWordPressContent(contentPost, hexColor)

        isFavorite = PostDB.getInstance(applicationContext)?.getDbPostIsFav(id) ?: false
        updateFavoriteUI()

        binding.backButton.setOnClickListener { finish() }
        binding.favoriteButton.setOnClickListener {
            if (!isFavorite) {
                isFavorite = true
                PostDB.getInstance(applicationContext)?.insert(id, title, excerpt, isFavorite)
            } else {
                isFavorite = false
                PostDB.getInstance(applicationContext)?.delete(id)
            }
            updateFavoriteUI()
        }

        if (isNetworkAvailable()) {
            val api: WPApiService = WordPressClient.apiService
            val call: Call<Media?>? = api.getPostThumbnail(featuredMedia)

            call?.enqueue(object : Callback<Media?> {
                override fun onResponse(call: Call<Media?>, response: Response<Media?>) {
                    if (response.code() != 404) {
                        val mediaUrl = response.body()?.guid?.rendered.orEmpty()
                        binding.postBackdrop.load(mediaUrl) {
                            crossfade(enable = true)
                        }
                    }
                }

                override fun onFailure(call: Call<Media?>, t: Throwable) {}
            })
        } else {
            Snackbar.make(binding.root, R.string.connect_internet, Snackbar.LENGTH_INDEFINITE)
                .show()
        }
    }

    private fun updateFavoriteUI() {
        val icon = if (isFavorite) R.drawable.ic_heart_selected else R.drawable.ic_heart_unselected
        binding.favoriteButton.setImageDrawable(ContextCompat.getDrawable(context, icon))
    }

    companion object {
        fun createIntent(
            context: Context?, id: Int, featuredMedia: Int, title: String?,
            excerpt: String?, content: String?,
        ): Intent {
            return Intent(context, WordpressDetailsActivity::class.java).apply {
                putExtra(DATA.POST_ID, id)
                putExtra(DATA.FEATURED_MEDIA, featuredMedia)
                putExtra(DATA.POST_EXCERPT, excerpt)
                putExtra(DATA.POST_TITLE, title)
                putExtra(DATA.POST_CONTENT, content)
            }
        }
    }
}