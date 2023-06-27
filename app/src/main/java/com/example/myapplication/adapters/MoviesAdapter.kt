package com.example.myapplication.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activities.PlaylistActivity
import com.example.myapplication.fragments.AlbumDetailFragment
import com.example.myapplication.fragments.MoviesFragment
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import kotlinx.android.synthetic.main.cell_album_detail.view.*

class MoviesAdapter(val items: List<String>, val context: Context, val fragment: MoviesFragment) :
    RecyclerView.Adapter<MoviesViewHolder>() {
    private var mClickInterface: FeedLikeClickInterface? = null
    private var mPostDetails: ClickInterface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        return MoviesViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_movies, parent, false))
    }
    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
            try {
//                this.mClickInterface = fragment
//                this.mPostDetails = fragment
            } catch (e: ClassCastException) {
                throw ClassCastException("Fragment must implement AdapterCallback.")
            }
            holder.mCellLinearLayout.setOnClickListener {
                context.startActivity(Intent(context,PlaylistActivity::class.java))
            }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class MoviesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mCellLinearLayout: LinearLayout = view.cell_layout
}