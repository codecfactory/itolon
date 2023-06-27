package com.example.myapplication.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.activities.PostsActivity
import com.example.myapplication.activities.PostsDescriptionActivity
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import com.example.myapplication.model.postsmodel.PostResult
import kotlinx.android.synthetic.main.cell_posts.view.*

class PostsAdapter(val items: List<PostResult>, val context: Context, val fragment: PostsActivity) :
    RecyclerView.Adapter<PostsViewHolder>() {
    private var selectPosition: Int = -1
    private var oldPosition: Int = -1
    private var mClickInterface: FeedLikeClickInterface? = null
    private var mPostDetails: ClickInterface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        return PostsViewHolder(
            LayoutInflater.from(context).inflate(R.layout.cell_posts, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        try {
            holder.cellLayout.setOnClickListener {
                try {
                    if(!items[position].content[0].filePath.isNullOrEmpty()){
                    context.startActivity(Intent(context, PostsDescriptionActivity::class.java).putExtra("title",items[position].title).putExtra("description",items[position].description).putExtra("imageUrl","http://44.231.47.188" + items[position].content[0].filePath).putExtra("postId",items[position].id.toString()).putExtra("commentsList",items[position]))
                }else{
                        context.startActivity(Intent(context, PostsDescriptionActivity::class.java).putExtra("title",items[position].title).putExtra("description",items[position].description).putExtra("postId",items[position].id.toString()).putExtra("commentsList",items[position]))
                    }
                } catch (e: Exception) {
                    context.startActivity(Intent(context, PostsDescriptionActivity::class.java).putExtra("title",items[position].title).putExtra("description",items[position].description).putExtra("postId",items[position].id.toString()).putExtra("commentsList",items[position]))
                    e.printStackTrace()
                }
            }
            holder.mTitleTextView.text=items[position].title
            holder.mDescriptionTextView.text=items[position].description
            if(!items[position].content[0].filePath.isNullOrEmpty()) {
                Glide.with(context).load("http://44.231.47.188" + items[position].content[0].filePath).error(R.drawable.maxresdefault).placeholder(R.drawable.maxresdefault).into(holder.mPostImageView)
            }
            }catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Gets the number of data in the list
    override fun getItemCount(): Int {
        return items.size
    }
}

class PostsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var cellLayout: LinearLayout = view.cell_layout
    var mTitleTextView: TextView = view.title_textview
    var mDescriptionTextView: TextView = view.description_textview
    var mPostImageView: ImageView = view.post_imageview

}