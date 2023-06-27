package com.example.myapplication.activities
import android.content.Intent
import android.os.Bundle
import android.widget.AbsListView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.MyPlaylistAdapter
import com.example.myapplication.adapters.MyPlaylistItemClickListener
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.model.playlistmodel.PlaylistModel
import com.example.myapplication.model.playlistmodel.PlaylistResult
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.viewmodel.PlaylistViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_artists_biographie.*
import kotlinx.android.synthetic.main.activity_my_playlist.*
import org.json.JSONObject
import java.util.*

class MyPlaylistActivity : BaseActivity(), MyPlaylistItemClickListener {
    var isScrolling: Boolean? = false
    var currentItems: Int = 0
    var totalItems: Int = 0
    var scrollOutItems: Int = 0
    var manager: LinearLayoutManager? = null
    var count: Int = 6
    var clickPosition=-1
    private lateinit var playlistViewModel: PlaylistViewModel

    var items: MutableList<PlaylistResult> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlistViewModel = ViewModelProvider.NewInstanceFactory().create(PlaylistViewModel::class.java)
        setContentView(R.layout.activity_my_playlist)
        init()
    }
    private fun init(){
        manager = LinearLayoutManager(this)
        my_playlist_recyclerview.adapter = MyPlaylistAdapter(items,this, this,this)
        my_playlist_recyclerview.layoutManager = manager
        my_playlist_recyclerview.adapter?.notifyDataSetChanged()
        pagination()
        back_imageview.setOnClickListener {
            finish()
        }
        create_playlist_layout.setOnClickListener {
            startActivity(Intent(this@MyPlaylistActivity, CreatePlaylistActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        getApiData()
    }
    private fun getApiData(){
        if (Utils.getInstance().isNetworkConnected(this@MyPlaylistActivity)) {
            val hashMap = HashMap<String, String>()
            hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN,"")
            hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN,"")
            playlistViewModel.getPlaylists(hashMap)
            getResponse()
            progressBar.show(this)
        }
        else{
            showErrorDialog("No Internet Connection")
        }
    }
    private fun showErrorDialog(message:String){
        ViewUtils.showSnackBar(this@MyPlaylistActivity,message,false,"Retry").subscribe{ res->
            if(res){
                getApiData()
            }
        }
    }
    private fun pagination() {
        my_playlist_recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
    private fun getResponse() {
        playlistViewModel.playlistLiveData!!.observe(this, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), PlaylistModel::class.java)
                items.clear()
                if (response.meta.code == 205) {
                    items.addAll(response.result)
                    total_playlist_textview.text=response.result.size.toString()+" Playlists"
                    my_playlist_recyclerview.adapter!!.notifyDataSetChanged()
                } else {
                    showErrorDialog(response.meta.message)
                }
            }else{
                showErrorDialog("Server is not responding")
            }
        })


    }
    override fun onClick(position: Int, songUrl: String) {
        clickPosition=position
    }
}