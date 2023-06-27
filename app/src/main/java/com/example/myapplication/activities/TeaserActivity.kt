package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.model.CartModel
import com.example.myapplication.model.albumdetailmodel.Song
import com.example.myapplication.model.albummodel.Album
import com.example.myapplication.model.userdownloadsmodel.UserDownloadResult
import com.example.myapplication.prefrences.Constants
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.RxBus
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.viewmodel.SongDownloadViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_teaser.*
import org.json.JSONObject
import java.util.*

class TeaserActivity : BaseActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private var albumDetailModel: Song? = null
    private var playlistResult: UserDownloadResult? = null
    private var songListItem: com.example.myapplication.model.categoriessongmodel.Song? = null
    private var playlistTrackResult: com.example.myapplication.model.playlistdetailmodel.Song? = null
    private var isPlayFlag = false
    private var timeWhenStopped: Long = 0
    private var mediaPlayerLength = 0
    private var duration = ""
    private var mediaPlayerDuration = 0
    private var position = 0
    private var songPosition = 0
    private var completeFlag = false
    private var firstTimePlay = false
    private var isShuffle = false
    private var isRepeat = false
    private var isPurchased = false
    private var seconds:Long=0
    private var elapsedMillis:Long=0
    private var songId=-1
    private var songName=""
    private  var songImagePath=""
    private var from=""
    private var songPrice=0
    private var startPosition=0
    private lateinit var runnable:Runnable
    private var handler: Handler = Handler()
    private lateinit var userDownloadsViewModel: SongDownloadViewModel
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teaser)
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {

            mediaPlayer!!.stop()
            mediaPlayer!!.release()
        }
        userDownloadsViewModel = ViewModelProvider.NewInstanceFactory().create(SongDownloadViewModel::class.java)
        back_arrow_imageview.setOnClickListener {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                moveTaskToBack(true)
//                super.onBackPressed()
            } else {
                super.onBackPressed()
//                finish()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startPostponedEnterTransition()
            }
        }
        if (intent.hasExtra("id")) {
            songId = intent.getIntExtra("id", -1)
        }
        if(intent.hasExtra("from")){
            from=intent.getStringExtra("from")
        }
        try {
            position = intent.getIntExtra("position", 0)
            songPosition = intent.getIntExtra("songPosition", 0)
            if(intent.hasExtra("songPosition")) {
                if (Constants.songsArrayList.size > 0) {
                    position=intent.getIntExtra("songPosition", 0)
                    playMusic(Constants.songsArrayList[position])
                }
            }else{
                if (Constants.songsArrayList.size > 0) {
                    playMusic(Constants.songsArrayList[intent.getIntExtra("position", 0)])
                }
            }
            startPosition=position
            if (intent.hasExtra("currentItem")) {
                albumDetailModel = intent.getSerializableExtra("currentItem") as Song
                track_name_textview.text = albumDetailModel!!.name
                artist_name_textview.text = albumDetailModel!!.artistName
            }
            if (intent.hasExtra("playlistItemData")) {
                playlistTrackResult = intent.getSerializableExtra("playlistItemData") as com.example.myapplication.model.playlistdetailmodel.Song
                track_name_textview.text = playlistTrackResult!!.name
                songImagePath="http://44.231.47.188" + playlistTrackResult!!.imagePath
                Glide.with(this@TeaserActivity).load("http://44.231.47.188" + playlistTrackResult!!.imagePath).placeholder(R.drawable.banner).error(R.drawable.banner).into(profile_image)
                if(!playlistTrackResult!!.name.isNullOrEmpty()){
                    songName=playlistTrackResult!!.name
                }
                if(playlistTrackResult!!.price!=null){
                    songPrice=playlistTrackResult!!.price
                    add_to_cart_textview.text="Acheter mtn: CFA "+songPrice.toString()
                }else{
                    add_to_cart_textview.text="Acheter mtn: CFA 0"
                }
                if(playlistTrackResult!!.isDownloaded) {
                    donwload_imageview.visibility = View.GONE
                }
                isPurchased=playlistTrackResult!!.isPurchased
            }
            if (intent.hasExtra("downloadsTrackData")) {
                playlistResult = intent.getSerializableExtra("downloadsTrackData") as UserDownloadResult
                track_name_textview.text = playlistResult!!.name
                songImagePath="http://44.231.47.188" + playlistResult!!.imagePath
                Glide.with(this@TeaserActivity).load("http://44.231.47.188" + playlistResult!!.imagePath).placeholder(R.drawable.banner).error(R.drawable.banner).into(profile_image)
                donwload_imageview.visibility= View.GONE
                add_to_cart_textview.visibility= View.GONE
                isPurchased=true
            }
            if (intent.hasExtra("albumDetailTrackData")) {
                songListItem = intent.getSerializableExtra("albumDetailTrackData") as com.example.myapplication.model.categoriessongmodel.Song
                track_name_textview.text = songListItem!!.name
                songImagePath="http://44.231.47.188" + songListItem!!.imagePath
                Glide.with(this@TeaserActivity).load("http://44.231.47.188" + songListItem!!.imagePath).placeholder(R.drawable.banner).error(R.drawable.banner).into(profile_image)
                if(songListItem!!.isDownloaded) {
                    donwload_imageview.visibility = View.GONE
                }
                if(!songListItem!!.name.isNullOrEmpty()){
                    songName=songListItem!!.name
                }
                if(songListItem!!.price!=null){
                    songPrice=songListItem!!.price.toInt()
                    add_to_cart_textview.text="Acheter mtn: CFA "+songPrice.toString()
                }else{
                    add_to_cart_textview.text="Acheter mtn: CFA 0"
                }
                isPurchased=songListItem!!.isPurchased
            }
            if (intent.hasExtra("albumSongTrackData")) {
                songListItem = intent.getSerializableExtra("albumSongTrackData") as com.example.myapplication.model.categoriessongmodel.Song
                track_name_textview.text = songListItem!!.name
                songImagePath="http://44.231.47.188" + songListItem!!.imagePath
                Glide.with(this@TeaserActivity).load("http://44.231.47.188" + songListItem!!.imagePath).placeholder(R.drawable.banner).error(R.drawable.banner).into(profile_image)
                if(songListItem!!.isDownloaded) {
                    donwload_imageview.visibility = View.GONE
                }
                if(!songListItem!!.name.isNullOrEmpty()){
                    songName=songListItem!!.name
                }
                if(songListItem!!.price!=null){
                    songPrice=songListItem!!.price.toInt()
                    add_to_cart_textview.text="Acheter mtn: CFA "+songPrice.toString()
                }else{
                    add_to_cart_textview.text="Acheter mtn: CFA 0"
                }
                isPurchased=songListItem!!.isPurchased
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        add_to_cart_textview.setOnClickListener {
            if(isItemExist()){
                ViewUtils.showSnackBar(this,"Song is already added to cart successfully",true,"")
            }else{
                ViewUtils.show(this,"Are you sure you want to add product to cart").subscribe {res->
                    if(res){
                        stopPlayer()
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
        play_song_imageview.setOnClickListener {
            playSong(position)
            isShuffle=false
            shuffle_music_play.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
        shuffle_music_play.setOnClickListener {
            isShuffle=true
            playRandom()
            shuffle_music_play.setColorFilter(ContextCompat.getColor(this, R.color.purple), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
        previous_song_play_icon.setOnClickListener {
            if (position > 0) {
                try {
                    if(mediaPlayer!=null) {
                        if (mediaPlayer!!.isPlaying) {
                            mediaPlayer!!.stop()
                        }
                        mediaPlayer!!.release()
                        mediaPlayer=null
                    }
                    isPlayFlag=false
                    completeFlag = false
                    firstTimePlay=false
                    timeWhenStopped=0
                    seconds=0
                    elapsedMillis=0
                    seekBar.progress = 0
                    playMusic(Constants.songsArrayList[position - 1])
                    playSong(position-1)
                    if(from.equals("fromPlaylist")){
                        songImagePath="http://44.231.47.188" + Constants.playlistItems[position-1].imagePath
                        Glide.with(this@TeaserActivity).load(songImagePath).placeholder(R.drawable.banner).error(R.drawable.banner).into(profile_image)
                        if(Constants.playlistItems[position-1].price!=null){
                            songPrice= Constants.playlistItems[position - 1].price.toInt()
                            add_to_cart_textview.text="Acheter mtn: CFA "+songPrice.toString()
                        }else{
                            add_to_cart_textview.text="Acheter mtn: CFA 0"
                        }
                        if(!Constants.playlistItems[position - 1].name.isNullOrEmpty()){
                            songName= Constants.playlistItems[position - 1].name
                        }
                        track_name_textview.text=songName
                    }else  if(from.equals("fromDownloads")){
                        songImagePath="http://44.231.47.188" + Constants.downloadListItems[position-1].imagePath
                        Glide.with(this@TeaserActivity).load(songImagePath).placeholder(R.drawable.banner).error(R.drawable.banner).into(profile_image)
                        if(Constants.downloadListItems[position - 1].price!=null){
                            songPrice= Constants.downloadListItems[position - 1].price.toInt()
                            add_to_cart_textview.text="Acheter mtn: CFA "+songPrice.toString()
                        }else{
                            add_to_cart_textview.text="Acheter mtn: CFA 0"
                        }
                        if(!Constants.downloadListItems[position - 1].name.isNullOrEmpty()){
                            songName= Constants.downloadListItems[position - 1].name
                        }
                        track_name_textview.text=songName
                    }else  if(from.equals("fromAlbumCategories")){
                        songImagePath="http://44.231.47.188" + Constants.albumCategoryListItems[position-1].imagePath
                        Glide.with(this@TeaserActivity).load(songImagePath).placeholder(R.drawable.banner).error(R.drawable.banner).into(profile_image)
                        if(Constants.albumCategoryListItems[position - 1].price!=null){
                            songPrice= Constants.albumCategoryListItems[position - 1].price.toInt()
                            add_to_cart_textview.text="Acheter mtn: CFA "+songPrice.toString()
                        }else{
                            add_to_cart_textview.text="Acheter mtn: CFA 0"
                        }
                        if(!Constants.albumCategoryListItems[position - 1].name.isNullOrEmpty()){
                            songName= Constants.albumCategoryListItems[position - 1].name
                        }
                        track_name_textview.text=songName
                    }
                    position -= 1
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        next_song_play_icon.setOnClickListener {
            try {
                if (position >= 0 && Constants.songsArrayList[position + 1] != null) {
                    try {
                        if(mediaPlayer!=null) {
                            if (mediaPlayer!!.isPlaying) {
                                mediaPlayer!!.stop()
//                                mediaPlayer = null
                            }
                            mediaPlayer!!.release()
                            mediaPlayer=null
                        }
                        isPlayFlag=false
                        completeFlag = false
                        firstTimePlay=false
                        timeWhenStopped=0
                        seconds=0
                        elapsedMillis=0
                        seekBar.progress = 0
                        playMusic(Constants.songsArrayList[position + 1])
                        playSong(position+1)
                        if(from.equals("fromPlaylist")){
                            songImagePath="http://44.231.47.188" + Constants.playlistItems[position+1].imagePath
                            Glide.with(this@TeaserActivity).load(songImagePath).placeholder(R.drawable.banner).error(R.drawable.banner).into(profile_image)
                            if(Constants.playlistItems[position + 1].price!=null){
                                songPrice=Constants.playlistItems[position+1].price.toInt()
                                add_to_cart_textview.text="Acheter mtn: CFA "+songPrice.toString()
                            }else{
                                add_to_cart_textview.text="Acheter mtn: CFA 0"
                            }
                            if(!Constants.playlistItems[position+1].name.isNullOrEmpty()){
                                songName=Constants.playlistItems[position+1].name
                            }
                            track_name_textview.text=songName
                            isPurchased=Constants.playlistItems[position+1].isPurchased
                        }  else  if(from.equals("fromDownloads")){
                            songImagePath="http://44.231.47.188" + Constants.downloadListItems[position+1].imagePath
                            Glide.with(this@TeaserActivity).load(songImagePath).placeholder(R.drawable.banner).error(R.drawable.banner).into(profile_image)
                            if(Constants.downloadListItems[position+1].price!=null){
                                songPrice=Constants.downloadListItems[position+1].price.toInt()
                                add_to_cart_textview.text="Acheter mtn: CFA "+songPrice.toString()
                            }else{
                                add_to_cart_textview.text="Acheter mtn: CFA 0"
                            }
                            if(!Constants.downloadListItems[position+1].name.isNullOrEmpty()){
                                songName=Constants.downloadListItems[position+1].name
                            }
                            track_name_textview.text=songName
                            add_to_cart_textview.visibility=View.GONE
                        }else  if(from.equals("fromAlbumCategories")){
                            songImagePath="http://44.231.47.188" + Constants.albumCategoryListItems[position+1].imagePath
                            Glide.with(this@TeaserActivity).load(songImagePath).placeholder(R.drawable.banner).error(R.drawable.banner).into(profile_image)
                            if(Constants.albumCategoryListItems[position+1].price!=null){
                                songPrice=Constants.albumCategoryListItems[position+1].price.toInt()
                                add_to_cart_textview.text="Acheter mtn: CFA "+songPrice.toString()
                            }else{
                                add_to_cart_textview.text="Acheter mtn: CFA 0"
                            }
                            if(!Constants.albumCategoryListItems[position+1].name.isNullOrEmpty()){
                                songName=Constants.albumCategoryListItems[position+1].name
                            }
                            track_name_textview.text=songName
                            isPurchased=Constants.albumCategoryListItems[position+1].isPurchased
                        }
                        position += 1
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
            }
        }
        refresh_music_player.setOnClickListener {
            if(Constants.repeatCount<=1){
                Constants.repeatCount=Constants.repeatCount+1
                if(Constants.repeatCount==2){
                    count_textview.text="All"
                }else{
                    count_textview.text=Constants.repeatCount.toString()
                }
                count_textview.visibility=View.VISIBLE
                isRepeat=true
                refresh_music_player.setColorFilter(ContextCompat.getColor(this, R.color.purple), android.graphics.PorterDuff.Mode.MULTIPLY);
            }else{
                Constants.repeatCount=-1
                isRepeat=false
                count_textview.visibility=View.GONE
                refresh_music_player.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        }
        add_to_playlist_imageview.setOnClickListener {
            startActivity(Intent(this, PlaylistsActivity::class.java).putExtra("id",songId).putExtra("title","Select Playlist"))
        }
        initializeSeekBar()
    }
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

    private fun isItemExist(): Boolean {
        var flag = false
        for (i in 0 until Constants.cartArrayList.size) {
            if (Constants.cartArrayList[i].id == songId) {
                flag = true
            }
        }
        return flag
    }
    private fun refreshSong(){
        try {
            if (position >= 0 ) {
                try {
                    if(mediaPlayer!=null) {
                        if (mediaPlayer!!.isPlaying) {
                            mediaPlayer!!.stop()
                            mediaPlayer = null
                        }
                    }
                    isPlayFlag=false
                    completeFlag = false
                    firstTimePlay=false
                    timeWhenStopped=0
                    seconds=0
                    elapsedMillis=0
                    seekBar.progress = 0
                    playMusic(Constants.songsArrayList[position])
                    playSong(position)
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playSong(position: Int) {
        if (mediaPlayer == null) {
            if(intent.hasExtra("songPosition")) {
                if (Constants.songsArrayList.size > 0) {
                    playMusic(Constants.songsArrayList[position])
                }
            }else {
                playMusic(Constants.songsArrayList[position])
            }
        }else{
            playMusic(Constants.songsArrayList[position])
        }
        if (!isPlayFlag) {
            isPlayFlag = true
            Glide.with(this).load(R.drawable.ic_baseline_pause_24).into(play_icon_imageview)
            if (mediaPlayer != null && !firstTimePlay) {
                firstTimePlay = true
                startTime()
            }
            if (mediaPlayerLength != 0) {
                mediaPlayer?.seekTo(mediaPlayerLength)
            }
            mediaPlayer?.start()
        } else {
            isPlayFlag = false
            Glide.with(this).load(R.drawable.ic_baseline_play_arrow_24).into(play_icon_imageview)
            mediaPlayer?.pause()
            mediaPlayerLength = mediaPlayer?.currentPosition!!
            if (mediaPlayerLength != 0) {
                mediaPlayer?.seekTo(mediaPlayerLength)
            }
            if (mediaPlayer != null) {
                mediaPlayer!!.pause()
            }
        }
    }
    private fun playMusic(filePath: String) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
//                Constants.mediaPlayer=mediaPlayer
            }
            val myUri: Uri = Uri.parse(filePath)
            mediaPlayer!!.setDataSource(this@TeaserActivity,myUri)
            mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer!!.prepare()
            mediaPlayerDuration = (mediaPlayer!!.duration / 1000)
            duration = Utils.getInstance().getDurationInMinutes(mediaPlayer!!.duration.toLong())
            total_duration_textview.text = duration
            seekBar.max = mediaPlayerDuration
            duration_textview.text = duration
            mediaPlayer!!.setOnCompletionListener {
                if(isShuffle){
                    playRandom()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun startTime() {

        // Seek bar change listener
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                try {
                    if (b) {
                        mediaPlayer!!.seekTo(i * 1000)
                        if(!isPurchased){
                            remaining_time_textview.text = Utils.getInstance().getDurationString(mediaPlayer!!.currentSeconds)+" sec"
                            if (i>= 20) {
                                if(mediaPlayer!!.isPlaying) {
                                    mediaPlayer!!.stop()
                                    mediaPlayer=null
                                }
                                stopPlayer()
                                stopMediaPlayer()
                                ViewUtils.show(this@TeaserActivity,"Please purchase the song to listen completely").subscribe {res->
                                    if(res){
                                        if(isItemExist()){
                                            ViewUtils.showSnackBar(this@TeaserActivity,"Song is already added to cart successfully",true,"")
                                        }else{
                                            ViewUtils.showSnackBar(this@TeaserActivity,"Song added to cart successfully",true,"")
                                            Constants.cartArrayList.add(CartModel(songId,songName,songImagePath,songPrice))
                                            RxBus.publish( CartModel(songId,songName,songImagePath,songPrice));
                                        }
                                    }
                                }
                            }
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
    }

    private fun initializeSeekBar() {
        try {
            if(mediaPlayer!=null&&mediaPlayer!!.isPlaying) {
                seekBar.max = mediaPlayer!!.seconds
            }
            runnable = Runnable {
                handler.postDelayed(runnable, 1000)
                if(mediaPlayer!=null&&mediaPlayer!!.isPlaying) {
                    seekBar.progress = mediaPlayer!!.currentSeconds
                    remaining_time_textview.text = Utils.getInstance().getDurationString(mediaPlayer!!.currentSeconds)+" sec"
                    val diff = mediaPlayer!!.seconds - mediaPlayer!!.currentSeconds
                    seconds=mediaPlayer!!.currentSeconds.toLong()
                }
                if(!isPurchased){
                    if(mediaPlayerDuration>=20) {
                        if (seconds.toInt() == 20) {
                            if(mediaPlayer!!.isPlaying) {
                                mediaPlayer!!.stop()
                                mediaPlayer=null
                            }
                            stopPlayer()
                            stopMediaPlayer()
                            ViewUtils.show(this@TeaserActivity,"Please purchase the song to listen completely").subscribe {res->
                                if(res){
                                    if(isItemExist()){
                                        ViewUtils.showSnackBar(this,"Song is already added to cart successfully",true,"")
                                    }else{
                                        ViewUtils.showSnackBar(this,"Song added to cart successfully",true,"")
                                        Constants.cartArrayList.add(CartModel(songId,songName,songImagePath,songPrice))
                                        RxBus.publish( CartModel(songId,songName,songImagePath,songPrice));
                                    }
                                }
                            }
                        }
                    }else if(seconds.toInt()==mediaPlayerDuration/2){
                        if(mediaPlayer!!.isPlaying) {
                            mediaPlayer!!.stop()
                            mediaPlayer=null
                        }
                        stopPlayer()
                        stopMediaPlayer()
                        ViewUtils.show(this@TeaserActivity,"Please purchase the song to listen completely").subscribe { res->
                            if(res){
                                if(isItemExist()){
                                    ViewUtils.showSnackBar(this,"Song is already added to cart successfully",true,"")
                                }else{
                                    ViewUtils.showSnackBar(this,"Song added to cart successfully",true,"")
                                    Constants.cartArrayList.add(CartModel(songId,songName,songImagePath,songPrice))
                                    RxBus.publish( CartModel(songId,songName,songImagePath,songPrice));
                                }
                            }
                        }
                    }
                }else {
                    if (seconds.toInt() == mediaPlayerDuration) {
                        stopPlayer()
                    }
                }
            }
            handler.postDelayed(runnable, 1000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun playRandom() {
        val random = Random()
        val songIndex = random.nextInt(Constants.songsArrayList.size)
        if (songIndex >= 0 && Constants.songsArrayList.size > 1) {
            try {
                if (mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.stop()
                    mediaPlayer = null
                }
                isPlayFlag = false
                completeFlag = false
                firstTimePlay = false
                timeWhenStopped = 0
                seconds = 0
                elapsedMillis = 0
                seekBar.progress = 0
                playMusic(Constants.songsArrayList[songIndex])
                playSong(songIndex)
                position = songIndex
                setContent(songIndex)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            refreshSong()
        }
    }

    override fun onStop() {
        super.onStop()
    }
    private fun stopMediaPlayer(){
        try {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                mediaPlayer = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            moveTaskToBack(true)
        } else {
            super.onBackPressed()
        }
    }

    private fun setContent(randomPosition:Int){
        if(from.equals("fromPlaylist")){
            songImagePath="http://44.231.47.188" + Constants.playlistItems[randomPosition].imagePath
            Glide.with(this@TeaserActivity).load(songImagePath).placeholder(R.drawable.banner).error(R.drawable.banner).into(profile_image)
            if(Constants.playlistItems[randomPosition].price!=null){
                songPrice=Constants.playlistItems[randomPosition].price.toInt()
                add_to_cart_textview.text="Acheter mtn: CFA "+songPrice.toString()
            }else{
                add_to_cart_textview.text="Acheter mtn: CFA 0"
            }
            if(!Constants.playlistItems[randomPosition].name.isNullOrEmpty()){
                songName=Constants.playlistItems[randomPosition].name
            }
            track_name_textview.text=songName
            isPurchased=Constants.playlistItems[randomPosition].isPurchased
        }else  if(from.equals("fromDownloads")){
            songImagePath="http://44.231.47.188" + Constants.downloadListItems[randomPosition].imagePath
            Glide.with(this@TeaserActivity).load(songImagePath).placeholder(R.drawable.banner).error(R.drawable.banner).into(profile_image)
            if(Constants.downloadListItems[randomPosition].price!=null){
                songPrice=Constants.downloadListItems[randomPosition].price.toInt()
                add_to_cart_textview.text="Acheter mtn: CFA "+songPrice.toString()
            }else{
                add_to_cart_textview.text="Acheter mtn: CFA 0"
            }
            if(!Constants.downloadListItems[randomPosition].name.isNullOrEmpty()){
                songName=Constants.downloadListItems[randomPosition].name
            }
            track_name_textview.text=songName
            add_to_cart_textview.visibility=View.GONE
        }else  if(from.equals("fromAlbumCategories")){
            songImagePath="http://44.231.47.188" + Constants.albumCategoryListItems[randomPosition].imagePath
            Glide.with(this@TeaserActivity).load(songImagePath).placeholder(R.drawable.banner).error(R.drawable.banner).into(profile_image)
            if(Constants.albumCategoryListItems[randomPosition].price!=null){
                songPrice=Constants.albumCategoryListItems[randomPosition].price.toInt()
                add_to_cart_textview.text="Acheter mtn: CFA "+songPrice.toString()
            }else{
                add_to_cart_textview.text="Acheter mtn: CFA 0"
            }
            if(!Constants.albumCategoryListItems[randomPosition].name.isNullOrEmpty()){
                songName=Constants.albumCategoryListItems[randomPosition].name
            }
            track_name_textview.text=songName
            isPurchased=Constants.albumCategoryListItems[randomPosition].isPurchased
        }
    }
    private fun stopPlayer(){
        mediaPlayer = null
        Glide.with(this).load(R.drawable.ic_baseline_play_arrow_24).into(play_icon_imageview)
        isPlayFlag = false
        completeFlag = false
        firstTimePlay = false
        timeWhenStopped = 0
        seconds = 0
        elapsedMillis = 0
        seekBar.progress = 0
        try {
            if(isPurchased) {
                if (position < Constants.songsArrayList.size && !Constants.songsArrayList[position + 1].isNullOrEmpty()) {
                    if (!isShuffle && Constants.repeatCount >= 0 && isRepeat && Constants.repeatCount < 2) {
                        Constants.repeatCount = Constants.repeatCount - 1
                        if (Constants.repeatCount == 0) {
                            isRepeat = false
                            refresh_music_player.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                            count_textview.visibility = View.GONE
                            Constants.repeatCount = -1
                        }
                        stopMediaPlayer()
                        playMusic(Constants.songsArrayList[position])
                        playSong(position)
                        setContent(position)
                    } else if (!isShuffle && Constants.repeatCount >= 0 && isRepeat && Constants.repeatCount == 2) {
                        stopMediaPlayer()
                        playMusic(Constants.songsArrayList[position + 1])
                        playSong(position + 1)
                        setContent(position + 1)
                        position += 1
                    } else if (!isShuffle && !isRepeat) {
                        stopMediaPlayer()
                        playMusic(Constants.songsArrayList[position + 1])
                        playSong(position + 1)
                        setContent(position + 1)
                        position += 1
                    } else if (isShuffle) {
                        playRandom()
                    }
                } else {
                    if (isRepeat && Constants.repeatCount == 0) {
                        position = startPosition
                        if (!isShuffle && Constants.repeatCount > 0 && isRepeat) {
                            Constants.repeatCount = Constants.repeatCount - 1
                            if (Constants.repeatCount == 0) {
                                refresh_music_player.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                                isRepeat = false
                                count_textview.visibility = View.GONE
                                Constants.repeatCount = -1
                            }
                            stopMediaPlayer()
                            playMusic(Constants.songsArrayList[position])
                            playSong(position)
                            setContent(position)
                        } else if (!isShuffle && !isRepeat) {
                            stopMediaPlayer()
                            playMusic(Constants.songsArrayList[position + 1])
                            playSong(position + 1)
                            setContent(position + 1)
                            position += 1
                        } else if (isShuffle) {
                            playRandom()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            count_textview.visibility=View.GONE
            Constants.repeatCount=-1
            refresh_music_player.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            if(isRepeat&&Constants.repeatCount==0) {
                position = startPosition
                if (!isShuffle && Constants.repeatCount > 0 && isRepeat) {
                    Constants.repeatCount = Constants.repeatCount - 1
                    if (Constants.repeatCount == 0) {
                        isRepeat = false
                    }
                    stopMediaPlayer()
                    playMusic(Constants.songsArrayList[position])
                    playSong(position)
                    setContent(position)
                } else if (!isShuffle && !isRepeat) {
                    stopMediaPlayer()
                    playMusic(Constants.songsArrayList[position + 1])
                    playSong(position + 1)
                    setContent(position + 1)
                    position += 1
                } else if (isShuffle) {
                    playRandom()
                }
            }
        }
    }

    // Creating an extension property to get the media player time duration in seconds
    val MediaPlayer.seconds:Int
        get() {
            return this.duration / 1000
        }
    // Creating an extension property to get media player current position in seconds
    val MediaPlayer.currentSeconds:Int
        get() {
            return this.currentPosition/1000
        }
}