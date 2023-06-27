package com.example.myapplication.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.fragments.AlbumDetailFragment
import com.example.myapplication.model.categoriessongmodel.Song
import kotlinx.android.synthetic.main.action_bar_layout.*
import java.util.ArrayList

class CategoriesDetailActivity :BaseActivity(){
    private var items: ArrayList<Song>? = null
    private var existItems: ArrayList<Song>? = ArrayList<Song>()
    var artistId=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_container)
        back_arrow_imageview.setOnClickListener { finish() }
        if(intent.hasExtra("artistId")){
            artistId=intent.getStringExtra("artistId")
        }
        if (intent.hasExtra("items")) {
            items = intent.getSerializableExtra("items") as ArrayList<Song>
            for(i in 0 until items!!.size){
                if(items!![i].content!=null){
                    if(items!![i].content.filePath!=null){
                        existItems!!.add(items!![i])
                    }
                }
            }
            addFragment(AlbumDetailFragment(existItems!!,artistId), false, "one")
        }
        if (intent.hasExtra("name")) {
            titleTextview.text= intent.getStringExtra("name")
        }
    }
    private fun addFragment(fragment: Fragment, addToBackStack: Boolean, tag: String) {
        val manager = supportFragmentManager
        val ft = manager.beginTransaction()
        if (addToBackStack) {
            ft.addToBackStack(tag)
        }
        ft.replace(R.id.container_frame, fragment, tag)
        ft.commitAllowingStateLoss()
    }
}