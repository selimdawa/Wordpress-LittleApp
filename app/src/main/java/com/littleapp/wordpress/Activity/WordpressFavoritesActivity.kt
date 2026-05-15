package com.littleapp.wordpress.Activity

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.littleapp.wordpress.R
import com.littleapp.wordpress.Unit.THEME
import com.littleapp.wordpress.Adapter.WordpressAdapter
import com.littleapp.wordpress.Model.Post
import com.littleapp.wordpress.Sqlite.PostDB
import com.littleapp.wordpress.Util.InternetConnection
import com.littleapp.wordpress.Util.WPApiService
import com.littleapp.wordpress.Util.WordPressClient
import com.littleapp.wordpress.databinding.ActivityWordpressFavoritesBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WordpressFavoritesActivity : AppCompatActivity() {

    private var binding: ActivityWordpressFavoritesBinding? = null
    var context: Context = this@WordpressFavoritesActivity
    private var favPost: View? = null
    private var sqLitePostList: List<Post?>? = null
    private var postList: List<Post?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        binding = ActivityWordpressFavoritesBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        favPost = binding!!.item
        binding!!.toolbar.back.visibility = View.VISIBLE
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        binding!!.toolbar.nameSpace.setText(R.string.favorites)

        sqLitePostList = PostDB.getInstance(applicationContext)!!.allDbPosts
        setFavListContent(true, sqLitePostList)
    }

    fun setFavListContent(withProgress: Boolean, favPostList: List<Post?>?) {
        if (InternetConnection.checkInternetConnection(applicationContext)) {
            val api: WPApiService = WordPressClient.apiService
            val call: Call<List<Post?>?> = api.getPosts()!!

            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle(getString(R.string.progressdialog_title))
            progressDialog.setMessage(getString(R.string.progressdialog_message))
            if (withProgress) {
                progressDialog.show()
            }

            call.enqueue(object : Callback<List<Post?>?> {
                override fun onResponse(
                    call: Call<List<Post?>?>, response: Response<List<Post?>?>,
                ) {
                    val myList: ArrayList<Post> = ArrayList()
                    postList = response.body()
                    for (post in postList!!) {
                        for (dbPost in favPostList!!) {
                            if (dbPost != null) {
                                if (post!!.id === dbPost.wpPostId) {
                                    myList.add(post)
                                }
                            }
                        }
                    }
                    //binding.recyclerView.setHasFixedSize(true);
                    binding!!.recyclerView.adapter =
                        WordpressAdapter(applicationContext, myList)
                    if (withProgress) {
                        progressDialog.dismiss()
                    }
                }

                override fun onFailure(call: Call<List<Post?>?>, t: Throwable) {
                    if (withProgress) {
                        progressDialog.dismiss()
                    }
                }
            })
        } else {
            Snackbar.make(favPost!!, "Can't connect to the Internet", Snackbar.LENGTH_INDEFINITE)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        sqLitePostList = PostDB.getInstance(applicationContext)!!.allDbPosts
        setFavListContent(true, sqLitePostList)
    }
}