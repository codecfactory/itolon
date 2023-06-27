package com.example.myapplication.adapters

import android.content.Context
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
import com.example.myapplication.model.playlistmodel.PlaylistResult
import kotlinx.android.synthetic.main.cell_albums.view.*
import kotlinx.android.synthetic.main.cell_albums.view.cell_layout

class PlaylistsAdapter(val items: List<PlaylistResult>, val context: Context, val playListClickListener: PlayListClickListener) : RecyclerView.Adapter<PlaylistsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        return PlaylistsViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_albums, parent, false))
    }
    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
            try {
                holder.mAlbumTitleTextView.text=items[position].name
                Glide.with(context).load("http://44.231.47.188"+items[position].imagePath).transform(
                    RoundedCorners(10)
                ).placeholder(R.drawable.banner).error(R.drawable.banner).into(holder.mSongImageView)
                holder.cellLayout.setOnClickListener {
                    playListClickListener.onClick(position,items[position].playlistId,"http://44.231.47.188"+items[position].imagePath)
                }
            } catch (e: Exception) {
                    e.printStackTrace()
            }
    }
    // Gets the number of data in the list
    override fun getItemCount(): Int {
        return items.size
    }
}
interface PlayListClickListener {
    fun onClick( position: Int,id:Int,imagePath:String)
}
class PlaylistsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val cellLayout:RelativeLayout=view.cell_layout
    val mSongImageView: ImageView =view.category_imageview
    val mAlbumTitleTextView:TextView=view.album_title
}