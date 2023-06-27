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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.myapplication.R
import com.example.myapplication.activities.CartActivity
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import com.example.myapplication.model.CartModel
import com.example.myapplication.model.albumdetailmodel.Song
import kotlinx.android.synthetic.main.cell_cart.view.*

class CartDetailAdapter(val items: List<CartModel>, val context: Context,val clickListener: CartClickListener) :
    RecyclerView.Adapter<CartDetailViewHolder>() {
    private var selectPosition: Int = -1
    private var oldPosition: Int = -1
    private var mClickInterface: FeedLikeClickInterface? = null
    private var mPostDetails: ClickInterface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartDetailViewHolder {
        return CartDetailViewHolder(
            LayoutInflater.from(context).inflate(R.layout.cell_cart, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CartDetailViewHolder, position: Int) {
        try {
            holder.cellLayout.setOnClickListener {
                clickListener.onClick(position,"http://44.231.47.188"+items[position].productName)
            }
            holder.mClearTextView.setOnClickListener {
                clickListener.onItemDeleteClick(position)
            }
            holder.mSongTitleTextView.text=items[position].productName
            if(items[position].price!=-1) {
                holder.mSongDescriptionTextView.text ="€ "+ items[position].price.toString()
            }else{
                holder.mSongDescriptionTextView.text = "Free"
            }
            Glide.with(context).load("http://44.231.47.188"+items[position].imageUrl).transform(
                RoundedCorners(10)
            ).placeholder(R.drawable.banner).error(R.drawable.banner).into(holder.mSongImageView)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Gets the number of data in the list
    override fun getItemCount(): Int {
        return items.size
    }
}
interface CartClickListener {
    fun onClick( position: Int,songUrl:String)
    fun onItemDeleteClick( position: Int)
}

class CartDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var cellLayout: LinearLayout = view.cell_layout
    val mSongTitleTextView: TextView =view.song_title_textview
    val mClearTextView: TextView =view.clear_textview
    val mSongDescriptionTextView: TextView =view.song_description_textview
    val mSongImageView: ImageView =view.song_imageview

}