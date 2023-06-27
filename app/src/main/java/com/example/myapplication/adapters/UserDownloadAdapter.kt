package com.example.myapplication.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.myapplication.R
import com.example.myapplication.activities.*
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import com.example.myapplication.model.albummodel.Result
import com.example.myapplication.model.allartistmodel.AllArtistResult
import com.example.myapplication.model.starsmodel.StarResult
import com.example.myapplication.model.userdownloadsmodel.UserDownloadResult
import kotlinx.android.synthetic.main.cell_albums.view.*

class UserDownloadAdapter(val items: List<UserDownloadResult>, val context: Context, val fragment: UserDownloadActivity,val downloadSongClickListener: DownloadSongClickListener) :
    RecyclerView.Adapter<UserDownloadsViewHolder>() {
    private var selectPosition: Int = -1
    private var oldPosition: Int = -1
    private var mClickInterface: FeedLikeClickInterface? = null
    private var mPostDetails: ClickInterface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserDownloadsViewHolder {
        return UserDownloadsViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_albums, parent, false))
    }
    override fun onBindViewHolder(holder: UserDownloadsViewHolder, position: Int) {
            try {
//                this.mClickInterface = fragment
//                this.mPostDetails = fragment
                holder.mAlbumTitleTextView.text=items[position].name
                Glide.with(context).load("http://44.231.47.188"+items[position].imagePath).transform(
                    RoundedCorners(10)
                ).placeholder(R.drawable.banner).error(R.drawable.banner).into(holder.mSongImageView)

                holder.cellLayout.setOnClickListener {
                    downloadSongClickListener.onClick(position,"http://44.231.47.188"+items[position].content.filePath)
                }
            } catch (e: ClassCastException) {
                throw ClassCastException("Fragment must implement AdapterCallback.")
            }
//
    }
    // Gets the number of data in the list
    override fun getItemCount(): Int {
        return items.size
    }
}
interface DownloadSongClickListener {
    fun onClick( position: Int,songUrl:String)
}
class UserDownloadsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val cellLayout:RelativeLayout=view.cell_layout
    val mAlbumTitleTextView:TextView=view.album_title
    val mSongImageView: ImageView =view.category_imageview
}