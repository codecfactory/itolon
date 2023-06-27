package com.example.myapplication.activities
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.ArtistAdapter
import com.example.myapplication.adapters.CartAdapter
import com.example.myapplication.adapters.PlaylistAdapter
import com.example.myapplication.adapters.SongClickListener
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.model.albumdetailmodel.AlbumDetailModel
import com.example.myapplication.model.albumdetailmodel.AlbumDetailResult
import com.example.myapplication.model.albumdetailmodel.Song
import com.example.myapplication.model.albummodel.Album
import com.example.myapplication.prefrences.Constants
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.viewmodel.AlbumDetailViewModel
import com.google.gson.Gson
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import kotlinx.android.synthetic.main.activity_album.*
import kotlinx.android.synthetic.main.activity_artists_biographie.*
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_cart.artists_recyclerview
import kotlinx.android.synthetic.main.activity_cart.back_arrow_imageview
import kotlinx.android.synthetic.main.activity_cart.no_result_textview
import kotlinx.android.synthetic.main.activity_cart.profile_image
import kotlinx.android.synthetic.main.activity_cart.titleTextview
import kotlinx.android.synthetic.main.activity_cart.track_scrollview
import kotlinx.android.synthetic.main.activity_playlists.*
import org.json.JSONObject
import java.io.File
import java.util.HashMap

class CartActivity : BaseActivity(), SongClickListener {
    var isScrolling: Boolean? = false
    var currentItems: Int = 0
    var totalItems: Int = 0
    var scrollOutItems: Int = 0
    var manager: LinearLayoutManager? = null
    var count: Int = 6
    var clickPosition=-1
    var playFilePath=""
    var playSongPosition=-1
    var items: MutableList<Song> = mutableListOf()
    private var filePath = ""
    private var mediaPlayer: MediaPlayer? = null
    private var duration=""
    val songsArrayList=ArrayList<String>()
    val videoArrayList=ArrayList<String>()
    private lateinit var albumDetailViewModel: AlbumDetailViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        albumDetailViewModel = ViewModelProvider.NewInstanceFactory().create(AlbumDetailViewModel::class.java)
        setContentView(R.layout.activity_cart)
        init()
    }
    private fun init(){
        manager = LinearLayoutManager(this)
        cart_recyclerview.adapter = CartAdapter(items,this, this)
        cart_recyclerview.layoutManager = manager
        pagination()
        back_arrow_imageview.setOnClickListener {
            finish()
        }
        profile_image.setOnClickListener {
            startActivity(Intent(this@CartActivity, ArtistsActivity::class.java))
        }
//        playlist_textview.setOnClickListener {
//        }
        setArtistsAdapter()
        getApiData()
    }

    private fun setArtistsAdapter(){
        val layoutManager = LinearLayoutManager(this@CartActivity,LinearLayoutManager.HORIZONTAL,false)
        artists_recyclerview.layoutManager = layoutManager
        artists_recyclerview.adapter = CartAdapter(items, this, this)
    }

    private fun getApiData(){
        if (Utils.getInstance().isNetworkConnected(this@CartActivity)) {
            val hashMap = HashMap<String, String>()
            hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN,"")
            hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN,"")
            hashMap["album_id"] = intent.getStringExtra("albumId")
            albumDetailViewModel.getAlbumDetails(hashMap)
            getResponse()
            progressBar.show(this)
        }
        else{
            showErrorDialog("No Internet Connection")
        }
    }
    private fun showErrorDialog(message:String){
        ViewUtils.showSnackBar(this@CartActivity,message,false,"Retry").subscribe{ res->
            if(res){
                getApiData()
            }
        }
    }

    private fun pagination() {
        cart_recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
        albumDetailViewModel.albumDetailLiveData!!.observe(this, Observer { tokenResponse ->
            try {
                val gson = Gson()
                val json = gson.toJson(tokenResponse)
                val jsonResponse = JSONObject(json)
                progressBar.dialog.dismiss()
                if (jsonResponse.has("body")) {
                    val body = jsonResponse.getJSONObject("body")
                    val response = gson.fromJson(body.toString(), AlbumDetailModel::class.java)
                    if (response.meta.code == 205) {
                        titleTextview.text=response.result.title
                        name_textview.text=response.result.title
                        if(response.result.songs!=null&&response.result.songs.size>0) {
                            trackVisibility(true)
                            track_counter_textview.text = response.result.songs.size.toString() + " Tracks"
                            items.addAll(response.result.songs)
                            for(i in 0 until response.result.songs.size){
                                downloadFile("http://44.231.47.188"+response.result.songs[i].content.filePath,i)
                            }
                        }else{
                            trackVisibility(false)
                        }
                        cart_recyclerview.adapter!!.notifyDataSetChanged()
                    } else {
                        trackVisibility(false)
                        showErrorDialog(response.meta.message)
                    }
                }else{
                    showErrorDialog("Server is not responding")
                }
            } catch (e: Exception) {
                trackVisibility(false)
                e.printStackTrace()
            }
        })
    }

    private fun downloadFile(url: String,position:Int) {
        if(!progressBar.dialog.isShowing) {
            progressBar.show(this)
        }
        FileLoader.with(this).load(url, false) //2nd parameter is optioal, pass true to force load from network
            .fromDirectory("test4", FileLoader.DIR_INTERNAL).asFile(object : FileRequestListener<File> {
                override fun onLoad(request: FileLoadRequest, response: FileResponse<File>) {
                    val loadedFile = response.body
                    if(loadedFile.path.contains(".mp3")||loadedFile.path.contains(".m4a")) {
                        items[position].duration=getDuration( loadedFile.path)
                        songsArrayList.add(loadedFile.path)
                    }else{
                        items[position].duration=getDuration( loadedFile.path)
                        videoArrayList.add(loadedFile.path)
                    }
                    clickPosition=-1
                    popular_tracks_recyclerview.adapter!!.notifyDataSetChanged()
                    progressBar.dialog.dismiss()
                }
                override fun onError(request: FileLoadRequest, t: Throwable) {
                    progressBar.dialog.dismiss()
                }
            })
    }
