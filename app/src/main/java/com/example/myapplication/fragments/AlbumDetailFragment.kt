package com.example.myapplication.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.R
import com.example.myapplication.activities.TeaserActivity
import com.example.myapplication.activities.VideoPlayActivity
import com.example.myapplication.adapters.CategoriesSongAdapter
import com.example.myapplication.adapters.CategorySongClickListener
import com.example.myapplication.model.categoriessongmodel.Song
import com.example.myapplication.prefrences.Constants
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.ViewUtils.showSnackBar
import com.example.myapplication.view.CustomProgressBar
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import kotlinx.android.synthetic.main.fragment_album_detail.view.*
import java.io.File

class AlbumDetailFragment(val songsList:ArrayList<Song>,val artistId:String) : Fragment(), CategorySongClickListener {
    var isScrolling: Boolean? = false
    var currentItems: Int = 0
    val progressBar = CustomProgressBar()
    var totalItems: Int = 0
    var scrollOutItems: Int = 0
    var manager: GridLayoutManager? = null
    var count: Int = 6
    var songItems: MutableList<Song> = mutableListOf()
    var songFinalList: MutableList<Song> = mutableListOf()
    var mView:View?=null
    var clickPosition=-1
    var songPosition=-1
    var playSongPosition=-1
    var playFilePath=""
    var existFlag=false
    val songsArrayList=ArrayList<String>()
    val videoArrayList=ArrayList<String>()
    lateinit var dialog: Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_album_detail, container, false)
        mView=view
        init(view)
        return view
    }

    private fun init(view: View) {
        view.cp_cardview.setCardBackgroundColor(Color.parseColor("#70000000")) //Box Color
        if (artistId != null && !artistId.isNullOrEmpty()) {
            for (i in 0 until songsList.size) {
                if (songsList[i].artistId.toString().equals(artistId)) {
                    if(songsList[i].content!=null) {
                        songItems.add(songsList[i])
                        existFlag = true
                    }
                }
            }
            for(count in 0 until songItems.size){
                if (songItems[count].content != null && songItems[count].content.filePath != null) {
                    if(songItems[count].content!=null) {
                        songFinalList.add(songItems[count])
                    }
                }
            }
            for(i in 0 until songFinalList.size) {
                if (songFinalList[i].content != null && songFinalList[i].content.filePath != null) {
                    if(songFinalList[i].content!=null) {
                        downloadFile("http://44.231.47.188" + songFinalList[i].content.filePath)
                    }
                }
            }
            setAdapter(songFinalList)
        } else {
            existFlag = true
            for(songCount in 0 until songsList.size){
                if(songsList[songCount].content!=null&&songsList[songCount].content.filePath!=null) {
                    songFinalList.add(songsList[songCount])
                }
            }
            for(i in 0 until songFinalList.size){
                if(songFinalList[i].content!=null&&songFinalList[i].content.filePath!=null) {
//                    if(songFinalList[i].content!=null) {
                        downloadFile("http://44.231.47.188" + songFinalList[i].content.filePath)
//                    }
                }
            }
            setAdapter(songFinalList)
        }
        view.swipeToRefresh?.setOnRefreshListener {
            view.swipeToRefresh.isRefreshing = false
        }
        try {
            if (!existFlag) {
                setVisibilty(false)
            }
            if (songsList.size > 0 && existFlag) {
                setVisibilty(true)
            } else {
                setVisibilty(false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setAdapter(itemsArray:MutableList<Song>){
        manager = GridLayoutManager(requireContext(),3)
        mView!!.feedRecyclerview?.adapter = CategoriesSongAdapter(itemsArray, (requireActivity()),this, this)
        mView!!.feedRecyclerview?.layoutManager = manager
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onClick(position: Int, songUrl: String,song:Song) {
        clickPosition=position
        songPosition=position
//        downloadFile(songUrl)
        if(songUrl.contains(".mp3")||songUrl.contains(".m4a"))
        {
            Constants.songsArrayList.clear()
            Constants.songsArrayList.addAll(songsArrayList)
            playSongPosition=computePosition(songUrl)
            if(playSongPosition!=-1) {
                if(!Constants.isSearch){
                if(songItems!=null&&songItems.size>0) {
                    try {
                        Constants.albumCategoryListItems.clear()
                        Constants.albumCategoryListItems.addAll(songItems)
                        for (i in 0 until songItems.size) {
                         if (songItems[i].content != null) {
                                if (songItems[i].content.filePath != null && songItems[i].content.filePath.contains(".mp4")) {
                                    Constants.albumCategoryListItems.removeAt(i)
                                }
                            }
                        }
//                        for(count in 0 until Constants.albumCategoryListItems.size){
//                            if(Constants.albumCategoryListItems[count].content.filePath.contains(songUrl.substring(songUrl.lastIndexOf("/")))){
//                                songPosition=count
//                            }
//                        }
                    } catch (e: Exception) {
                    }
                    startActivity(Intent(requireContext(), TeaserActivity::class.java).putExtra("position", clickPosition).putExtra("id", songItems[clickPosition].id).putExtra("albumDetailTrackData", songItems[clickPosition]).putExtra("from","fromAlbumCategories"))
                }else{
                    try {
                        Constants.albumCategoryListItems.clear()
                        Constants.albumCategoryListItems.addAll(songItems)
                        for(i in 0 until songItems.size){
                                if (songItems[i].content != null) {
                                if (songItems[i].content.filePath != null &&songItems[i].content.filePath.contains(".mp4")){
                                Constants.albumCategoryListItems.removeAt(i)
                                }
                            }
                        }

//                        for(count in 0 until Constants.albumCategoryListItems.size){
//                            if(Constants.albumCategoryListItems[count].content.filePath.contains(songUrl.substring(songUrl.lastIndexOf("/")))){
//                                songPosition=count
//                            }
//                        }
                    } catch (e: Exception) {
                    }
                    startActivity(Intent(requireContext(), TeaserActivity::class.java).putExtra("position", clickPosition).putExtra("id", songItems[clickPosition].id).putExtra("albumSongTrackData", songItems[clickPosition]).putExtra("from","fromAlbumCategories"))
                }
                    playSongPosition = -1
              }else{
                    if(songsList!=null&&songsList.size>0) {
                        try {
                            Constants.albumCategoryListItems.clear()
                            Constants.albumCategoryListItems.addAll(songsList)
                            for (i in 0 until songsList.size) {
                                if (songsList[i].content != null) {
                                    if (songsList[i].content.filePath != null && songsList[i].content.filePath.contains(".mp4")) {
                                        Constants.albumCategoryListItems.removeAt(i)
                                    }
                                }
                            }
                            for(count in 0 until Constants.albumCategoryListItems.size){
                                if(Constants.albumCategoryListItems[count].content.filePath.contains(songUrl.substring(songUrl.lastIndexOf("/")))){
                                    songPosition=count
                                }
                            }
                        } catch (e: Exception) {
                        }
                        startActivity(Intent(requireContext(), TeaserActivity::class.java).putExtra("songPosition", songPosition).putExtra("position", clickPosition).putExtra("id", songsList[clickPosition].id).putExtra("albumDetailTrackData", songsList[clickPosition]).putExtra("from","fromAlbumCategories"))
                    }else{
                        try {
                            Constants.albumCategoryListItems.clear()
                            Constants.albumCategoryListItems.addAll(songItems)
                            for(i in 0 until songItems.size){
                                if (songItems[i].content != null) {
                                    if (songItems[i].content.filePath != null &&songItems[i].content.filePath.contains(".mp4")){
                                        Constants.albumCategoryListItems.removeAt(i)
                                    }
                                }
                            }
                            for(count in 0 until Constants.albumCategoryListItems.size){
                                if(Constants.albumCategoryListItems[count].content.filePath.contains(songUrl.substring(songUrl.lastIndexOf("/")))){
                                    songPosition=count
                                }
                            }
                        } catch (e: Exception) {
                        }
                        startActivity(Intent(requireContext(), TeaserActivity::class.java).putExtra("songPosition", songPosition).putExtra("position", clickPosition).putExtra("id", songItems[clickPosition].id).putExtra("albumSongTrackData", songItems[clickPosition]).putExtra("from","fromAlbumCategories"))
                    }
                }
            }
                else{
                showSnackBar(requireContext(),"File is not exist",true,"")
            }
        }else{
            Constants.songsArrayList.clear()
            Constants.songsArrayList.addAll(videoArrayList)
            playSongPosition = computePosition(songUrl)
            if (playSongPosition != -1) {
                if (songsList != null && songsList.size > 0) {
                    startActivity(Intent(requireActivity(), VideoPlayActivity::class.java).putExtra("id", songsList[clickPosition].id).putExtra("filePath", songsList[clickPosition].content.filePath).putExtra("songListCategoryItemDownloaded", songsList[clickPosition]))
                } else {
                    startActivity(Intent(requireActivity(), VideoPlayActivity::class.java).putExtra("id", songItems[clickPosition].id).putExtra("filePath", songItems[clickPosition].content.filePath).putExtra("songListCategoryItemDownloaded", songItems[clickPosition]))
                }
            } else {
                showSnackBar(requireContext(), "File is not exist", true, "")
            }
        }
    }
    private fun computePosition(songUrl: String):Int{
        val fileName= Utils.getInstance().getLastString(songUrl)
        for(i in 0 until Constants.songsArrayList.size){
            if(Constants.songsArrayList.get(i).contains(fileName)){
                playSongPosition=i
                playFilePath= Constants.songsArrayList.get(i)
            }
        }
        return playSongPosition
    }
    private fun setVisibilty(flag:Boolean){
        if(flag){
            mView!!.no_result_textview.visibility=View.GONE
            mView!!.swipeToRefresh.visibility=View.VISIBLE
        }else{
            mView!!. no_result_textview.visibility=View.VISIBLE
            mView!!.swipeToRefresh.visibility=View.GONE
        }
    }
    private fun downloadFile(url: String) {
            mView!!.cp_pbar.visibility = View.VISIBLE
            mView!!.cp_cardview.visibility = View.VISIBLE
//        }
        FileLoader.with(requireActivity()).load(url, false) //2nd parameter is optioal, pass true to force load from network
            .fromDirectory(".Itolon", FileLoader.DIR_EXTERNAL_PUBLIC).asFile(object : FileRequestListener<File> {
                override fun onLoad(request: FileLoadRequest, response: FileResponse<File>) {
                    val loadedFile = response.body
                    if(loadedFile.path.contains(".mp3")||loadedFile.path.contains(".m4a")) {
                        songsArrayList.add(loadedFile.path)
                    }else{
                        videoArrayList.add(loadedFile.path)
                    }
                    clickPosition=-1
                    mView!!.cp_pbar.visibility=View.GONE
                    mView!!.cp_cardview.visibility=View.GONE
                }
                override fun onError(request: FileLoadRequest, t: Throwable) {
                    mView!!.cp_pbar.visibility=View.GONE
                    mView!!.cp_cardview.visibility=View.GONE
                }
            })
    }


}