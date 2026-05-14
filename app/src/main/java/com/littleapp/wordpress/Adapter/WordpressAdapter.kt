package com.littleapp.wordpress.Adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.littleapp.wordpress.R
import com.littleapp.wordpress.Activity.WordpressDetailsActivity
import com.littleapp.wordpress.Model.Post

class WordpressAdapter(private val context: Context, posts: List<Post>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    private val posts: List<Post>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_wordpress, parent, false)
        return PostViewHolder(view)
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post: Post = posts[position]
        val postHolder = holder as PostViewHolder
        postHolder.setCurrentPost(post)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val cardPt: TextView
        private val cardEx: TextView
        private var currentPost: Post? = null

        @RequiresApi(api = Build.VERSION_CODES.N)
        fun setCurrentPost(post: Post) {
            currentPost = post
            val title: String = post.title!!.get("rendered").toString().replace("\"", "")
            val excerpt: String = post.excerpt?.get("rendered").toString().replace("\"", "")
            cardPt.text = Html.fromHtml(title, Html.FROM_HTML_MODE_LEGACY)
            cardEx.text = Html.fromHtml(excerpt, Html.FROM_HTML_MODE_LEGACY)
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        override fun onClick(v: View) {
            val title: String =
                currentPost!!.title!!.get("rendered").toString().replace("\"", "")
            var content: String =
                currentPost!!.content!!.get("rendered").toString().replace("\"", "")
            val excerpt: String =
                currentPost!!.excerpt!!.get("rendered").toString().replace("\"", "")
            content = contentFilter(content, "<ins", "</ins>")
            content = videoFilter(content, "<iframe", "/iframe>")
            val intent: Intent =
                WordpressDetailsActivity.createIntent(
                    v.context, currentPost!!.id,
                    currentPost!!.featured_media, Html.fromHtml(
                        title,
                        Html.FROM_HTML_MODE_LEGACY
                    ).toString(), excerpt, content
                )
            v.context.startActivity(intent)
        }

        fun contentFilter(content: String, first: String?, last: String): String {
            val contentOutput: String
            val contentResult: String
            //set index
            val firstIndex = content.indexOf(first!!)
            val lastIndex = content.lastIndexOf(last)
            if (firstIndex != -1 || lastIndex != -1) {
                //get substring
                contentOutput = content.substring(firstIndex, lastIndex + last.length)
                //replace
                contentResult = content.replace(contentOutput, "")
            } else {
                contentResult = content
            }
            return contentResult
        }

        fun videoFilter(content: String, first: String?, last: String): String {
            val oldContentSubstring: String
            val newContentSubstring: String
            val contentResult: String
            //set index
            val firstIndex = content.indexOf(first!!)
            val lastIndex = content.lastIndexOf(last)
            if (firstIndex != -1 || lastIndex != -1) {
                //get substring
                oldContentSubstring = content.substring(firstIndex, lastIndex + last.length)
                newContentSubstring = "<div class=\"videoWrapper\">$oldContentSubstring</div>"
                contentResult = content.replace(oldContentSubstring, newContentSubstring)
            } else {
                contentResult = content
            }
            return contentResult
        }

        init {
            cardPt = itemView.findViewById(R.id.title)
            cardEx = itemView.findViewById(R.id.content)
            itemView.setOnClickListener(this)
        }
    }

    //Constructor
    init {
        this.posts = posts
    }
}