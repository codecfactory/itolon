package com.example.myapplication.prefrences

import android.media.MediaPlayer
import com.example.myapplication.model.CartModel
import com.example.myapplication.model.categoriessongmodel.Song
import com.example.myapplication.model.userdownloadsmodel.UserDownloadResult

object Constants {

    var cartArrayList=ArrayList<CartModel>()
    var songsArrayList=ArrayList<String>()
    var playlistItems: MutableList<com.example.myapplication.model.playlistdetailmodel.Song> = mutableListOf()
    var downloadListItems: MutableList<UserDownloadResult> = mutableListOf()
    var albumCategoryListItems: MutableList<Song> = mutableListOf()
    var serviceSongsArrayList=ArrayList<String>()
    var categorySongsArrayList=ArrayList<Song>()
    var mediaPlayer:MediaPlayer ? = null
    var downloadResponse="download_response"
    var position=-1
    var repeatCount=-1
    var isSearch=false
    var isShuffle=false
}