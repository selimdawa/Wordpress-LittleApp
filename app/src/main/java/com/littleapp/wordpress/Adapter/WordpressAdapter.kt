package com.littleapp.wordpress.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.littleapp.wordpress.Activity.WordpressDetailsActivity
import com.littleapp.wordpress.Model.Post
import com.littleapp.wordpress.databinding.ItemWordpressBinding

class WordpressAdapter(
    private val context: Context,
    private val posts: List<Post>
) : RecyclerView.Adapter<WordpressAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWordpressBinding.inflate(inflater, parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size

    inner class PostViewHolder(private val binding: ItemWordpressBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            val title = post.title?.get("rendered").toString().replace("\"", "")
            val excerpt = post.excerpt?.get("rendered").toString().replace("\"", "")

            binding.title.text = HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY)
            binding.content.text = HtmlCompat.fromHtml(excerpt, HtmlCompat.FROM_HTML_MODE_LEGACY)

            itemView.setOnClickListener {
                val cleanTitle = post.title?.get("rendered").toString().replace("\"", "")
                var content = post.content?.get("rendered").toString().replace("\"", "")
                val cleanExcerpt = post.excerpt?.get("rendered").toString().replace("\"", "")

                content = contentFilter(content)
                content = videoFilter(content)

                val formattedTitle = HtmlCompat.fromHtml(cleanTitle, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

                val intent = WordpressDetailsActivity.createIntent(
                    context,
                    post.id,
                    post.featured_media,
                    formattedTitle,
                    cleanExcerpt,
                    content
                )
                context.startActivity(intent)
            }
        }

        private fun contentFilter(content: String): String {
            val first = "<ins"
            val last = "</ins>"
            val firstIndex = content.indexOf(first)
            val lastIndex = content.lastIndexOf(last)

            return if (firstIndex != -1 && lastIndex != -1) {
                val contentOutput = content.substring(firstIndex, lastIndex + last.length)
                content.replace(contentOutput, "")
            } else {
                content
            }
        }

        private fun videoFilter(content: String): String {
            val first = "<iframe"
            val last = "/iframe>"
            val firstIndex = content.indexOf(first)
            val lastIndex = content.lastIndexOf(last)

            return if (firstIndex != -1 && lastIndex != -1) {
                val oldContentSubstring = content.substring(firstIndex, lastIndex + last.length)
                val newContentSubstring = "<div class=\"videoWrapper\">$oldContentSubstring</div>"
                content.replace(oldContentSubstring, newContentSubstring)
            } else {
                content
            }
        }
    }
}