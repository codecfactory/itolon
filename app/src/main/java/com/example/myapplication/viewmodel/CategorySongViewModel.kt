package com.example.myapplication.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.addtoplaylistmodel.AddToPlaylistModel
import com.example.myapplication.model.albummodel.Album
import com.example.myapplication.model.categoriessongmodel.CategoriesSongModel
import com.example.myapplication.model.playlistmodel.PlaylistModel
import com.example.myapplication.model.postmodel.Post
import com.example.myapplication.model.postsmodel.Posts
import com.example.myapplication.network.ApiResponse
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.network.RetrofitServices

class CategorySongViewModel : ViewModel() {
    var songsLiveData: LiveData<ApiResponse<CategoriesSongModel>>? =null
    var addToPlaylistLiveData: LiveData<ApiResponse<AddToPlaylistModel>>? =null
    val retrofitClient= RetrofitClient().getRetrofit().create(RetrofitServices::class.java)
    fun getCategoriesSongs(hashMap:HashMap<String,String>) {
        songsLiveData=retrofitClient.getCategoriesSongs(hashMap)
    }
    fun addToPlaylist(hashMap:HashMap<String,String>) {
        addToPlaylistLiveData=retrofitClient.addToPlaylist(hashMap)
    }
}