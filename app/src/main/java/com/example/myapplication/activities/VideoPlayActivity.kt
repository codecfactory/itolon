package com.example.myapplication.activities
import android.content.pm.ActivityInfo
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.MediaController
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.model.CartModel
import com.example.myapplication.model.albummodel.Album
import com.example.myapplication.model.playlistdetailmodel.Song
import com.example.myapplication.model.userdownloadsmodel.UserDownloadResult
import com.example.myapplication.prefrences.Constants
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.RxBus
import com.example.myapplication.utils.VideoPlayerConfig
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.viewmodel.SongDownloadViewModel
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_video_play.*
import org.json.JSONObject
import java.util.*

class VideoPlayActivity : BaseActivity() ,Player.EventListener{
    var isPurchased=false
    val mp = MediaPlayer()
    var player: SimpleExoPlayer? = null
    private var playlistResult: UserDownloadResult? = null
    private var playlistTrackResult: Song? = null
    private var categoryTrackResult: com.example.myapplication.model.categoriessongmodel.Song? = null
    private var songsList: com.example.myapplication.model.categoriessongmodel.Song? = null
    private lateinit var userDownloadsViewModel: SongDownloadViewModel
    var songId=-1
    var songName=""
    var songImagePath=""
    var songPrice=0
    var videoUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)
        userDownloadsViewModel = ViewModelProvider.NewInstanceFactory().create(SongDownloadViewModel::class.java)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        setUp()
        progressBar.show(this)
        if(intent.hasExtra("is_purchased")) {
            isPurchased=  intent.getBooleanExtra("is_purchased",false)
        }
        if (intent.hasExtra("id")) {
            songId = intent.getIntExtra("id", -1)
        }
        if (intent.hasExtra("currentPlaylistItemTrack")) {
            playlistResult = intent.getSerializableExtra("currentPlaylistItemTrack") as UserDownloadResult
//            track_name_textview.text = playlistResult!!.name
            songImagePath = "http://44.231.47.188" + playlistResult!!.imagePath
            titleTextview.text = playlistResult!!.name
            if (songsList!!.price!=null&&!TextUtils.isEmpty(songsList!!.price.toString())) {
                price_textview.text = playlistResult!!.price.toString()
            } else {
                price_textview.text = "Free"
            }
            donwload_imageview.visibility=View.GONE
        }
        if (intent.hasExtra("playlistItemTrack")) {
            playlistTrackResult = intent.getSerializableExtra("playlistItemTrack") as Song
            songImagePath="http://44.231.47.188" + playlistTrackResult!!.imagePath
            titleTextview.text=playlistTrackResult!!.name
            if (playlistTrackResult!!.price!=null&&!TextUtils.isEmpty(playlistTrackResult!!.price.toString())) {
                price_textview.text = playlistTrackResult!!.price.toString()
            } else {
                price_textview.text = "Free"
            }
                if(playlistTrackResult!!.isDownloaded){
                    donwload_imageview.visibility=View.GONE
                }else{
                    donwload_imageview.visibility=View.VISIBLE
                }
            isPurchased=playlistTrackResult!!.isPurchased
//                artist_name_textview.text = playlistResult!!
        }
        if (intent.hasExtra("songListCategoryItemDownloaded")) {
            categoryTrackResult = intent.getSerializableExtra("songListCategoryItemDownloaded") as com.example.myapplication.model.categoriessongmodel.Song
            songImagePath="http://44.231.47.188" + categoryTrackResult!!.imagePath
            titleTextview.text=categoryTrackResult!!.name
            if (categoryTrackResult!!.price!=null&&!TextUtils.isEmpty(categoryTrackResult!!.price.toString())) {
                price_textview.text = categoryTrackResult!!.price.toString()
            } else {
                price_textview.text = "Free"
            }
                if(categoryTrackResult!!.isDownloaded){
                    donwload_imageview.visibility=View.GONE
                }else{
                    donwload_imageview.visibility=View.VISIBLE
                }
            isPurchased=categoryTrackResult!!.isPurchased
        }
        if (intent.hasExtra("currentPlaylistItemTrack")) {
            playlistResult = intent.getSerializableExtra("currentPlaylistItemTrack") as UserDownloadResult
            songImagePath="http://44.231.47.188" + playlistResult!!.imagePath
            titleTextview.text=playlistResult!!.name
            if (playlistResult!!.price!=null&&!TextUtils.isEmpty(playlistResult!!.price.toString())) {
                price_textview.text = playlistResult!!.price.toString()
            } else {
                price_textview.text = "Free"
            }
//
//                if(playlistResult!!.isDownloaded){
//                }
//                artist_name_textview.text = playlistResult!!
        }
        if (intent.hasExtra("currentCategoryItemDownloaded")) {
            songsList = intent.getSerializableExtra("currentCategoryItemDownloaded") as com.example.myapplication.model.categoriessongmodel.Song
            if(!songsList!!.name.isNullOrEmpty()){
                songName=songsList!!.name
            }
            if(songsList!!.price!=null){
                songPrice=songsList!!.price.toInt()
            }
            titleTextview.text=songsList!!.name
            songImagePath="http://44.231.47.188" + songsList!!.imagePath
            if (songsList!!.price!=null&&!TextUtils.isEmpty(songsList!!.price.toString())) {
                price_textview.text = songsList!!.price.toString()
            } else {
                price_textview.text = "Free"
            }
        }
