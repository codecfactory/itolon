package com.example.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.myapplication.R
import com.example.myapplication.activities.ArtistsActivity
import com.example.myapplication.activities.PlaylistActivity
import com.example.myapplication.fragments.FeedFragment
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import com.example.myapplication.model.artistdetailmodel.Song
import kotlinx.android.synthetic.main.cell_artists_album.view.*

class ArtistAlbumAdapter(val items: List<Song>, val context: Context, val fragment: ArtistsActivity) :
    RecyclerView.Adapter<ArtistAlbumViewHolder>() {
    private var selectPosition: Int = -1
    private var oldPosition: Int = -1
    private var mClickInterface: FeedLikeClickInterface? = null
    private var mPostDetails: ClickInterface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistAlbumViewHolder {
        return ArtistAlbumViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_artists_album, parent, false))
    }
    override fun onBindViewHolder(holder: ArtistAlbumViewHolder, position: Int) {
        try {
                holder.mSongTitleTextView.text=items[position].name
                holder.mSongDescriptionTextView.text=items[position].description
            Glide.with(context).load("http://44.231.47.188/"+items[position].imagePath).transform(
                RoundedCorners(10)
            ).placeholder(R.drawable.banner).error(R.drawable.banner).into(holder.mSongThumbnailImageView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun getItemCount(): Int {
        return items.size
    }
}

class ArtistAlbumViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mSongThumbnailImageView:ImageView=view.profile_image
    val mSongTitleTextView:TextView=view.song_title_textview
    val mSongDescriptionTextView:TextView=view.song_description_textview

}