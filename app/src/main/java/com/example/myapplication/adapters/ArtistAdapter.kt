package com.example.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activities.PlaylistActivity
import com.example.myapplication.fragments.FeedFragment
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface

class ArtistAdapter(val items: List<String>, val context: Context, val fragment: PlaylistActivity) : RecyclerView.Adapter<ArtistsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistsViewHolder {
        return ArtistsViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_artists, parent, false))
    }
    override fun onBindViewHolder(holder: ArtistsViewHolder, position: Int) {
        try {
            try {
            } catch (e: ClassCastException) {
                throw ClassCastException("Fragment must implement AdapterCallback.")
            }
        } catch (ignored: Exception) {
        }
    }
    override fun getItemCount(): Int {
        return items.size
    }
}

class ArtistsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
}