//    private fun downloadFile(url: String) {
//        progressBar.show(this)
//        FileLoader.with(this).load(url, false)
//            .fromDirectory("test4", FileLoader.DIR_INTERNAL).asFile(object :
//                FileRequestListener<File> {
//                override fun onLoad(request: FileLoadRequest, response: FileResponse<File>) {
//                    val loadedFile = response.body
//                    filePath = loadedFile.path
//                    progressBar.dialog.dismiss()
//                    if(loadedFile.path. contains(".mp3")) {
//                        startActivity(Intent(this@CartActivity, TeaserActivity::class.java).putExtra("filePath", loadedFile.path).putExtra("currentItem", items.get(clickPosition)))
//                    }else{
//                        startActivity(Intent(this@CartActivity, VideoPlayActivity::class.java).putExtra("filePath",loadedFile.path))
//                    }
//                    clickPosition=-1
//                }
//                override fun onError(request: FileLoadRequest, t: Throwable) {
//                    progressBar.dialog.dismiss()
//                    showErrorDialog("File format not supported")
//                }
//            })
//    }

    override fun onClick(position: Int, songUrl: String) {
        clickPosition=position
        if(songUrl.contains(".mp3"))
        {
            Constants.songsArrayList.addAll(songsArrayList)
            playSongPosition=computePosition(songUrl)
            if(playSongPosition!=-1) {
                startActivity(Intent(this@CartActivity, TeaserActivity::class.java).putExtra("position", playSongPosition).putExtra("id",items[clickPosition].id).putExtra("currentPlaylistItemTrack", items[clickPosition]))
                playSongPosition = -1
            }else{
                showSnackBar("File is not exist",false)
            }
        }else{
            Constants.songsArrayList.addAll(videoArrayList)
            playSongPosition=computePosition(songUrl)
            if(playSongPosition!=-1) {
                startActivity(Intent(this@CartActivity, VideoPlayActivity::class.java).putExtra("position", playSongPosition).putExtra("id",items[clickPosition].id).putExtra("currentPlaylistItemTrack", items[clickPosition]))
            }else{
                showSnackBar("File is not exist",false)
            }
        }
//        downloadFile(songUrl)
    }

    private fun trackVisibility(flag:Boolean){
        if(flag){
            track_scrollview.visibility= View.VISIBLE
            no_result_textview.visibility= View.GONE
        }else{
            track_scrollview.visibility= View.GONE
            no_result_textview.visibility= View.VISIBLE
        }
    }
    private fun getDuration(filePath: String):String {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer!!.setDataSource(filePath)
        mediaPlayer!!.prepare()
        duration = Utils.getInstance().getDurationInMinutes(mediaPlayer!!.duration.toLong())
        return duration
    }
    private fun computePosition(songUrl: String):Int{
        val fileName=Utils.getInstance().getLastString(songUrl)
        for(i in 0 until Constants.songsArrayList.size){
            if(Constants.songsArrayList.get(i).contains(fileName)){
                playSongPosition=i
                playFilePath= Constants.songsArrayList.get(i)
            }
        }
        return playSongPosition
    }
}