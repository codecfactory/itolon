package com.example.myapplication.activities

import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.myapplication.R
import com.example.myapplication.base.BaseActivity
import kotlinx.android.synthetic.main.action_bar_layout.view.*
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat

class AudioBarsActivity : BaseActivity() {
    private var progressAnimator: ObjectAnimator? = null
    private var mediaPlayer: MediaPlayer? = null
    private var arrayList: ArrayList<Int>? = null
    private var progressFlag = true
    private var playRecordFlag = false
    private var timeWhenStopped: Long = 0
    private var mediaPlayerLength = 0
    private var i = 0
    private var filePath = ""
    private var duration: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teaser)
//        setSupportActionBar(toolbar)
        init()
    }

    private fun init() {
//
//        toolbar.backArrowImageView.visibility = View.VISIBLE
//        toolbar.backArrowRelativeLayout.setOnClickListener(this)
//        arrayList = ArrayList()
//        singButton.setOnClickListener(this)
//        restartButtonLayout.setOnClickListener(this)
//        restartButtonLayout.isEnabled = false
//        if(intent.hasExtra("post_detail"))
//            postDetail = intent.getSerializableExtra("post_detail") as PostDetail
//        try {
//            toolbar.titleTextview.text = postDetail?.postTitle
//            if (postDetail?.participant?.size!! > 0) {
//                for (i in postDetail?.participant?.indices!!.iterator()) {
//                    if (!postDetail?.participant?.get(i)?.startTime.equals("") && !postDetail?.participant?.get(i)?.endTime.equals("")) {
//                        val startTime = postDetail?.participant?.get(i)?.startTime.toString()
//                        val endTime = postDetail?.participant?.get(i)?.endTime.toString()
//                        val format = SimpleDateFormat("HH:mm:ss")
//                        val formatStartTime = format.parse(startTime)
//                        val formatEndTime = format.parse(endTime)
//                        val difference = formatEndTime.time - formatStartTime.time
//                        arrayList!!.add((difference / 1000).toInt())
//                    }
//                    if (postDetail?.isLocked == 0 && postDetail?.initiatorId != MyPreferences(applicationContext).getIntegerPrefrence(Constants.USER_ID, 0)) {
//                        if(postDetail?.participant?.get(i)?.participantId==MyPreferences(this).getIntegerPrefrence(Constants.USER_ID, 0)){
//                            val startTime=postDetail?.participant?.get(i)?.startTime
//                            val endTime=postDetail?.participant?.get(i)?.endTime
//                            if(startTime!!.isEmpty()&&endTime!!.isEmpty()) {
//                                singButton.visibility = View.VISIBLE
//                            }
//                            else{
//                                singButton.visibility = View.GONE
//                            }
//                        }
//                    }
//                }
//                if (!postDetail?.audioUrl?.isEmpty()!!) {
//                    showProgressDialog()
//                    downloadFile(postDetail!!.audioUrl)
//                }
//            } else {
//                try{
//                    if (!postDetail!!.audioUrl.isEmpty()) {
//                        showProgressDialog()
//                        playMusic(postDetail!!.audioUrl)
//                    } else {
//                        Utils.getInstance().infoDialog(this, "Alert", "Audio Url Not Found", false, false, null, "Ok", "Restart", false)
//                    }
//                } catch (e: Exception) {
//                }
//            }
//            if(postDetail?.participant!!.size>0){
////                for(i in postDetail?.participant!!.indices){
////                    if(i==0){
////                        Glide.with(this).load(postDetail?.participant!![0].participantAvatar).centerCrop().error(R.drawable.default_dp).placeholder(R.drawable.default_dp).into(userImageView)
////                        imageLayout.visibility = View.VISIBLE
////                    }else if(i==1){
////                    Glide.with(this).load(postDetail?.participant!![1].participantAvatar).centerCrop().error(R.drawable.default_dp).placeholder(R.drawable.default_dp).into(secondUserImageView)
////                    secondImageLayout.visibility=View.VISIBLE
////                    }else if(i==2){
////                     Glide.with(this).load(postDetail?.participant!![2].participantAvatar).centerCrop().error(R.drawable.default_dp).placeholder(R.drawable.default_dp).into(thirdUserImageView)
////                    thirdImageLayout.visibility=View.VISIBLE
////                    }else if(i==3){
////                    Glide.with(this).load(postDetail?.participant!![3].participantAvatar).centerCrop().error(R.drawable.default_dp).placeholder(R.drawable.default_dp).into(fourthUserImageView)
////                    fourthImageLayout.visibility=View.VISIBLE
////                    }
////                }
//                if(postDetail?.participant!!.size==1){
//                    Glide.with(this).load(postDetail?.participant!![0].participantAvatar).centerCrop().error(R.drawable.default_dp).placeholder(R.drawable.default_dp).into(userImageView)
//                    imageLayout.visibility=View.VISIBLE
//                }else if(postDetail?.participant!!.size==2){
//                    Glide.with(this).load(postDetail?.participant!![0].participantAvatar).centerCrop().error(R.drawable.default_dp).placeholder(R.drawable.default_dp).into(userImageView)
//                    Glide.with(this).load(postDetail?.participant!![1].participantAvatar).centerCrop().error(R.drawable.default_dp).placeholder(R.drawable.default_dp).into(secondUserImageView)
//                    imageLayout.visibility=View.VISIBLE
//                    secondImageLayout.visibility=View.VISIBLE
//                }else if(postDetail?.participant!!.size==3){
//                    Glide.with(this).load(postDetail?.participant!![0].participantAvatar).centerCrop().error(R.drawable.default_dp).placeholder(R.drawable.default_dp).into(userImageView)
//                    Glide.with(this).load(postDetail?.participant!![1].participantAvatar).centerCrop().error(R.drawable.default_dp).placeholder(R.drawable.default_dp).into(secondUserImageView)
//                    Glide.with(this).load(postDetail?.participant!![2].participantAvatar).centerCrop().error(R.drawable.default_dp).placeholder(R.drawable.default_dp).into(thirdUserImageView)
//                    imageLayout.visibility=View.VISIBLE
//                    secondImageLayout.visibility=View.VISIBLE
//                    thirdImageLayout.visibility=View.VISIBLE
//                }else if(postDetail?.participant!!.size==4){
//                    Glide.with(this).load(postDetail?.participant!![0].participantAvatar).centerCrop().error(R.drawable.default_dp).placeholder(R.drawable.default_dp).into(userImageView)
//                    Glide.with(this).load(postDetail?.participant!![1].participantAvatar).centerCrop().error(R.drawable.default_dp).placeholder(R.drawable.default_dp).into(secondUserImageView)
//                    Glide.with(this).load(postDetail?.participant!![2].participantAvatar).centerCrop().error(R.drawable.default_dp).placeholder(R.drawable.default_dp).into(thirdUserImageView)
//                    Glide.with(this).load(postDetail?.participant!![3].participantAvatar).centerCrop().error(R.drawable.default_dp).placeholder(R.drawable.default_dp).into(fourthUserImageView)
//                    imageLayout.visibility=View.VISIBLE
//                    secondImageLayout.visibility=View.VISIBLE
//                    thirdImageLayout.visibility=View.VISIBLE
//                    fourthImageLayout.visibility=View.VISIBLE
//                }
//            } else {
//                if (postDetail!!.bars != null) {
//                    Glide.with(this).load(postDetail!!.bars[0].photoUrl.toString()).centerCrop().error(R.drawable.default_dp).placeholder(R.drawable.default_dp).into(userImageView)
//                }
//                imageLayout.visibility = View.VISIBLE
//            }
//        } catch (e: ParseException) {
//            e.printStackTrace()
//        }
//        playButtonLayout.setOnClickListener {
//            setButtonPlayRecordingListener()
//        }
//    }
//    override fun onClick(v: View) {
//        when (v.id) {
//            R.id.singButton -> {
//                try {
//                    if (postDetail?.assignableBars!!.size > 0) {
//                        MyPreferences(applicationContext).saveIntegerInPrefrence(Constants.ASSIGN_BARS, postDetail?.assignableBars!!.get(0))
//                    }
//                    if (postDetail?.participant!!.size > 0) {
//                        MyPreferences(applicationContext).saveIntegerInPrefrence(Constants.PARTICIPANTS_SIZE, postDetail?.participant!!.size)
//                    }
//                    MyPreferences(applicationContext).saveIntegerInPrefrence(Constants.POST_ID, postDetail?.postId!!)
//                    MyPreferences(applicationContext).saveBooleanFlagInPrefrence(Constants.IS_INITIATOR, false)
//                    val intent = Intent(this@AudioBarsActivity, MainActivity::class.java)
//                    intent.putExtra("fragmentkey", "center")
//                    startActivity(intent)
//                } catch (ignored: Exception) {
//                }
//            }
//            R.id.restartButtonLayout -> {
//                restartButton()
//            }
//            R.id.backArrowRelativeLayout -> {
//                finish()
//            }
//        }
//    }
//    private fun downloadFile(url: String) {
//        FileLoader.with(this).load(url, false) //2nd parameter is optioal, pass true to force load from network
//            .fromDirectory("test4", FileLoader.DIR_INTERNAL).asFile(object : FileRequestListener<File> {
//                override fun onLoad(request: FileLoadRequest, response: FileResponse<File>) {
//                    val loadedFile = response.body
//                    filePath = loadedFile.path
//                    inflateWave(loadedFile)
//                    if (arrayList!!.size > 0) {
//                        playMusic(filePath)
//                    }
//                }
//                override fun onError(request: FileLoadRequest, t: Throwable) {
//                    dismissProgressDialog()
//                }
//            })
//    }
//
//    private fun inflateWave(file: File) {
//        val b = ByteArray(file.length().toInt())
//        try {
//            val fileInputStream = FileInputStream(file)
//            fileInputStream.read(b)
//            for (i in b.indices) {
//                print(b[i].toChar())
//            }
//        } catch (e: FileNotFoundException) {
//            println("File Not Found.")
//            e.printStackTrace()
//        } catch (e1: IOException) {
//            println("Error Reading The File.")
//            e1.printStackTrace()
//        }
//        if (arrayList?.size == 1) {
//            firstDurationTextView.text=Utils.getInstance().secondsToString((arrayList!![0]-1))
//            totalTimeDurationTextView.text=" / "+Utils.getInstance().secondsToString((arrayList!![0]-1))
//            drawBars(b, wave)
//        } else if (arrayList?.size == 2) {
//            firstDurationTextView.text=Utils.getInstance().secondsToString((arrayList!![0]-1))
//            secondDurationTextView.text=Utils.getInstance().secondsToString((arrayList!![1]-1))
//            totalTimeDurationTextView.text=" / "+Utils.getInstance().secondsToString((arrayList!![0]-1)+(arrayList!![1]-1))
//            drawBars(b, wave)
//            drawBars(b, waveSecond)
//        } else if (arrayList?.size == 3) {
//            firstDurationTextView.text=Utils.getInstance().secondsToString((arrayList!![0]-1))
//            secondDurationTextView.text=Utils.getInstance().secondsToString((arrayList!![1]-1))
//            thirdDurationTextView.text=Utils.getInstance().secondsToString((arrayList!![2]-1))
//            totalTimeDurationTextView.text=" / "+Utils.getInstance().secondsToString((arrayList!![0]-1)+(arrayList!![1]-1)+(arrayList!![2]-1))
//            drawBars(b, wave)
//            drawBars(b, waveSecond)
//            drawBars(b, waveThird)
//        } else if (arrayList?.size == 4) {
//            firstDurationTextView.text=Utils.getInstance().secondsToString((arrayList!![0]-1))
//            secondDurationTextView.text=Utils.getInstance().secondsToString((arrayList!![1]-1))
//            thirdDurationTextView.text=Utils.getInstance().secondsToString((arrayList!![2]-1))
//            fourthDurationTextView.text=Utils.getInstance().secondsToString((arrayList!![3]-1))
//            totalTimeDurationTextView.text=" / "+Utils.getInstance().secondsToString((arrayList!![0]-1)+(arrayList!![1]-1)+(arrayList!![2]-1)+(arrayList!![3]-1))
//            drawBars(b, wave)
//            drawBars(b, waveSecond)
//            drawBars(b, waveThird)
//            drawBars(b, waveFourth)
//        } else if (duration != 0) {
//            drawBars(b, wave)
//            firstDurationTextView.text=Utils.getInstance().secondsToString((duration/1000))
//            totalTimeDurationTextView.text=" / "+Utils.getInstance().secondsToString((duration/1000))
//        }
//    }
//
//    private fun setProgress(durationPass: Int, waveView: AudioWaveView) {
//        val progressAnim: ObjectAnimator by lazy {
//            ObjectAnimator.ofFloat(waveView, "progress", 0F, 100F).apply {
//                interpolator = LinearInterpolator()
//                duration = 1000 * durationPass.toLong()
//            }
//        }
//        progressAnimator = progressAnim
//        progressAnim.start()
//        waveView.onProgressChanged = { progress, byUser ->
//            try {
//                if (progress == 100F) {
//                    waveView.waveColor = ContextCompat.getColor(this, R.color.white)
//                    waveView.isTouchable = false
//                    i += 1
//                    if (i < arrayList!!.size) {
//                        if (i == 1)
//                            setProgress(arrayList!![i], waveSecond)
//                        else if (i == 2)
//                            setProgress(arrayList!![i], waveThird)
//                        else if (i == 3)
//                            setProgress(arrayList!![i], waveFourth)
//                    } else {
//                        chronometer.stop()
//                        playImageView.setBackgroundResource(R.drawable.play)
//                    }
//                }
//            } catch (ignored: Exception) {
//                ignored.printStackTrace()
//            }
//        }
//        waveView.onStartTracking = {
//        }
//        waveView.onStopTracking = {
//        }
//    }
//    private fun playMusic(filePath: String) {
//        if (mediaPlayer == null) {
//            mediaPlayer = MediaPlayer()
//        }
//        mediaPlayer!!.setDataSource(filePath)
//        mediaPlayer!!.prepare()
//        if (arrayList!!.size == 0) {
//            duration = mediaPlayer!!.duration
//            downloadFile(postDetail!!.audioUrl)
//        }
//        mediaPlayer!!.setOnCompletionListener {
//        }
//    }
//
//
//    private fun drawBars(b: ByteArray, waveView: AudioWaveView) {
//        waveView.visibility=View.VISIBLE
//        waveView.setRawData(b) {
//            dismissProgressDialog()
//        }
//        val progressAnim: ObjectAnimator by lazy {
//            ObjectAnimator.ofFloat(waveView, "progress", 0F, 100F).apply {
//                interpolator = LinearInterpolator()
//                if (arrayList!!.size == 0) {
//                    duration = 1000 * mediaPlayer?.duration!! / 1000.toLong()
//                } else {
//                    duration = 1000 * arrayList!![i].toLong()
//                }
//            }
//        }
//        progressAnimator = progressAnim
//    }
//
//    private fun setButtonPlayRecordingListener() {
//        try {
//            if (playRecordFlag) {
//                playRecordFlag = false
//                timeWhenStopped = chronometer.base - SystemClock.elapsedRealtime()
//                chronometer.stop()
//                mediaPlayer?.pause()
//                mediaPlayerLength = mediaPlayer?.currentPosition!!
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    progressAnimator!!.pause()
//                }
//                playImageView.setBackgroundResource(R.drawable.play)
//            } else {
//                playRecordFlag = true
//                playImageView.setBackgroundResource(R.drawable.pause)
//                chronometer.base = SystemClock.elapsedRealtime() + timeWhenStopped
//                chronometer.start()
//                if (mediaPlayerLength != 0) {
//                    mediaPlayer?.seekTo(mediaPlayerLength)
//                }
//                mediaPlayer?.start()
//                if (progressFlag) {
//                    if (arrayList!!.size > 0) {
//                        setProgress(arrayList!!.get(i), wave)
//                        progressFlag = false
//                        restartButtonLayout.isEnabled = true
//                    } else {
//                        setProgress(mediaPlayer!!.duration / 1000, wave)
//                        progressFlag = false
//                        restartButtonLayout.isEnabled = true
//                    }
//                } else {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                        progressAnimator!!.resume()
//                    }
//                }
//            }
//        } catch (ignored: Exception) {
//        }
//    }
//    private fun restartButton() {
//        try {
//            chronometer.base = SystemClock.elapsedRealtime()
//            chronometer.start()
//            progressAnimator!!.cancel()
//            progressAnimator!!.removeAllListeners()
//            wave.progress = 0F
//            waveSecond.progress = 0F
//            waveThird.progress = 0F
//            waveFourth.progress = 0F
//            i = 0
//            playRecordFlag = true
//            mediaPlayer!!.pause()
//            mediaPlayer!!.seekTo(0)
//            mediaPlayer!!.start()
//            if (arrayList!!.size > 0) {
//                setProgress(arrayList!!.get(i), wave)
//            } else {
//                setProgress(duration / 1000, wave)
//            }
//        } catch (e: Exception) {
//        }
//    }
//
//    override fun onStop() {
//        super.onStop()
//        stopMediaPlayer()
//    }
//
//    private fun stopMediaPlayer() {
//        try {
//            if (mediaPlayer != null) {
//                try {
//                    mediaPlayer?.stop()
//                    mediaPlayer?.release()
//                    mediaPlayer = null
//                } catch (ignored: Exception) {
//                }
//            }
//        } catch (ignored: Exception) {
//        }
//    }
    }
}