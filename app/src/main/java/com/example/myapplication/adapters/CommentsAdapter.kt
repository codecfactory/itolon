package com.example.myapplication.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activities.*
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import com.example.myapplication.model.albummodel.Result
import com.example.myapplication.model.allartistmodel.AllArtistResult
import com.example.myapplication.model.playlistmodel.PlaylistResult
import com.example.myapplication.model.postsmodel.Comment
import com.example.myapplication.model.postsmodel.PostResult
import com.example.myapplication.model.starsmodel.StarResult
import kotlinx.android.synthetic.main.cell_albums.view.*
import kotlinx.android.synthetic.main.cell_albums.view.cell_layout
import kotlinx.android.synthetic.main.cell_comments.view.*

class CommentsAdapter(val items: List<Comment>, val context: Context, val playListClickListener: CommentsClickListener) : RecyclerView.Adapter<CommentsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        return CommentsViewHolder(
            LayoutInflater.from(context).inflate(R.layout.cell_comments, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        try {
//                this.mClickInterface = fragment
//                this.mPostDetails = fragment
            holder.mUserNameTextView.text = items[position].userInfo.name
            holder.mDescriptionTextView.text = items[position].body
//            holder.cellLayout.setOnClickListener {
////                    playListClickListener.onClick(position,items[position].playlistId)
//            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Gets the number of data in the list
    override fun getItemCount(): Int {
        return items.size
    }
}

interface CommentsClickListener {
    fun onClick(position: Int, id: Int)
}

class CommentsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//    val cellLayout: RelativeLayout = view.cell_layout
    val mUserNameTextView: TextView = view.comment_user_name
    val mDescriptionTextView: TextView = view.comments_description_textview
}