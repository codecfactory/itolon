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
import com.example.myapplication.model.categoriessongmodel.CategoriesResult
import kotlinx.android.synthetic.main.activity_teaser.*
import kotlinx.android.synthetic.main.cell_albums.view.*

class CategoriesAdapter(val items: List<CategoriesResult>, val context: Context, val clickListener: CategoriesItemClickListener) :
    RecyclerView.Adapter<CategoriesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        return CategoriesViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_albums, parent, false))
    }
    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
            try {
                holder.cellLayout.setOnClickListener {
                    clickListener.onClick(position,items[position].name)
                }
                holder.mSongTitleTextView.text=items[position].name
                Glide.with(context).load("http://44.231.47.188"+items[position]!!.filePath).transform(RoundedCorners(10)).placeholder(R.drawable.banner).error(R.drawable.banner).into(holder.mSongCategoryImageView)
//                Glide.with(context).load("http://44.231.47.188/songsContent/1599718251.jpg")
//                    .placeholder(R.drawable.banner).error(R.drawable.banner).into(holder.mSongCategoryImageView)
            } catch (e: ClassCastException) {
                throw ClassCastException("Fragment must implement AdapterCallback.")
            }
    }
    // Gets the number of data in the list
    override fun getItemCount(): Int {
        return items.size
    }
}
interface CategoriesItemClickListener {
    fun onClick( position: Int,name:String)
}
class CategoriesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var cellLayout:RelativeLayout=view.cell_layout
    val mSongTitleTextView: TextView =view.album_title
    val mSongCategoryImageView: ImageView =view.category_imageview
}