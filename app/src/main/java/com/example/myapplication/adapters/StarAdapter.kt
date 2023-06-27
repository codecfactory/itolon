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
import com.example.myapplication.activities.AlbumActivity
import com.example.myapplication.activities.AlbumsActivity
import com.example.myapplication.activities.ArtistsActivity
import com.example.myapplication.activities.StarsActivity
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import com.example.myapplication.model.albummodel.Result
import com.example.myapplication.model.starsmodel.StarResult
import kotlinx.android.synthetic.main.cell_albums.view.*
import java.lang.Exception

class StarAdapter(val items: List<StarResult>, val context: Context, val fragment: StarsActivity) :
    RecyclerView.Adapter<StarViewHolder>() {
    private var selectPosition: Int = -1
    private var oldPosition: Int = -1
    private var mClickInterface: FeedLikeClickInterface? = null
    private var mPostDetails: ClickInterface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StarViewHolder {
        return StarViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_albums, parent, false))
    }
    override fun onBindViewHolder(holder: StarViewHolder, position: Int) {
            try {
//                this.mClickInterface = fragment
//                this.mPostDetails = fragment
                holder.mAlbumTitleTextView.text=items[position].name
                Glide.with(context).load("http://44.231.47.188/"+items[position].photo).transform(
                    RoundedCorners(10)
                ).placeholder(R.drawable.banner).error(R.drawable.banner).into(holder.mSongImageView)

                holder.cellLayout.setOnClickListener {
                    context.startActivity(Intent(context, AlbumsActivity::class.java).putExtra("artistId",items[position].artistId.toString()).putExtra("name",items[position].name))
//                    context.startActivity(Intent(context, ArtistsActivity::class.java).putExtra("artistId",items[position].artistId.toString()).putExtra("name",items[position].name))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
//
    }
    // Gets the number of data in the list
    override fun getItemCount(): Int {
        return items.size
    }
}

class StarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val cellLayout:RelativeLayout=view.cell_layout
    val mAlbumTitleTextView:TextView=view.album_title
    val mSongImageView: ImageView =view.category_imageview
}