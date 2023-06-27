package com.example.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.myapplication.R
import com.example.myapplication.activities.ArtistProfileActivity
import com.example.myapplication.activities.PlaylistActivity
import com.example.myapplication.fragments.FeedFragment
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import com.example.myapplication.model.artisttracks.ArtistTrack
import com.example.myapplication.model.artisttracks.TrackResult
import kotlinx.android.synthetic.main.cell_artist_tracks.view.*
import kotlinx.android.synthetic.main.cell_feed_list.view.*

class ArtistTrackAdapter(val items: List<TrackResult>, val context: Context, val fragment: ArtistProfileActivity, var  clickListener: ClickListener) : RecyclerView.Adapter<ArtistsTrackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistsTrackViewHolder {
        return ArtistsTrackViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_artist_tracks, parent, false))
    }
    override fun onBindViewHolder(holder: ArtistsTrackViewHolder, position: Int) {
        try {
            Glide.with(context).load("http://44.231.47.188/"+items[position].imagePath).transform(
                RoundedCorners(10)
            ).error(R.drawable.app_image).error(R.drawable.app_image).into(holder.mPostImageView)
            holder.mCrossImageView.setOnClickListener {
                clickListener.onClick(position,items[position].id)
            }
        } catch (ignored: Exception) {
        }
    }
    override fun getItemCount(): Int {
        return items.size
    }
}
interface ClickListener {
    fun onClick( position: Int,songId:Int)
}
class ArtistsTrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mPostImageView: ImageView =view.image
    val mCrossImageView: ImageView =view.cross_icon_imageview
}