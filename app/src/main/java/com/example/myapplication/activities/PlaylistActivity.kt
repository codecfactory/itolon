package com.example.myapplication.activities
import android.content.Intent
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_DURATION
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.adapters.PlaylistAdapter
import com.example.myapplication.adapters.PlaylistItemClickListener
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.model.playlistdetailmodel.PlaylistDetail
import com.example.myapplication.model.playlistdetailmodel.Song
import com.example.myapplication.prefrences.Constants
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.example.myapplication.viewmodel.PlaylistDetailViewModel
import com.google.gson.Gson
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import kotlinx.android.synthetic.main.activity_playlists.*
import kotlinx.android.synthetic.main.activity_playlists.back_arrow_imageview
import kotlinx.android.synthetic.main.activity_playlists.profile_image
import kotlinx.android.synthetic.main.activity_teaser.*
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class PlaylistActivity : BaseActivity(), PlaylistItemClickListener {
    var isScrolling: Boolean? = false
    var currentItems: Int = 0
    var totalItems: Int = 0
    var scrollOutItems: Int = 0
    var manager: LinearLayoutManager? = null
    var count: Int = 6
    var clickPosition=-1
    private var duration=""
    var playFilePath=""
    var playSongPosition=-1
    private lateinit var playlistDetailViewModel: PlaylistDetailViewModel
    private var mediaPlayer: MediaPlayer? = null
    val songsArrayList=ArrayList<String>()
    val videoArrayList=ArrayList<String>()

    var items: MutableList<Song> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlistDetailViewModel = ViewModelProvider.NewInstanceFactory().create(PlaylistDetailViewModel::class.java)
        setContentView(R.layout.activity_playlists)
        init()
    }
    private fun init(){
        if(intent.hasExtra("title")) {
            playlist_titleTextview.text = intent.getStringExtra("title")
        }
        val hashMap = HashMap<String, String>()
        hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN,"")
        hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN,"")
        hashMap["playlist_id"] = intent.getStringExtra("id")
        playlistDetailViewModel.getPlaylistDetail(hashMap)
        getResponse()
        progressBar.show(this)
        manager = LinearLayoutManager(this)
        playlist_recyclerview.adapter = PlaylistAdapter(items,this, this,this)
        playlist_recyclerview.layoutManager = manager
        pagination()
        back_arrow_imageview.setOnClickListener {
            finish()
        }
        Glide.with(this).load(intent.getStringExtra("imagePath")).placeholder(R.drawable.banner).error(R.drawable.banner).into(profile_image)
        profile_image.setOnClickListener {
//            startActivity(Intent(this@PlaylistActivity, ArtistsActivity::class.java))
        }
        setArtistsAdapter()
    }

    private fun setArtistsAdapter(){
        val layoutManager = LinearLayoutManager(this@PlaylistActivity,LinearLayoutManager.HORIZONTAL,false)
        artists_recyclerview.layoutManager = layoutManager
    }

    private fun pagination() {
        playlist_recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
        playlistDetailViewModel.playlistDetailLiveData!!.observe(this, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), PlaylistDetail::class.java)
                if (response.meta.code == 205) {
//                    Glide.with(this@PlaylistActivity).load("http://44.231.47.188" + response.result.artists!!.imagePath)
//                        .placeholder(R.drawable.banner).error(R.drawable.banner).into(profile_image)
                    tracks_count.text=response.result.songs.size.toString()+" Tracks"
                    if(response.result.songs.size>0) {
                        trackVisibility(true)
                        songsArrayList.clear()
                        videoArrayList.clear()
                        items.addAll(response.result.songs)
                        for(i in 0 until response.result.songs.size){
                            downloadFile("http://44.231.47.188"+response.result.songs[i].content.filePath)
                            if(response.result.songs[i].content.filePath.toString().contains(".mp3")){
                                items[i].duration=getDuration("http://44.231.47.188"+response.result.songs[i].content.filePath)
                            }else{
                                val retriever = MediaMetadataRetriever()
                                retriever.setDataSource("http://44.231.47.188"+response.result.songs[i].content.filePath, HashMap())
                                val time = retriever.extractMetadata(METADATA_KEY_DURATION)
                                val timeInMillisec = time.toLong()
                                retriever.release()
                                items[i].duration = Utils.getInstance().getDurationInMinutes(timeInMillisec)
                            }
                        }
                        playlist_recyclerview.adapter!!.notifyDataSetChanged()
                        progressBar.dialog.dismiss()
                    }else{
                        trackVisibility(false)
                        progressBar.dialog.dismiss()
                    }
                } else if(response.meta.code==404) {
                    trackVisibility(false)
                    progressBar.dialog.dismiss()
                }else {
                    trackVisibility(false)
                    progressBar.dialog.dismiss()
                }
            }else{
                progressBar.dialog.dismiss()
                trackVisibility(false)
            }
        })
    }

    override fun onClick(position: Int, songUrl: String) {
        playSongPosition=-1
        Constants.songsArrayList.clear()
        clickPosition = position
        if (songUrl.contains(".mp3")) {
            Constants.songsArrayList.addAll(songsArrayList)
            playSongPosition = computePosition(songUrl)
            if (playSongPosition != -1) {
                Constants.playlistItems.clear()
                Constants.playlistItems.addAll(items)
                try {
                    for (i in 0 until items.size) {
                        if (items[i].content != null) {
                            if (items[i].content.filePath != null && items[i].content.filePath.contains(".mp4")) {
                                Constants.playlistItems.removeAt(i)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                startActivity(Intent(this@PlaylistActivity, TeaserActivity::class.java).putExtra("position", playSongPosition).putExtra("id",items[clickPosition].id).putExtra("playlistItemData", items[clickPosition]).putExtra("from","fromPlaylist"))
                playSongPosition = -1
            } else {
                showSnackBar("File is not exist", false)
            }
        } else {
            Constants.songsArrayList.addAll(videoArrayList)
            playSongPosition = computePosition(songUrl)
            if (playSongPosition != -1&&playSongPosition<items.size) {
                startActivity(Intent(this@PlaylistActivity, VideoPlayActivity::class.java).putExtra("position", playSongPosition).putExtra("id",items[clickPosition].id).putExtra("is_purchased",items[playSongPosition].isPurchased)
                    .putExtra("playlistItemTrack", items[clickPosition]))
            } else {
                showSnackBar("File is not exist", false)
            }
        }
    }
    private fun downloadFile(url: String) {
        if(!progressBar.dialog.isShowing) {
            progressBar.show(this)
        }
        FileLoader.with(this).load(url, false) //2nd parameter is optioal, pass true to force load from network
            .fromDirectory("test4", FileLoader.DIR_INTERNAL).asFile(object : FileRequestListener<File> {
                override fun onLoad(request: FileLoadRequest, response: FileResponse<File>) {
                    val loadedFile = response.body
                    if(loadedFile.path.contains(".mp3")||loadedFile.path.contains(".m4a")) {
                        songsArrayList.add(loadedFile.path)
                    }else{
                        videoArrayList.add(loadedFile.path)
                    }
                    clickPosition=-1
                    progressBar.dialog.dismiss()
                }
                override fun onError(request: FileLoadRequest, t: Throwable) {
                    progressBar.dialog.dismiss()
                }
            })
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
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
            }
            mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer!!.setDataSource(filePath)
            mediaPlayer!!.prepare()
            duration = Utils.getInstance().getDurationInMinutes(mediaPlayer!!.duration.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
        }
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