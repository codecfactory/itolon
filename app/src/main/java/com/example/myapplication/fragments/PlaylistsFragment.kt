package com.example.myapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.view.CustomProgressBar
import com.example.myapplication.R
import com.example.myapplication.activities.MainActivity
import com.example.myapplication.activities.PlaylistActivity
import com.example.myapplication.adapters.PlayListClickListener
import com.example.myapplication.adapters.PlaylistsAdapter
import com.example.myapplication.model.addtoplaylistmodel.AddToPlaylistModel
import com.example.myapplication.model.playlistmodel.PlaylistModel
import com.example.myapplication.model.playlistmodel.PlaylistResult
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.viewmodel.PlaylistViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_search_track.*
import kotlinx.android.synthetic.main.fragment_search_track.view.*
import org.json.JSONObject
import java.util.*

class PlaylistsFragment : Fragment(),
    PlayListClickListener {
    val progressBar = CustomProgressBar()
    var isScrolling: Boolean? = false
    var currentItems: Int = 0
    var totalItems: Int = 0
    var scrollOutItems: Int = 0
    var manager: GridLayoutManager? = null
    var count: Int = 6
    var items: MutableList<PlaylistResult> = mutableListOf()
    var searchItem: MutableList<PlaylistResult> = mutableListOf()
    var songId=-1
    var mView:View?=null
    private lateinit var playlistViewModel: PlaylistViewModel
    var isAddToPlaylist=false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_track, container, false)
        playlistViewModel = ViewModelProvider.NewInstanceFactory().create(PlaylistViewModel::class.java)
        mView=view
        mView!!.title_textview.visibility=View.GONE
        init(view)
    return view
    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        playlistViewModel = ViewModelProvider.NewInstanceFactory().create(PlaylistViewModel::class.java)
//        setContentView(R.layout.fragment_search_track)
//        init()
//    }

    private fun init(view:View) {
        getApiData()
//        if(intent.hasExtra("id")){
//            songId=intent.getIntExtra("id",-1)
//            titleTextview?.text = "Select Playlist"
//            isAddToPlaylist=true
//        }else {
//            titleTextview?.text = "Search Track"
//            isAddToPlaylist=false
//        }
//        if(intent.hasExtra("title")){
//            title_textview.text=intent.getStringExtra("title")
//        }
        setAdapter(items)
        mView!!.search_data_edittext.addTextChangedListener(object : TextWatcher {
            override  fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(!s.toString().toLowerCase().isNullOrEmpty()){
                    searchValue(s.toString().toLowerCase())
                }
                else{
                    setAdapter(items)
                }
            }
            override   fun afterTextChanged(editable: Editable?) {}
        })
        mView!!.swipeToRefresh?.setOnRefreshListener {
            mView!!.swipeToRefresh.isRefreshing=false
        }
        view.back_imageview.setOnClickListener {
//            finish()\
            (activity as MainActivity).onBackPressed()
        }
        mView!!.search_imageview.setOnClickListener {
//            startActivity(Intent(this@PlaylistsActivity,SearchActivity::class.java))
        }
        mView!!.feedRecyclerview?.adapter?.notifyDataSetChanged()
        pagination()
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

    private fun getApiData(){
        if (Utils.getInstance().isNetworkConnected(requireContext())) {
            val hashMap = HashMap<String, String>()
            hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN,"")
            hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN,"")
            playlistViewModel.getPlaylists(hashMap)
            getResponse()
            progressBar.show(requireContext())
        }
        else{
            showErrorDialog("No Internet Connection")
        }
    }
    private fun addApiData(playlistId:Int){
        if (Utils.getInstance().isNetworkConnected(requireContext())) {
            val hashMap = HashMap<String, String>()
            hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN,"")
            hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN,"")
            hashMap["playlist_id"] =playlistId.toString()
            hashMap["song_id"] =songId.toString()
            playlistViewModel.addToPlaylist(hashMap)
            getAddToPlaylistResponse()
            progressBar.show(requireContext())
        }
        else{
            showErrorDialog("No Internet Connection")
        }
    }
    private fun showErrorDialog(message:String){
        ViewUtils.showSnackBar(requireContext(),message,false,"Retry").subscribe{ res->
            if(res){
                getApiData()
            }
        }
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

    private fun setAdapter(itemsArray:MutableList<PlaylistResult>){
        manager = GridLayoutManager(requireContext(),3)
        mView!!.feedRecyclerview?.adapter = PlaylistsAdapter(itemsArray, (requireContext()), this)
        mView!!.feedRecyclerview?.layoutManager = manager
    }
    private fun getResponse() {
        playlistViewModel.playlistLiveData!!.observe(viewLifecycleOwner, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), PlaylistModel::class.java)
                if (response.meta.code == 205) {
                    items.addAll(response.result)
                    feedRecyclerview.adapter!!.notifyDataSetChanged()
                } else {
                    showErrorDialog(response.meta.message)
                }
            }else{
                showErrorDialog("Server is not responding")
            }
        })
    }
    private fun getAddToPlaylistResponse() {
        playlistViewModel.addToPlaylistLiveData!!.observe(this, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), AddToPlaylistModel::class.java)
                if (response.meta.code == 210) {
                    ViewUtils.showSnackBar(activity!!,response.meta.message,true,"")
                    Handler().postDelayed({
                        (activity as MainActivity).finish()
                    }, 2 * 1000)
                } else {
                    showErrorDialog(response.meta.message)
                }
            }else{
                showErrorDialog(getString(R.string.server_is_not_responding))
            }
        })
    }

    override fun onClick(position: Int, playListId: Int,imagePath:String) {
        if(isAddToPlaylist) {
            addApiData(playListId)
        }else{
            startActivity(Intent(activity!!, PlaylistActivity::class.java).putExtra("id",items[position].playlistId.toString()).putExtra("title",items[position].name).putExtra("imagePath",imagePath))
        }
    }
}