//        if(intent.hasExtra("position")) {
//            if (isPurchased) {
//                playMusic(Constants.songsArrayList.get(intent.getIntExtra("position", 0)))
//            }else {
////                playMusic(intent.getStringExtra("filePath"), 30)
//                playSoundForXSeconds(Constants.songsArrayList.get(intent.getIntExtra("position", 0)),30)
////                playMusic("http://44.231.47.188"+intent.getStringExtra("filePath"))
//            }
//        }
        if (intent.hasExtra("filePath")) {
            if (isPurchased) {
                initializePlayer(videoFullScreenPlayer!!)
                buildMediaSource(Uri.parse("http://44.231.47.188"+intent.getStringExtra("filePath")))
            } else {
//                playMusic(intent.getStringExtra("filePath"), 30)
//                calculateDuration("http://44.231.47.188"+intent.getStringExtra("filePath"))
                initializePlayer(videoFullScreenPlayer!!)
                buildMediaSource(Uri.parse("http://44.231.47.188"+intent.getStringExtra("filePath")))
//                player!!.seekTo(calculateDuration("http://44.231.47.188"+intent.getStringExtra("filePath")))

//              var duration=  Utils.getInstance().getDurationInSeconds(getMediaDuration(this,"http://44.231.47.188"+intent.getStringExtra("filePath")))
//Log.e("duration",duration.toString())
//              var duration=  Utils.getInstance().getDurationInSeconds(calculateDuration("http://44.231.47.188"+intent.getStringExtra("filePath")))
//                if(player!!.duration.toInt()<30){
//                    val timer = Timer()
//                    timer.schedule(object : TimerTask() {
//                        override fun run() {
//                            runOnUiThread {
//                                val handler = Handler()
//                                handler.postDelayed({ }, 1000)
//                                if(player!!.currentPosition.toInt()==player!!.duration.toInt()/2){
//                                    player!!.stop()
//                                }
//                            }
//                        }
//                    }, 0, 1000)
//                }else{
//                    val timer = Timer()
//                    timer.schedule(object : TimerTask() {
//                        override fun run() {
//                            runOnUiThread {
//                                val handler = Handler()
//                                handler.postDelayed({ }, 1000)
//                                if(Utils.getInstance().getDurationInSeconds(player!!.currentPosition)==30){
//                                    player!!.stop()
//                                }
//                            }
//                        }
//                    }, 0, 1000)
//                }

//                val cntr_aCounter: CountDownTimer = object : CountDownTimer(calculateDuration("http://44.231.47.188"+intent.getStringExtra("filePath")), 1000) {
//            override fun onTick(millisUntilFinished: Long) {
////                mediaPlayer!!.start()
//            }
//            override fun onFinish() {
//                if(player!!.isPlayingAd){
//                    player!!.stop()
//                }
//                player!!.stop()
//            }
//        }; cntr_aCounter.start()
//                        playSoundForXSeconds("http://44.231.47.188"+intent.getStringExtra("filePath"),30)
//                playMusic("http://44.231.47.188"+intent.getStringExtra("filePath"))
            }
        }

        if(intent.hasExtra("id")){
            songId=intent.getIntExtra("id",-1)

        }
        back_arrow_imageview.setOnClickListener{
            finish()
        }
        cart_imageview.setOnClickListener{
                if(isItemExist()){
                    ViewUtils.showSnackBar(this,"Song is already added to cart successfully",true,"")
                }else{
                    ViewUtils.show(this,"Are you sure you want to add product to cart").subscribe { res->
                        if(res){
                            ViewUtils.showSnackBar(this,"Song added to cart successfully",true,"")
                            Constants.cartArrayList.add(CartModel(songId,songName,songImagePath,songPrice))
                            RxBus.publish( CartModel(songId,songName,songImagePath,songPrice));
                        }
                    }
                } }
        donwload_imageview.setOnClickListener {
            val hashMap = HashMap<String, String>()
            hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN, "")
            hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN, "")
            hashMap["song_id"] = songId.toString()
            userDownloadsViewModel.downloadSong(hashMap)
            getResponse()
            progressBar.show(this)
        }
    }

