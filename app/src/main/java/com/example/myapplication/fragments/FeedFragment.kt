package com.example.myapplication.fragments

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.R
import com.example.myapplication.activities.MainActivity
import com.example.myapplication.activities.UserDownloadActivity
import com.example.myapplication.adapters.FeedAdapter
import com.example.myapplication.adapters.RecyclerViewClickListener
import com.example.myapplication.adapters.SliderAdapter
import com.example.myapplication.adapters.SlidingImage_Adapter
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import com.example.myapplication.model.postmodel.Post
import com.example.myapplication.model.postmodel.Results
import com.example.myapplication.model.screenimagesmodel.ImageResult
import com.example.myapplication.model.screenimagesmodel.ScreenImages
import com.example.myapplication.prefrences.Constants
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.services.MusicPlayerService
import com.example.myapplication.utils.RxBus
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.VideoPlayerConfig
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.utils.ViewUtils.showSnackBar
import com.example.myapplication.view.CustomProgressBar
import com.example.myapplication.viewmodel.FeedViewModel
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util
import com.google.gson.Gson
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import com.smarteist.autoimageslider.SliderView
import com.viewpagerindicator.CirclePageIndicator
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import kotlinx.android.synthetic.main.fragment_feed_list.*
import kotlinx.android.synthetic.main.fragment_feed_list.view.*
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class FeedFragment : Fragment(), FeedLikeClickInterface, ClickInterface, RecyclerViewClickListener,Player.EventListener {
    var isScrolling: Boolean? = false
    var currentItems: Int = 0
    var totalItems: Int = 0
    var scrollOutItems: Int = 0
    var manager: LinearLayoutManager? = null
    var count: Int = 6
    var items: MutableList<Results> = mutableListOf()
    var audioItems: MutableList<Results> = mutableListOf()
    var videoItems: MutableList<Results> = mutableListOf()
    var sliderImagesList: MutableList<ImageResult> = mutableListOf()
    private lateinit var feedViewModel: FeedViewModel
    val progressBar = CustomProgressBar()
    private var mediaPlayer: MediaPlayer? = null
    private var duration: Int = 0
    private var mView:View?=null
    var  sliderImagesAdapter:SliderAdapter?=null
    var sliderView: SliderView? = null
    var playSongPosition=-1
    var playVideo=false
    var videoView:PlayerView?=null
    var playIcon:ImageView?=null
    var videoUri: String? = null
    var isRepeat=false
    var player: SimpleExoPlayer? = null
    private lateinit var disposable: Disposable
    private var mPager: ViewPager? = null
    private var currentPage = 0
    private var NUM_PAGES = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView= inflater.inflate(R.layout.fragment_feed_list, container, false)
        feedViewModel = ViewModelProvider.NewInstanceFactory().create(FeedViewModel::class.java)
        init(mView!!)
        return mView
    }
    private fun getApiData() {
        if (Utils.getInstance().isNetworkConnected(requireActivity())) {
            val hashMap = HashMap<String, String>()
            hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN, "")
            hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN, "")
            feedViewModel.getFeeds(hashMap)
            getResponse()
        } else {
            showErrorDialog()
        }
    }
    private fun showErrorDialog(){
        showSnackBar(requireContext(),"No Internet Connection",false,"Retry").subscribe{ res->
            if(res){
                getApiData()
            }
        }
    }

    private fun init(view: View) {
        mView!!.searchEdit.setOnClickListener {
            Utils.getInstance().hideKeyboard(requireActivity())
            startActivity(Intent(requireContext(),UserDownloadActivity::class.java))
        }
        if (Utils.getInstance().isNetworkConnected(requireActivity())) {
            getApiData()
        } else {
            showSnackBar(requireContext(), "No Internet Connection", false, "Retry").subscribe { res ->
                if (res) {
                    getApiData()
                }
            }
        }
        feedViewModel.getScreenImages()
        getImagesResponse()
        progressBar.show(activity as MainActivity)
        manager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        view.feedRecyclerview?.adapter = FeedAdapter(items, (activity as MainActivity), this, this)
        view.feedRecyclerview?.layoutManager = manager
        view.swipeToRefresh?.setOnRefreshListener {
            view.swipeToRefresh.isRefreshing = false
        }
        listenRxResponse()
        view.feedRecyclerview?.adapter?.notifyDataSetChanged()
        pagination(view)
//        setSliderImages(sliderImagesList)
        setUp()
    }

    private fun setUp() {
        if (videoUri == null) {
            return
        }
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
            player = ExoPlayerFactory.newSimpleInstance(DefaultRenderersFactory(requireContext()), trackSelector, loadControl)
            videoFullScreenPlayer.setPlayer(player)
        }
    }

    private fun buildMediaSource(mUri: Uri) {
        // Measures bandwidth during playback. Can be null if not required.
        val bandwidthMeter = DefaultBandwidthMeter()
        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory: DataSource.Factory =DefaultDataSourceFactory(requireContext(), Util.getUserAgent(requireContext(), getString(R.string.app_name)), bandwidthMeter)
        // This is the MediaSource representing the media to be played.
        val videoSource: MediaSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(mUri)
        // Prepare the player with the source.
        player!!.prepare(videoSource)
        player!!.playWhenReady = true
        player!!.addListener(this)
    }
    override fun onResume() {
        super.onResume()
        if(SharedPref.read(SharedPref.PLAY_SONG_POSITION,-1)!=-1){
        if(SharedPref.read(SharedPref.PLAY_SONG_POSITION,-1)!=null){
            view!!.feedRecyclerview?.adapter!!.notifyDataSetChanged()
            view!!.feedRecyclerview.smoothScrollToPosition(SharedPref.read(SharedPref.PLAY_SONG_POSITION,-1))
        } }
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
                if(!isRepeat) {
                    scrollToNextItem()
                }else if(isRepeat&&Constants.repeatCount==2){
                    scrollToNextItem()
                }else if(isRepeat&&Constants.repeatCount<2){
                    if(Constants.repeatCount>0) {
                        Constants.repeatCount = Constants.repeatCount - 1
                        playCurrentItem()
                    }else{
                        isRepeat=false
                        scrollToNextItem()
                        Constants.repeatCount = Constants.repeatCount - 1
                        feedRecyclerview.adapter!!.notifyDataSetChanged()
                    }
                }else{
                    playMusic("")
                }
            }
            Player.STATE_IDLE -> {
            }
            Player.STATE_READY ->{}
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

    private fun scrollToNextItem(){
        try {
            if(items!=null&&items.size>0) {
                SharedPref.clearKey(SharedPref.PLAY_SONG_POSITION)
                if (items[playSongPosition + 1].content.filePath != null) {
                    Constants.position = playSongPosition + 1
                    view!!.feedRecyclerview?.adapter!!.notifyDataSetChanged()
                    view!!.feedRecyclerview.scrollToPosition(playSongPosition + 1)
                    if (items[playSongPosition + 1].content.filePath.contains(".mp3") || items[playSongPosition + 1].content.filePath.contains(".m4a")) {
                        stopMediaPlayer()
                        playMusic(items[playSongPosition + 1].content.filePath)
                        playSongPosition += 1
                    } else if (items[playSongPosition + 1].content.filePath.contains(".mp4")) {
                        view!!.feedRecyclerview?.adapter!!.notifyDataSetChanged()
                        view!!.feedRecyclerview.scrollToPosition(playSongPosition + 1)
                        videoView!!.bringToFront()
                        if (player != null) {
                            player!!.release()
                            player = null
                        }
                        stopMediaPlayer()
                        initializePlayer(videoView!!)
                        buildMediaSource(Uri.parse("http://44.231.47.188" + items[playSongPosition + 1].content.filePath))
                        playSongPosition += 1
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun playCurrentItem(){
        try {
            if(items!=null&&items.size>0) {
                SharedPref.clearKey(SharedPref.PLAY_SONG_POSITION)
                if (items[playSongPosition ].content.filePath != null) {
//                    Constants.position = playSongPosition + 1
                    view!!.feedRecyclerview?.adapter!!.notifyDataSetChanged()
                    view!!.feedRecyclerview.scrollToPosition(playSongPosition)
                    if (items[playSongPosition].content.filePath.contains(".mp3") || items[playSongPosition ].content.filePath.contains(".m4a")) {
                        stopMediaPlayer()
                        playMusic(items[playSongPosition].content.filePath)
//                        playSongPosition += 1
                    } else if (items[playSongPosition ].content.filePath.contains(".mp4")) {
                        view!!.feedRecyclerview?.adapter!!.notifyDataSetChanged()
                        view!!.feedRecyclerview.scrollToPosition(playSongPosition)
                        videoView!!.bringToFront()
                        if (player != null) {
                            player!!.release()
                            player = null
                        }
                        stopMediaPlayer()
                        initializePlayer(videoView!!)
                        buildMediaSource(Uri.parse("http://44.231.47.188" + items[playSongPosition ].content.filePath))
//                        playSongPosition += 1
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun setSliderImages(imagesList:List<ImageResult>){
        mPager = mView!!.findViewById(R.id.pager) as ViewPager
        mPager!!.setAdapter(SlidingImage_Adapter(requireContext(), imagesList as ArrayList<ImageResult>))
        val indicator: CirclePageIndicator = mView!!.findViewById(R.id.indicator) as CirclePageIndicator
        indicator.setViewPager(mPager)
        indicator.bringToFront()
        val density = resources.displayMetrics.density
        indicator.radius = 5 * density
        NUM_PAGES = imagesList[0].images.size
        val handler = Handler()
        val Update = Runnable {
            if (currentPage === NUM_PAGES) {
                currentPage = 0
            }
            mPager!!.setCurrentItem(currentPage++, true)
        }
        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        }, 3000, 3000)
        indicator.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                currentPage = position
            }

            override fun onPageScrolled(pos: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(pos: Int) {}
        })
    }
    private fun pagination(view: View) {
        view.feedRecyclerview?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    override fun showDialog(check: Boolean?, audioUrl: String?, trackId: String?, position: Int) {
    }

    override fun click(url: String, imageView: ImageView, textView: TextView) {
    }

    override fun previousButtonClick(position: Int) {

    }

    override fun nextButtonClick(position: Int) {
    }

    override fun onStop() {
        super.onStop()
        try {
            if(progressBar.dialog.isShowing) {
                progressBar.dialog.dismiss()
            }
            if (player != null) {
                player!!.release()
                player = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun clickListener(id: String?, postId: String?, likeStatus: Int, shareUrl: String?, position: Int) {
    }
    private fun getResponse() {
        feedViewModel.postsLiveData!!.observe(this.viewLifecycleOwner, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), Post::class.java)
                if (response.meta.code == 205) {
                    if(response.results.size>0) {
                        for(i in 0 until response.results.size ) {
                            if(response.results[i].content!=null&&response.results[i].content.filePath!=null) {
                                if(response.results[i].content.filePath.contains(".mp3")||response.results[i].content.filePath.contains(".m4a")){
                                    audioItems.add(response.results[i])
                                }else{
                                    videoItems.add(response.results[i])
                                }
                            }
                        }
                        try {
                            for(j in 0 until audioItems.size ) {
                                if(response.results[j].content!=null&&response.results[j].content.filePath!=null) {
                                    items.add(audioItems[j])
                                    if(videoItems[j]!=null){
                                        items.add(videoItems[j])
                                    }
                                }
                            }
                        } catch (e: Exception) {
                        }
                        setVisibilty(true)
                    }else{
                        setVisibilty(false)
                    }
                    for(i in 0 until items.size){
                        if(items[i].content!=null&&items[i].content.filePath!=null) {
                            downloadFile("http://44.231.47.188" + items[i].content.filePath,i)
                        }
                    }
                    feedRecyclerview.adapter!!.notifyDataSetChanged()
                    progressBar.dialog.dismiss()
                }else{
                    if(response.meta.code!=303) {
                        setVisibilty(false)
                        showSnackBar(requireContext(), response.meta.message, false, "Retry").subscribe { res ->
                            if (res) {
                                getApiData()
                            }
                        }
                    }else{
                        ViewUtils.clearLocalStorage(requireContext(),"Your session is expired.Please login again",response.meta.code)
                    }
                }
            }else{
                showSnackBar(requireContext(),"Server is not responding",false,"Retry").subscribe{ res->
                    if(res){
                        getApiData()
                    }
                }
                setVisibilty(false)
            }
        })
    }
    private fun getImagesResponse() {
        feedViewModel.screenImagesLiveData!!.observe(this.viewLifecycleOwner, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), ScreenImages::class.java)
                if (response.meta.code == 210) {
                    sliderImagesList.clear()
                    sliderImagesList.addAll(listOf(response.result))
                    setSliderImages(sliderImagesList)
//                    sliderImagesAdapter!!.notifyDataSetChanged()
                }else{
                    Toast.makeText(activity!!,response.meta.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    override fun onClick(position: Int,video_view: PlayerView,songUrl:String,imageView: ImageView) {
        videoView=video_view
        playIcon=imageView
        playSongPosition=-1
        playSongPosition=position
        stopMediaPlayer()
        if(items[position].content.filePath.contains(".mp3")||items[position].content.filePath.contains(".m4a")) {
            Constants.serviceSongsArrayList.add(items[position].content.filePath)
            val svc = Intent(requireContext(), MusicPlayerService::class.java)
            activity!!.startService(svc)
        }else if (items[position].content.filePath.contains(".mp4")){
            video_view.bringToFront()
            if (player != null) {
                player!!.release()
                player = null
            }
            initializePlayer(video_view)
            buildMediaSource(Uri.parse("http://44.231.47.188"+items[position].content.filePath))
        }
    }

    override fun stopMediaPlayer(position: Int) {
        stopMediaPlayer()
    }

    override fun previousButtonClick(position: Int,video_view: PlayerView, songUrl: String,imageView: ImageView) {
        videoView=video_view
        playIcon=imageView
        playSongPosition=-1
        playSongPosition=position
        mView!!.feedRecyclerview.scrollToPosition(position)
        stopMediaPlayer()
        if(items[position].content.filePath.contains(".mp3")||items[position].content.filePath.contains(".m4a")) {
            Constants.serviceSongsArrayList.add(items[position].content.filePath)
            val svc = Intent(requireContext(), MusicPlayerService::class.java)
            activity!!.startService(svc)
        }else if (items[position].content.filePath.contains(".mp4")){
            video_view.bringToFront()
            if (player != null) {
                player!!.release()
                player = null
            }
            initializePlayer(video_view)
            buildMediaSource(Uri.parse("http://44.231.47.188"+items[position].content.filePath))
        }
    }

    override fun nextButtonClick(position: Int,video_view: PlayerView, songUrl: String,imageView: ImageView) {
        playSongPosition=-1
        videoView=video_view
        playIcon=imageView
        playSongPosition=position
        mView!!.feedRecyclerview.scrollToPosition(position)
        stopMediaPlayer()
        if(items[position].content.filePath.contains(".mp3")||items[position].content.filePath.contains(".m4a")) {
            Constants.serviceSongsArrayList.add(items[position].content.filePath)
            val svc = Intent(requireContext(), MusicPlayerService::class.java)
            activity!!.startService(svc)
        }else if (items[position].content.filePath.contains(".mp4")){
            video_view.bringToFront()
            if (player != null) {
                player!!.release()
                player = null
            }
            initializePlayer(video_view)
            buildMediaSource(Uri.parse("http://44.231.47.188"+items[position].content.filePath))
        }
    }

    override fun randomButtonClick(position: Int,video_view: PlayerView, songUrl: String,imageView: ImageView) {
        playSongPosition=-1
        Constants.isShuffle=true
        videoView=video_view
        playIcon=imageView
        playSongPosition=position
        mView!!.feedRecyclerview.scrollToPosition(position)
        stopMediaPlayer()
        if(items[position].content.filePath.contains(".mp3")||items[position].content.filePath.contains(".m4a")) {
            Constants.serviceSongsArrayList.add(items[position].content.filePath)
            val svc = Intent(requireContext(), MusicPlayerService::class.java)
            activity!!.startService(svc)
        }else if (items[position].content.filePath.contains(".mp4")){
            video_view.bringToFront()
            if (player != null) {
                player!!.release()
                player = null
            }
            initializePlayer(video_view)
            buildMediaSource(Uri.parse("http://44.231.47.188"+items[position].content.filePath))        }
    }

    override fun repeatClick(position: Int, video_view: PlayerView, songUrl: String, imageView: ImageView) {
        videoView=video_view
        playIcon=imageView
        playSongPosition=-1
        playSongPosition=position
        if(Constants.repeatCount<2) {
            isRepeat = true
        }else{
            isRepeat = true
        }
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer != null && mediaPlayer!!.isPlaying || player != null) {
            SharedPref.write(SharedPref.PLAY_SONG_POSITION, playSongPosition + 1)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mediaPlayer!=null&&mediaPlayer!!.isPlaying||player!=null) {
            SharedPref.write(SharedPref.PLAY_SONG_POSITION, playSongPosition + 1)
        }
    }
    private fun downloadFile(url: String,index:Int) {
        if(!progressBar.dialog.isShowing) {
            progressBar.show(requireContext())
        }
        FileLoader.with(requireContext()).load(url, false) //2nd parameter is optioal, pass true to force load from network
            .fromDirectory("test4", FileLoader.DIR_INTERNAL).asFile(object : FileRequestListener<File> {
                override fun onLoad(request: FileLoadRequest, response: FileResponse<File>) {
                    val loadedFile = response.body
                    if(loadedFile.path.contains(".mp3")||loadedFile.path.contains(".m4a")) {
                        items[index].content.filePath=loadedFile.path
                        }
                    progressBar.dialog.dismiss()
                }
                override fun onError(request: FileLoadRequest, t: Throwable) {
                    progressBar.dialog.dismiss()
                }
            })
    }
    private fun playMusic(filePath: String) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer!!.setDataSource(filePath)
        mediaPlayer!!.prepare()
        mediaPlayer!!.start()
        SharedPref.write(SharedPref.PLAY_SONG_POSITION,playSongPosition)
        duration = mediaPlayer!!.duration
        mediaPlayer!!.setOnCompletionListener {
            mediaPlayer!!.stop()
            playIcon!!.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp)
            SharedPref.clearKey(SharedPref.PLAY_SONG_POSITION)
            if(!Constants.isShuffle){
            if(items[playSongPosition+1].content.filePath!=null) {
                if (items[playSongPosition + 1].content.filePath.contains(".mp3") || items[playSongPosition + 1].content.filePath.contains(".m4a")) {
                    stopMediaPlayer()
                    playMusic(items[playSongPosition + 1].content.filePath)
                } else if (items[playSongPosition + 1].content.filePath.contains(".mp4")) {
                    videoView!!.bringToFront()
                    if (player != null) {
                        player!!.release()
                        player = null
                    }
                    initializePlayer(videoView!!)
                    buildMediaSource(Uri.parse("http://44.231.47.188" + items[playSongPosition + 1].content.filePath))
                }
            }else{
                playRandom()
            }
            }
        }
    }
    private fun playRandom(){
        val random = Random()
        val songIndex = random.nextInt(items.size)
        mView!!.feedRecyclerview.scrollToPosition(songIndex)
        if (songIndex >= 0 && items.size>1) {
            try {
                if(items[songIndex].content!=null) {
                    if(items[songIndex].content.filePath!=null) {
                        if (items[songIndex].content.filePath.contains(".mp3") || items[songIndex].content.filePath.contains(".m4a")) {
                            stopMediaPlayer()
                            playMusic(items[songIndex].content.filePath)
                        } else if (items[songIndex].content.filePath.contains(".mp4")) {
                            videoView!!.bringToFront()
                            if (player != null) {
                                player!!.release()
                                player = null
                            }
                            initializePlayer(videoView!!)
                            buildMediaSource(Uri.parse("http://44.231.47.188" + items[songIndex].content.filePath))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else{
//            refreshSong()
        }
    }
    private fun stopMediaPlayer() {
        try {
            val svc = Intent(requireContext(), MusicPlayerService::class.java)
            activity!!.stopService(svc)
            if (player != null) {
                player!!.release()
                player = null
            }
            if(!playVideo) {
                if (mediaPlayer != null) {
                    try {
                        mediaPlayer?.stop()
                        mediaPlayer?.release()
                        mediaPlayer = null
                    } catch (ignored: Exception) {
                        mediaPlayer = null
                    }
                } else {
                    mediaPlayer = null
                }
            }else{
                playVideo=false
            }
        } catch (ignored: Exception) {
        }
    }
    private fun setVisibilty(flag:Boolean){
        if(flag){
            no_data_textview.visibility=View.GONE
            swipeToRefresh.visibility=View.VISIBLE
        }else{
            no_data_textview.visibility=View.VISIBLE
            swipeToRefresh.visibility=View.GONE
        }
    }
    override fun onDetach() {
        super.onDetach()
        try {
            if (progressBar.dialog.isShowing) {
                progressBar.dialog.dismiss()
            }
            if(mediaPlayer!=null&&mediaPlayer!!.isPlaying||player!=null) {
                SharedPref.write(SharedPref.PLAY_SONG_POSITION, playSongPosition + 1)
            }
        } catch (e: Exception) {
        }
    }

    private fun listenRxResponse(){
        disposable = RxBus.subscribe(Consumer<Any?> {
            if(!isRepeat) {
                scrollToNextItem()
            }else if(isRepeat&&Constants.repeatCount==2){
                scrollToNextItem()
            }else if(isRepeat&&Constants.repeatCount<2){
                if(Constants.repeatCount>0) {
                    Constants.repeatCount = Constants.repeatCount - 1
//                    playMusic("")
                    playCurrentItem()
                    feedRecyclerview.adapter!!.notifyDataSetChanged()
                }else{
                    isRepeat=false
                    scrollToNextItem()
                    Constants.repeatCount = Constants.repeatCount - 1
                    feedRecyclerview.adapter!!.notifyDataSetChanged()
                }
            }else{
                playMusic("")
            }
        })
    }
}