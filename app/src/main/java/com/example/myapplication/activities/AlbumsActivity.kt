package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.CategoriesAdapter
import com.example.myapplication.adapters.CategoriesItemClickListener
import com.example.myapplication.adapters.ViewPagerAdapter
import com.example.myapplication.model.allartistmodel.AllArtistModel
import com.example.myapplication.model.categoriessongmodel.CategoriesResult
import com.example.myapplication.model.categoriessongmodel.CategoriesSongModel
import com.example.myapplication.model.categoriessongmodel.Song
import com.example.myapplication.prefrences.Constants
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.viewmodel.AllArtistsViewModel
import com.example.myapplication.viewmodel.CategorySongViewModel
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.action_bar_layout.*
import kotlinx.android.synthetic.main.action_bar_layout.view.*
import kotlinx.android.synthetic.main.action_bar_layout.view.titleTextview
import kotlinx.android.synthetic.main.fragment_movie.*
import kotlinx.android.synthetic.main.fragment_movie.view.*
import kotlinx.android.synthetic.main.fragment_movie.view.tabLayout
import kotlinx.android.synthetic.main.fragment_movie.view.viewPager
import kotlinx.android.synthetic.main.fragment_search_track.*
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class AlbumsActivity : BaseActivity(), CategoriesItemClickListener {
    private lateinit var categorySongViewModel: CategorySongViewModel
    var artistId=""
    var manager: GridLayoutManager? = null
    var items: MutableList<CategoriesResult> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categorySongViewModel = ViewModelProvider.NewInstanceFactory().create(CategorySongViewModel::class.java)
        setContentView(R.layout.fragment_movie)
        init()
    }

    private fun init() {
        titleTextview?.text = "Albums"
        if(intent.hasExtra("artistId")){
            artistId=intent.getStringExtra("artistId")
        }
        manager = GridLayoutManager(this@AlbumsActivity,3)
        categories_recyclerview.adapter = CategoriesAdapter(items,this, this)
        categories_recyclerview.layoutManager = manager
        getApiData()
        back_arrow_imageview.setOnClickListener {
            finish()
        }
    }
    private fun getApiData(){
        if (Utils.getInstance().isNetworkConnected(this@AlbumsActivity)) {
            val hashMap = HashMap<String, String>()
            hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN,"")
            hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN,"")
            categorySongViewModel.getCategoriesSongs(hashMap)
            getResponse()
            progressBar.show(this)
        }
        else{
            showErrorDialog("No Internet Connection")
        }
    }
    private fun getResponse() {
        categorySongViewModel.songsLiveData!!.observe(this, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), CategoriesSongModel::class.java)
                if (response.meta.code == 205) {
                    items.addAll(response.result)
                    categories_recyclerview.adapter!!.notifyDataSetChanged()
                    Constants.categorySongsArrayList.clear()
                    if(response.result!=null&&response.result.size>0) {
                        for(i in 0 until response.result.size) {
                            tabLayout!!.addTab(tabLayout!!.newTab().setText(response.result[i].name))
                            Constants.categorySongsArrayList.addAll(response.result[i].songs)
                        }
                        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL
                        val adapter = ViewPagerAdapter(supportFragmentManager, tabLayout!!.tabCount,response.result)
                        viewPager!!.adapter = adapter
                        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
                        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                            override fun onTabSelected(tab: TabLayout.Tab) {
                                viewPager!!.currentItem = tab.position
                            }
                            override fun onTabUnselected(tab: TabLayout.Tab) {
                            }
                            override fun onTabReselected(tab: TabLayout.Tab) {
                            }
                        })
                    }else{
//                        trackVisibility(false)
                    }
//                    feedRecyclerview.adapter!!.notifyDataSetChanged()
                } else {
//                    trackVisibility(false)
                    showErrorDialog(response.meta.message)
                }
            }else{
//                trackVisibility(false)
                showErrorDialog("Server is not responding")
            }
        })
    }
    private fun showErrorDialog(message:String){
        ViewUtils.showSnackBar(this@AlbumsActivity,message,false,"Retry").subscribe{ res->
            if(res){
                getApiData()
            }
        }
    }

    override fun onClick(position: Int,name:String) {
        startActivity(Intent(this@AlbumsActivity,CategoriesDetailActivity::class.java).putExtra("items",items[position].songs as ArrayList<Song>).putExtra("name",name).putExtra("artistId",artistId))
    }
}