//   private fun getMediaDuration(context: Context,uri: String): Long {
//       val retriever = MediaMetadataRetriever()
//       retriever.setDataSource(uri, HashMap())
//       val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
//       val timeInMillisec = time.toLong()
//       retriever.release()
//       return timeInMillisec
//
//    }
    private fun setUp() {
        if (videoUri == null) {
            return
        }
    }

    private fun calculateDuration(url:String):Long{
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(url, HashMap())
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val timeInMillisec = time.toLong()
        retriever.release()
//        val duration = timeInmillisec / 1000
//        val hours = duration / 3600
//        val minutes = (duration - hours * 3600) / 60
//        val seconds = duration - (hours * 3600 + minutes * 60)
        return timeInMillisec
    }


    private fun initializePlayer(videoFullScreenPlayer: PlayerView) {
        if (player == null) {
            // 1. Create a default TrackSelector
            val loadControl: LoadControl = DefaultLoadControl(
                DefaultAllocator(true, 16),
                VideoPlayerConfig.MIN_BUFFER_DURATION,
                VideoPlayerConfig.MAX_BUFFER_DURATION,
                VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
                VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER, -1, true
            )
            val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
            val videoTrackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory(bandwidthMeter)
            val trackSelector: TrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
            player = ExoPlayerFactory.newSimpleInstance(DefaultRenderersFactory(this@VideoPlayActivity), trackSelector, loadControl)
            videoFullScreenPlayer.setPlayer(player)
        }
    }

    private fun buildMediaSource(mUri: Uri) {
        // Measures bandwidth during playback. Can be null if not required.
        val bandwidthMeter = DefaultBandwidthMeter()
        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(this@VideoPlayActivity, Util.getUserAgent(this@VideoPlayActivity, getString(R.string.app_name)), bandwidthMeter)
        // This is the MediaSource representing the media to be played.
        val videoSource: MediaSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(mUri)
        // Prepare the player with the source.
        player!!.prepare(videoSource)
        player!!.playWhenReady = true
        player!!.addListener(this)
    }
    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
    }

    override  fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
    }


    override fun onLoadingChanged(isLoading: Boolean) {}

    override  fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            Player.STATE_BUFFERING -> {
            }
            Player.STATE_ENDED -> {
                progressBar.dialog.dismiss()
//                progressBar.show(this)
            }
            Player.STATE_IDLE -> {
                progressBar.dialog.dismiss()
            }
            Player.STATE_READY ->{
                progressBar.dialog.dismiss()
            }
            Player.TIMELINE_CHANGE_REASON_DYNAMIC->{
                Log.e("play","play")
            }
            else -> {
            }
        }
    }

    override fun onRepeatModeChanged(repeatMode: Int) {}

    override  fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}

    override fun onPlayerError(error: ExoPlaybackException?) {}

    override  fun onPositionDiscontinuity(reason: Int) {}

    override   fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}

    override fun onSeekProcessed() {}
    private fun getResponse() {
        userDownloadsViewModel.songDownloadLiveData!!.observe(this, androidx.lifecycle.Observer { response ->
            val gson = Gson()
            val json = gson.toJson(response)
            val jsonResponse = JSONObject(json)
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), Album::class.java)
                if (response.meta.code == 210) {
                    progressBar.dialog.dismiss()
                    donwload_imageview.visibility = View.GONE
                    ViewUtils.showSnackBar(this, "Item added to downloads successfully", true, "")
                }
            }
        })
    }
    private fun playMusic(path:String) {
        progressBar.show(this@VideoPlayActivity)
        val uri: Uri = Uri.parse(path)
        video_view.setVideoURI(uri)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(video_view)
        video_view.setMediaController(mediaController)
        video_view.start()
        video_view.setOnPreparedListener { mp ->
            progressBar.dialog.dismiss()
        }
        video_view.setOnErrorListener(object : MediaPlayer.OnErrorListener {
           override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
               progressBar.dialog.dismiss()
                return true
            }
        })
//        progressBar.dialog.dismiss()
    }
    private fun playSoundForXSeconds(path:String, seconds: Int) {
        val soundUri: Uri = Uri.parse(path)
        video_view.setVideoURI(soundUri)
        if (soundUri != null) {
            try {
                mp.setDataSource(this@VideoPlayActivity, soundUri)
                mp.prepare()
                mp.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val mHandler = Handler()
            mHandler.postDelayed(Runnable {
                try {
//                    mp.stop()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, (seconds * 1000).toLong())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(null!=video_view){
            video_view.stopPlayback();
            video_view.resume()
        }
        if (mp != null && mp.isPlaying) {
            mp.stop()
            mp.release()
            mp == null
        }
    }
    private fun isItemExist():Boolean{
        var flag=false
        for(i in 0 until Constants.cartArrayList.size){
            if(Constants.cartArrayList[i].id==songId){
                flag=true
            }
        }
        return flag
    }

    override fun onStop() {
        super.onStop()
        try {
            if (player != null) {
                player!!.release()
                player = null
            }
        } catch (e: Exception) {
        }
    }
}