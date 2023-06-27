package com.example.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.activities.CartActivity
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import com.example.myapplication.model.albumdetailmodel.Song
import kotlinx.android.synthetic.main.cell_cart.view.*

class CartAdapter(val items: List<Song>, val context: Context,val clickListener: SongClickListener) :
    RecyclerView.Adapter<CartViewHolder>() {
    private var selectPosition: Int = -1
    private var oldPosition: Int = -1
    private var mClickInterface: FeedLikeClickInterface? = null
    private var mPostDetails: ClickInterface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(
            LayoutInflater.from(context).inflate(R.layout.cell_cart, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        try {
            holder.cellLayout.setOnClickListener {
                clickListener.onClick(position,"http://44.231.47.188"+items[position].content.filePath)
            }
            holder.mSongTitleTextView.text=items[position].name
            holder.mSongDescriptionTextView.text=items[position].description
            Glide.with(context).load("http://44.231.47.188"+items[position].imagePath).placeholder(R.drawable.banner).error(R.drawable.banner).into(holder.mSongImageView)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Gets the number of data in the list
    override fun getItemCount(): Int {
        return items.size
    }
}
interface SongClickListener {
    fun onClick( position: Int,songUrl:String)
}

class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var cellLayout: LinearLayout = view.cell_layout
    val mSongTitleTextView: TextView =view.song_title_textview
    val mSongDescriptionTextView: TextView =view.song_description_textview
    val mSongImageView: ImageView =view.song_imageview

}