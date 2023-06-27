package com.example.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.action_bar_layout.view.*
import kotlinx.android.synthetic.main.fragment_movie.view.*

class AlbumsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_movie, container, false)
        init(view)
        return view
    }

    private fun init(view: View) {
        view.titleTextview?.text = "Albums"
        view.tabLayout!!.addTab(view.tabLayout!!.newTab().setText("Tracks"))
        view.tabLayout!!.addTab(view.tabLayout!!.newTab().setText("Albums"))
        view.tabLayout!!.addTab(view.tabLayout!!.newTab().setText("Playlists"))
        view.tabLayout!!.addTab(view.tabLayout!!.newTab().setText("Artists"))
        view.tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL
        view.viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(view.tabLayout))
        view.tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                view.viewPager!!.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {
            }
            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }

}