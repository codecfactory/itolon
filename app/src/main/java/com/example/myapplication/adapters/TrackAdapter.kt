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
import com.example.myapplication.activities.ArtistsActivity
import com.example.myapplication.activities.PlaylistActivity
import com.example.myapplication.fragments.FeedFragment
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import com.example.myapplication.model.artistdetailmodel.Similar
import kotlinx.android.synthetic.main.cell_artists.view.*

class TrackAdapter(val items: List<Similar>, val context: Context, val fragment: ArtistsActivity) :
    RecyclerView.Adapter<TrackViewHolder>() {
    private var selectPosition: Int = -1
    private var oldPosition: Int = -1
    private var mClickInterface: FeedLikeClickInterface? = null
    private var mPostDetails: ClickInterface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_artists, parent, false))
    }
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
            try {
                Glide.with(context).load("http://44.231.47.188/"+items[position].photo).transform(
                    RoundedCorners(10)
                ).error(R.drawable.banner).placeholder(R.drawable.banner).into(holder.mSimilarArtistImageView)
            } catch (e: ClassCastException) {
               e.printStackTrace()
            }

    }
    // Gets the number of data in the list
    override fun getItemCount(): Int {
        return items.size
    }
}

class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mSimilarArtistImageView:ImageView=view.image

}