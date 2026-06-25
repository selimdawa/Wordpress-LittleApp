package com.littleapp.wordpress.Activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.littleapp.wordpress.R
import com.littleapp.wordpress.Unit.CLASS
import com.littleapp.wordpress.Unit.THEME
import com.littleapp.wordpress.Unit.VOID
import com.littleapp.wordpress.Adapter.WordpressAdapter
import com.littleapp.wordpress.Model.Post
import com.littleapp.wordpress.Util.InternetConnection
import com.littleapp.wordpress.Util.WPApiService
import com.littleapp.wordpress.Util.WordPressClient
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
        THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.nameSpace.setText(R.string.wordpress_app)

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = true
            handler.postDelayed({
                binding.swipeRefresh.isRefreshing = false
                setListContent(false)
            }, 3000)
        }

        binding.toolbar.favorites.setOnClickListener {
            VOID.Intent1(context, CLASS.WORDPRESS_FAVORITES)
        }

        setListContent(true)
    }

    fun setListContent(withProgress: Boolean) {
        if (InternetConnection.checkInternetConnection(applicationContext)) {
            val api: WPApiService = WordPressClient.apiService
            val call: Call<List<Post?>?>? = api.getPosts()

            if (call == null) {
                binding.swipeRefresh.isRefreshing = false
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
            binding.swipeRefresh.isRefreshing = false
            Snackbar.make(
                binding.swipeRefresh, "Can't connect to the Internet", Snackbar.LENGTH_INDEFINITE
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}