package com.example.myapplication.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activities.CartActivity
import com.example.myapplication.activities.PlaylistActivity
import com.example.myapplication.fragments.AlbumDetailFragment
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import kotlinx.android.synthetic.main.cell_album_detail.view.*

class AlbumDetailAdapter(val items: List<String>, val context: Context, val fragment: AlbumDetailFragment) :
    RecyclerView.Adapter<AlbumDetailViewHolder>() {
    private var mClickInterface: FeedLikeClickInterface? = null
    private var mPostDetails: ClickInterface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumDetailViewHolder {
        return AlbumDetailViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_album_detail, parent, false))
    }
    override fun onBindViewHolder(holder: AlbumDetailViewHolder, position: Int) {
        try {
            try {
//                this.mClickInterface = fragment
//                this.mPostDetails = fragment
            } catch (e: ClassCastException) {
                throw ClassCastException("Fragment must implement AdapterCallback.")
            }
            holder.mCellLinearLayout.setOnClickListener {
                context.startActivity(Intent(context, CartActivity::class.java))
            }

        } catch (ignored: Exception) {
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class AlbumDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mCellLinearLayout: LinearLayout = view.cell_layout
//    val mUserNameTextView: TextView = view.nameTextView
}