package com.example.myapplication.activities

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.prefrences.Constants
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.adapters.*
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.model.artistdetailmodel.ArtistDetail
import com.example.myapplication.model.artistdetailmodel.Similar
import com.example.myapplication.model.artistdetailmodel.Song
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.viewmodel.ArtistDetailsViewModel
import com.google.gson.Gson
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import kotlinx.android.synthetic.main.activity_artists_biographie.*
import kotlinx.android.synthetic.main.activity_artists_biographie.artists_recyclerview
import kotlinx.android.synthetic.main.activity_artists_biographie.back_arrow_imageview
import kotlinx.android.synthetic.main.activity_artists_biographie.no_result_textview
import kotlinx.android.synthetic.main.activity_artists_biographie.playlist_recyclerview
import kotlinx.android.synthetic.main.activity_artists_biographie.profile_image
import kotlinx.android.synthetic.main.activity_artists_biographie.tracks_count
import org.json.JSONObject
import java.io.File
import java.util.HashMap

class ArtistsActivity : BaseActivity(), SongItemClickListener {
    var isScrolling: Boolean? = false
    var currentItems: Int = 0
    var totalItems: Int = 0
    var scrollOutItems: Int = 0
    var count: Int = 6
    var clickPosition=-1
    var playFilePath=""
    var playSongPosition=-1
    var manager: LinearLayoutManager? = null
    var items: MutableList<Song> = mutableListOf()
    private var defaultItems: MutableList<String> = mutableListOf()
    private var similiarArtistsList: MutableList<Similar> = mutableListOf()
    private lateinit var artistDetailsViewModel: ArtistDetailsViewModel
    private var mediaPlayer: MediaPlayer? = null
    private var duration=""
    val songsArrayList=ArrayList<String>()
    val videoArrayList=ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        artistDetailsViewModel = ViewModelProvider.NewInstanceFactory().create(ArtistDetailsViewModel::class.java)
        setContentView(R.layout.activity_artists_biographie)
        init()
    }

    private fun init() {
        manager = LinearLayoutManager(this@ArtistsActivity, LinearLayoutManager.HORIZONTAL, false)
        playlist_recyclerview.layoutManager = manager
        playlist_recyclerview.adapter = ArtistAlbumAdapter(items, this, this)
        getApiData()
        for (i in 0 until 6) {
            defaultItems.add("")
        }
        pagination()
        back_arrow_imageview.setOnClickListener {
            finish()
        }
        setArtistsAdapter()
        setAlbumTrackAdapter()
    }
    private fun setArtistsAdapter() {
        val layoutManager = LinearLayoutManager(this@ArtistsActivity, LinearLayoutManager.HORIZONTAL, false)
        artists_recyclerview.layoutManager = layoutManager
        artists_recyclerview.adapter = TrackAdapter(similiarArtistsList, this, this)
    }
    private fun getApiData(){
        if (Utils.getInstance().isNetworkConnected(this@ArtistsActivity)) {
            if(intent.hasExtra("artistId")){
                titleTextview.text=intent.getStringExtra("name")
                val hashMap = HashMap<String, String>()
                hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN,"")
                hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN,"")
                hashMap["artist_id"] = intent.getStringExtra("artistId")
                artistDetailsViewModel.getArtistDetails(hashMap)
                getResponse()
                progressBar.show(this)
            }
        }
        else{
            showErrorDialog("No Internet Connection")
        }
    }
    private fun showErrorDialog(message:String){
        ViewUtils.showSnackBar(this@ArtistsActivity,message,false,"Retry").subscribe{ res->
            if(res){
                getApiData()
            }
        }
    }
    private fun setAlbumTrackAdapter() {
        val layoutManager = LinearLayoutManager(this@ArtistsActivity, LinearLayoutManager.VERTICAL, false)
        popular_tracks_recyclerview.layoutManager = layoutManager
        popular_tracks_recyclerview.adapter = AlbumTracksAdapter(items, this, this,this)
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
        artistDetailsViewModel.artistDetailsLiveData!!.observe(this, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                try {
                    val body = jsonResponse.getJSONObject("body")
                    val response = gson.fromJson(body.toString(), ArtistDetail::class.java)
                    if (response.meta.code == 205) {
                        artist_name_textview.text=response.result.name+" "+response.result.secondName
                        about_description_textview.text=response.result.bio
                        more_textview.text="More by:"+response.result.name
                        similar_textview.text="Similar to "+response.result.name
                        tracks_count.text=response.result.totalTracks.toString()+" Tracks"
                        if(response.result.songs.size>0) {
                            trackVisibility(true)
                            items.addAll(response.result.songs)
                            for(i in 0 until response.result.songs.size){
                                downloadFile("http://44.231.47.188"+response.result.songs[i].content.filePath,i)
                            }
                        }else{
                            trackVisibility(false)
                        }
                        if(response.result.similar.size>0) {
                            similarTrackVisibility(true)
                            similiarArtistsList.addAll(response.result.similar)
                        }else{
                            similarTrackVisibility(false)
                        }
                        progressBar.dialog.dismiss()
                        playlist_recyclerview.adapter!!.notifyDataSetChanged()
                        popular_tracks_recyclerview.adapter!!.notifyDataSetChanged()
                        artists_recyclerview.adapter!!.notifyDataSetChanged()
                        Glide.with(this@ArtistsActivity).load(response.result.photo).error(R.drawable.maxresdefault).placeholder(R.drawable.maxresdefault).into(profile_image)
                    } else {
                        trackVisibility(false)
                        similarTrackVisibility(false)
                        showErrorDialog(response.meta.message)
                    }
                } catch (e: Exception) {
                    trackVisibility(false)
                    similarTrackVisibility(false)
                    e.printStackTrace()
                }
            }else{
                trackVisibility(false)
                similarTrackVisibility(false)
                showErrorDialog("Server is not responding")
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
    override fun onClick(position: Int, songUrl: String) {
        clickPosition=position
        if(songUrl.contains(".mp3"))
        {
            Constants.songsArrayList.addAll(songsArrayList)
            playSongPosition=computePosition(songUrl)
            if(playSongPosition!=-1) {
                startActivity(Intent(this@ArtistsActivity, TeaserActivity::class.java).putExtra("id",items[clickPosition].id).putExtra("position", playSongPosition).putExtra("currentPlaylistItemTrack", items[clickPosition]))
                playSongPosition = -1
            }else{
                showSnackBar("File is not exist",false)
            }
        }else{
            Constants.songsArrayList.addAll(videoArrayList)
            playSongPosition=computePosition(songUrl)
            if(playSongPosition!=-1) {
                startActivity(Intent(this@ArtistsActivity, VideoPlayActivity::class.java).putExtra("id",items[clickPosition].id).putExtra("position", playSongPosition).putExtra("currentPlaylistItemTrack", items[clickPosition]))
            }else{
                showSnackBar("File is not exist",false)
            }
        }
    }
    private fun trackVisibility(flag:Boolean){
        if(flag){
            nested_scrollview.visibility= View.VISIBLE
            popular_track_nested_scrollview.visibility= View.VISIBLE
            no_result_textview.visibility= View.GONE
            no_popular_track_textview.visibility= View.GONE
        }else{
            nested_scrollview.visibility= View.GONE
            popular_track_nested_scrollview.visibility= View.GONE
            no_result_textview.visibility= View.VISIBLE
            no_popular_track_textview.visibility= View.VISIBLE
        }
    }
    private fun similarTrackVisibility(flag:Boolean){
        if(flag){
            artists_recyclerview.visibility= View.VISIBLE
            no_similiar_result_textview.visibility= View.GONE
        }else{
            artists_recyclerview.visibility= View.GONE
            no_similiar_result_textview.visibility= View.VISIBLE
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