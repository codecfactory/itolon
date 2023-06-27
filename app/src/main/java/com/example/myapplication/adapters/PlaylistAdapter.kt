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
import com.example.myapplication.activities.PlaylistActivity
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import com.example.myapplication.model.playlistdetailmodel.Song
import kotlinx.android.synthetic.main.cell_playlist.view.*

class PlaylistAdapter(val items: List<Song>, val context: Context, val fragment: PlaylistActivity, val clickListener: PlaylistItemClickListener) :
    RecyclerView.Adapter<PlaylistViewHolder>() {
    private var selectPosition: Int = -1
    private var oldPosition: Int = -1
    private var mClickInterface: FeedLikeClickInterface? = null
    private var mPostDetails: ClickInterface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        return PlaylistViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_playlist, parent, false))
    }
    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
            try {
                holder.cellLayout.setOnClickListener {
//                    context.startActivity(Intent(context,TeaserActivity::class.java))
                    clickListener.onClick(position,items[position].content.filePath)
                }
                holder.mSongTitleTextView.text=items[position].name
                holder.mDurationTextView.text=items[position].duration
                if(items[position].price!=null) {
                    holder.mPriceTextView.text = "€ "+items[position].price.toString()
                }else{
                    holder.mPriceTextView.text = "Free"
                }
                Glide.with(context).load("http://44.231.47.188/"+items[position].imagePath).transform(
                    RoundedCorners(10)
                ).placeholder(R.drawable.banner).error(R.drawable.banner).into(holder.mSongImageView)

            } catch (e: ClassCastException) {
                throw ClassCastException("Fragment must implement AdapterCallback.")
            }
    }
    // Gets the number of data in the list
    override fun getItemCount(): Int {
        return items.size
    }
}
interface PlaylistItemClickListener {
    fun onClick( position: Int,songUrl:String)
}
class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var cellLayout:RelativeLayout=view.cell_layout

    val mSongTitleTextView: TextView =view.song_title_textview
    val mDurationTextView: TextView =view.duration_textview
    val mPriceTextView: TextView =view.price_textview
    val mSongImageView: ImageView =view.song_imageview

}