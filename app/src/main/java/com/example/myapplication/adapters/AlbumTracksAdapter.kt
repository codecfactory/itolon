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
import com.example.myapplication.activities.ArtistsActivity
import com.example.myapplication.activities.PlaylistActivity
import com.example.myapplication.activities.PlaylistsActivity
import com.example.myapplication.model.artistdetailmodel.Song
import kotlinx.android.synthetic.main.cell_popular_track.view.*
import kotlinx.android.synthetic.main.cell_popular_track.view.song_description_textview
import kotlinx.android.synthetic.main.cell_popular_track.view.song_imageview
import kotlinx.android.synthetic.main.cell_popular_track.view.song_title_textview
import kotlinx.android.synthetic.main.cell_popular_track.view.track_counter_textview

class AlbumTracksAdapter(val items: List<Song>, val context: Context, val fragment: ArtistsActivity,val clickListener: SongItemClickListener) :
    RecyclerView.Adapter<AlbumTracksViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumTracksViewHolder {
        return AlbumTracksViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_popular_track, parent, false))
    }
    override fun onBindViewHolder(holder: AlbumTracksViewHolder, position: Int) {
        try {
            holder.cellLayout.setOnClickListener {
                clickListener.onClick(position, "http://44.231.47.188/" + items[position].content.filePath)
            }
            holder.mAddToPlayListImageView.setOnClickListener {
                context.startActivity(Intent(context, PlaylistsActivity::class.java).putExtra("id",items[position].id).putExtra("title","Select Playlist"))
            }
            holder.trackCounterTextView.text = (position + 1).toString()
            holder.mSongTitleTextView.text = items[position].name
            holder.mSongDescriptionTextView.text = items[position].description
            if(!items[position].duration.isNullOrEmpty()) {
                holder.mDurationTextView.text = items[position].duration.toString()
            }
            Glide.with(context).load("http://44.231.47.188/" + items[position].imagePath).transform(
                RoundedCorners(10)
            )
                .placeholder(R.drawable.banner).error(R.drawable.banner).into(holder.mSongImageView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun getItemCount(): Int {
        return items.size
    }
}
interface SongItemClickListener {
    fun onClick( position: Int,songUrl:String)
}

class AlbumTracksViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var cellLayout: RelativeLayout = view.cell_layout
    val trackCounterTextView:TextView=view.track_counter_textview
    val mSongTitleTextView:TextView=view.song_title_textview
    val mSongDescriptionTextView:TextView=view.song_description_textview
    val mDurationTextView:TextView=view.duration_textview
    val mSongImageView:ImageView=view.song_imageview
    val mAddToPlayListImageView:ImageView=view.add_to_playlist_imageview
}