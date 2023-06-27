package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.AlbumAdapter
import com.example.myapplication.adapters.AllArtistAdapter
import com.example.myapplication.adapters.SearchAdapter
import com.example.myapplication.adapters.StarAdapter
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import com.example.myapplication.model.albummodel.Album
import com.example.myapplication.model.albummodel.Result
import com.example.myapplication.model.allartistmodel.AllArtistModel
import com.example.myapplication.model.allartistmodel.AllArtistResult
import com.example.myapplication.model.starsmodel.StarResult
import com.example.myapplication.model.starsmodel.StarsModel
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.viewmodel.AlbumsViewModel
import com.example.myapplication.viewmodel.AllArtistsViewModel
import com.example.myapplication.viewmodel.StarsViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.action_bar_layout.*
import kotlinx.android.synthetic.main.fragment_search_track.*
import kotlinx.android.synthetic.main.fragment_search_track.feedRecyclerview
import kotlinx.android.synthetic.main.fragment_search_track.search_imageview
import kotlinx.android.synthetic.main.fragment_search_track.swipeToRefresh
import kotlinx.android.synthetic.main.fragment_search_track.title_textview
import org.json.JSONObject
import java.util.HashMap

class AllArtistsActivity : BaseActivity() {
    var isScrolling: Boolean? = false
    var currentItems: Int = 0
    var totalItems: Int = 0
    var scrollOutItems: Int = 0
    var manager: GridLayoutManager? = null
    var count: Int = 6
    var items: MutableList<AllArtistResult> = mutableListOf()
    var searchItem: MutableList<AllArtistResult> = mutableListOf()

    private lateinit var allArtistsViewModel: AllArtistsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        allArtistsViewModel = ViewModelProvider.NewInstanceFactory().create(AllArtistsViewModel::class.java)
        setContentView(R.layout.fragment_search_track)
        init()
    }

    private fun init() {
        getApiData()
        titleTextview?.text ="Search Track"
        if(intent.hasExtra("title")){
            title_textview.text=intent.getStringExtra("title")
        }
        setAdapter(items)
        search_data_edittext.addTextChangedListener(object : TextWatcher {
            override   fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(!s.toString().isNullOrEmpty()){
                    searchValue(s.toString().toLowerCase())
                }
                else{
                    setAdapter(items)
                }
            }

            override   fun afterTextChanged(editable: Editable?) {}
        })
        back_imageview.setOnClickListener {
            finish()        }
        swipeToRefresh?.setOnRefreshListener {
        swipeToRefresh.isRefreshing=false
        }
       search_imageview.setOnClickListener {
            startActivity(Intent(this@AllArtistsActivity,SearchActivity::class.java))
        }
        feedRecyclerview?.adapter?.notifyDataSetChanged()
        pagination()
    }
    private fun getApiData(){
        if (Utils.getInstance().isNetworkConnected(this@AllArtistsActivity)) {
            val hashMap = HashMap<String, String>()
            hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN,"")
            hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN,"")
            allArtistsViewModel.getAllArtists(hashMap)
            getResponse()
            progressBar.show(this)
        }
        else{
            showErrorDialog("No Internet Connection")
        }
    }
    private fun showErrorDialog(message:String){
        ViewUtils.showSnackBar(this@AllArtistsActivity,message,false,"Retry").subscribe{ res->
            if(res){
                getApiData()
            }
        }
    }
    private fun searchValue(value: String) {
        searchItem.clear()
        for (i in 0 until items.size) {
            if (items[i].name.toLowerCase().contains(value.toLowerCase())) {
                searchItem.add(items[i])
            }
        }
        setAdapter(searchItem)
    }

    private fun pagination() {
        feedRecyclerview?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentItems = manager!!.childCount
                totalItems = manager!!.itemCount
                scrollOutItems = manager!!.findFirstVisibleItemPosition()
                if (isScrolling == true && currentItems + scrollOutItems == totalItems && dy > 0) {
                    isScrolling = false
                    count += 1
                }
            }
        })
    }

    private fun setAdapter(itemsArray:MutableList<AllArtistResult>){
        manager = GridLayoutManager(this@AllArtistsActivity,3)
        feedRecyclerview?.adapter = AllArtistAdapter(itemsArray, (this@AllArtistsActivity), this)
        feedRecyclerview?.layoutManager = manager
    }
    private fun getResponse() {
        allArtistsViewModel.artistLiveData!!.observe(this, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), AllArtistModel::class.java)
                if (response.meta.code == 205) {
                    if(response.result!=null&&response.result.size>0) {
                        trackVisibility(true)
                        items.addAll(response.result)
                    }else{
                        trackVisibility(false)
                    }
                    feedRecyclerview.adapter!!.notifyDataSetChanged()
                } else {
                    trackVisibility(false)
                    showErrorDialog(response.meta.message)
                }
            }else{
                trackVisibility(false)
                showErrorDialog("Server is not responding")
            }
        })
    }
    private fun trackVisibility(flag:Boolean){
        if(flag){
            swipeToRefresh.visibility= View.VISIBLE
            no_result_textview.visibility= View.GONE
        }else{
            swipeToRefresh.visibility= View.GONE
            no_result_textview.visibility= View.VISIBLE
        }
    }
}