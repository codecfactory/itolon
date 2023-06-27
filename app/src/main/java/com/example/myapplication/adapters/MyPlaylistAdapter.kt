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
import com.example.myapplication.R
import com.example.myapplication.activities.MyPlaylistActivity
import com.example.myapplication.activities.PlaylistActivity
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import kotlinx.android.synthetic.main.cell_my_playlist.view.*
import kotlinx.android.synthetic.main.cell_playlist.view.cell_layout

class MyPlaylistAdapter(val items: List<com.example.myapplication.model.playlistmodel.PlaylistResult>, val context: Context, val fragment: MyPlaylistActivity, val clickListener: MyPlaylistItemClickListener) :
    RecyclerView.Adapter<MyPlaylistViewHolder>() {
    private var selectPosition: Int = -1
    private var oldPosition: Int = -1
    private var mClickInterface: FeedLikeClickInterface? = null
    private var mPostDetails: ClickInterface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPlaylistViewHolder {
        return MyPlaylistViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_my_playlist, parent, false))
    }
    override fun onBindViewHolder(holder: MyPlaylistViewHolder, position: Int) {
            try {
                holder.mPlaylistTitleTextView.text=items[position].name
                holder.cellLayout.setOnClickListener {
                    context.startActivity(Intent(context,PlaylistActivity::class.java).putExtra("id",items[position].playlistId.toString()).putExtra("title",items[position].name).putExtra("imagePath","http://44.231.47.188"+items[position].imagePath))
                }
                Glide.with(context).load("http://44.231.47.188"+items[position].imagePath).placeholder(R.drawable.banner).error(R.drawable.banner).into(holder.mSongImageView)

            } catch (e:Exception) {
                e.printStackTrace()
            }
    }
    // Gets the number of data in the list
    override fun getItemCount(): Int {
        return items.size
    }
}
interface MyPlaylistItemClickListener {
    fun onClick( position: Int,songUrl:String)
}
class MyPlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var cellLayout:RelativeLayout=view.cell_layout

    val mPlaylistTitleTextView: TextView =view.name_textview
    val mSongImageView: ImageView =view.song_imageview

}