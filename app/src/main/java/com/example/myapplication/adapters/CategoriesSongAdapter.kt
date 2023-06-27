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
import com.example.myapplication.activities.*
import com.example.myapplication.fragments.AlbumDetailFragment
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import com.example.myapplication.model.albummodel.Result
import com.example.myapplication.model.allartistmodel.AllArtistResult
import com.example.myapplication.model.categoriessongmodel.Song
import com.example.myapplication.model.starsmodel.StarResult
import com.example.myapplication.model.userdownloadsmodel.UserDownloadResult
import kotlinx.android.synthetic.main.cell_albums.view.*

class CategoriesSongAdapter(val items: List<Song>, val context: Context, val fragment: AlbumDetailFragment,val categorySongClickListener: CategorySongClickListener) :
    RecyclerView.Adapter<CategorySongViewHolder>() {

    private var mClickInterface: FeedLikeClickInterface? = null
    private var mPostDetails: ClickInterface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategorySongViewHolder {
        return CategorySongViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_albums, parent, false))
    }
    override fun onBindViewHolder(holder: CategorySongViewHolder, position: Int) {
            try {
                if(items[position].content!=null&&items[position].content.filePath!=null) {
                    holder.mAlbumTitleTextView.text = items[position].name
                    Glide.with(context).load("http://44.231.47.188" + items[position]!!.imagePath).transform( RoundedCorners(10)).placeholder(R.drawable.banner).error(R.drawable.banner).into(holder.mSongCategoryImageView)
                    holder.cellLayout.setOnClickListener {
                        if (items[position].content != null) {
                            categorySongClickListener.onClick(position, "http://44.231.47.188" + items[position].content.filePath, items[position])
                        }
                    }
                }
            } catch (e: ClassCastException) {
                throw ClassCastException("Fragment must implement AdapterCallback.")
            }
//
    }
    // Gets the number of data in the list
    override fun getItemCount(): Int {
        return items.size
    }
}
interface CategorySongClickListener {
    fun onClick( position: Int,songUrl:String,song:Song)
}
class CategorySongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val cellLayout:RelativeLayout=view.cell_layout
    val mAlbumTitleTextView:TextView=view.album_title
    val mSongCategoryImageView: ImageView =view.category_imageview

}