package com.littleapp.wordpress.activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.littleapp.wordpress.R
import com.littleapp.wordpress.utils.applyAppTheme
import com.littleapp.wordpress.utils.isNetworkAvailable
import com.littleapp.wordpress.utils.launchActivity
import com.littleapp.wordpress.adapter.WordpressAdapter
import com.littleapp.wordpress.model.Post
import com.littleapp.wordpress.utils.WPApiService
import com.littleapp.wordpress.utils.WordPressClient
import com.google.android.material.snackbar.Snackbar
import com.littleapp.wordpress.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val context: Context = this
    private var postItemList: List<Post?>? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        applyAppTheme()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.nameSpace.setText(R.string.wordpress_app)

        binding.main.setOnRefreshListener {
            binding.main.isRefreshing = true
            handler.postDelayed({
                binding.main.isRefreshing = false
                setListContent(false)
            }, 3000)
        }

        binding.toolbar.favorites.setOnClickListener {
            context.launchActivity(WordpressFavoritesActivity::class.java)
        }

        setListContent(true)
    }

    fun setListContent(withProgress: Boolean) {
        if (isNetworkAvailable()) {
            val api: WPApiService = WordPressClient.apiService
            val call: Call<List<Post?>?>? = api.getPosts()

            if (call == null) {
                binding.main.isRefreshing = false
                return
            }

            if (withProgress) {
                binding.progressBar.visibility = View.VISIBLE
            }

            call.enqueue(object : Callback<List<Post?>?> {
                override fun onResponse(
                    call: Call<List<Post?>?>, response: Response<List<Post?>?>
                ) {
                    binding.progressBar.visibility = View.GONE
                    val body = response.body()
                    if (body != null) {
                        postItemList = body
                        val safeList = body.filterNotNull()
                        binding.recyclerView.adapter = WordpressAdapter(context, safeList)
                    }
                }

                override fun onFailure(call: Call<List<Post?>?>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                }
            })
        } else {
            binding.main.isRefreshing = false
            Snackbar.make(
                binding.main, "Can't connect to the Internet", Snackbar.LENGTH_INDEFINITE
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}