package com.example.myapplication.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.AlbumAdapter
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import com.example.myapplication.model.albummodel.Album
import com.example.myapplication.model.albummodel.Result
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.viewmodel.AlbumsViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_album.*
import org.json.JSONObject
import java.util.*

class AlbumActivity : BaseActivity(), FeedLikeClickInterface, ClickInterface {
    var isScrolling: Boolean? = false
    var currentItems: Int = 0
    var totalItems: Int = 0
    var scrollOutItems: Int = 0
    var manager: GridLayoutManager? = null
    var count: Int = 6
    var items: MutableList<Result> = mutableListOf()
    var searchItem: MutableList<Result> = mutableListOf()
    private lateinit var albumsViewModel: AlbumsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        albumsViewModel = ViewModelProvider.NewInstanceFactory().create(AlbumsViewModel::class.java)
        setContentView(R.layout.activity_album)
        init()
    }

    private fun init() {
//        titleTextview?.text ="Search Track"
        if(intent.hasExtra("title")){
            title_textview.text=intent.getStringExtra("title")
        }
        back_arrow_imageview.setOnClickListener {
            finish()
        }
        setAdapter(items)
        swipeToRefresh?.setOnRefreshListener {
        swipeToRefresh.isRefreshing=false
        }
        album_search_edittext.addTextChangedListener(object : TextWatcher {
         override  fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
              if(!s.toString().isNullOrEmpty()){
                    searchValue(s.toString().toLowerCase())
              }
                else{
                  setAdapter(items)
              }
            }
         override fun afterTextChanged(editable: Editable?) {}
        })
        getApiData()
        feedRecyclerview?.adapter?.notifyDataSetChanged()
        pagination()

    }

    private fun getApiData(){
        if (Utils.getInstance().isNetworkConnected(this@AlbumActivity)) {
            val hashMap = HashMap<String, String>()
            hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN, "")
            hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN, "")
            albumsViewModel.getAlbums(hashMap)
            getResponse()
            progressBar.show(this)
        }
        else{
            showErrorDialog("No Internet Connection")
        }
    }
    private fun showErrorDialog(message:String){
        ViewUtils.showSnackBar(this@AlbumActivity,message,false,"Retry").subscribe{ res->
            if(res){
                getApiData()
            }
        }
    }
    private fun searchValue(value: String) {
        searchItem.clear()
        for (i in 0 until items.size) {
            if (items[i].title.toLowerCase().contains(value.toLowerCase())) {
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

    private fun setAdapter(itemsArray:MutableList<Result>){
        manager = GridLayoutManager(this@AlbumActivity,3)
        feedRecyclerview?.adapter = AlbumAdapter(itemsArray, (this@AlbumActivity), this)
        feedRecyclerview?.layoutManager = manager
    }
    private fun getResponse() {
        albumsViewModel.albumsLiveData!!.observe(this, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), Album::class.java)
                if (response.meta.code == 205) {
                    if (response.result != null && response.result.size > 0) {
                        trackVisibility(true)
                        items.addAll(response.result)
                    } else {
                        trackVisibility(false)
                    }
                    feedRecyclerview.adapter!!.notifyDataSetChanged()
                } else {
                    trackVisibility(false)
                    showErrorDialog(response.meta.message)
                }
            }
            else{
                showErrorDialog("Server is not responding")
                trackVisibility(false)
            }
        })
    }
    override fun showDialog(check: Boolean?, audioUrl: String?, trackId: String?, position: Int) {
    }

    override fun click(param: String?, imageView: ImageView?, textView: TextView?) {
    }

    override fun previousButtonClick(position: Int) {

    }

    override fun nextButtonClick(position: Int) {
    }

    override fun clickListener(id: String?, postId: String?, likeStatus: Int, shareUrl: String?, position: Int) {
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