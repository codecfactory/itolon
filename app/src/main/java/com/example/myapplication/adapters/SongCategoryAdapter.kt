package com.example.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.activities.ArtistProfileActivity
import com.example.myapplication.activities.PlaylistActivity
import com.example.myapplication.fragments.FeedFragment
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import com.example.myapplication.model.artisttracks.ArtistTrack
import com.example.myapplication.model.artisttracks.TrackResult
import com.example.myapplication.model.categorymodel.CategoryResult
import kotlinx.android.synthetic.main.cell_category_list.view.*

class SongCategoryAdapter(val items: List<CategoryResult>, val context: Context, val fragment: ArtistProfileActivity, var  clickListener: ClickListener) : RecyclerView.Adapter<CategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_category_list, parent, false))
    }
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        try {
            if(!items[position].name.isNullOrEmpty()) {
                holder.mCategoryTitleTextView.text = items[position].name
            }
            holder.mCategoryTitleTextView.setOnClickListener {
                clickListener.onClick(position,items[position].id)
            }
        } catch (ignored: Exception) {
        }
    }
    override fun getItemCount(): Int {
        return items.size
    }
}
interface CategoryClickListener {
    fun onClick( position: Int,songId:Int)
}
class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mCategoryTitleTextView: TextView =view.category_title_textview
}