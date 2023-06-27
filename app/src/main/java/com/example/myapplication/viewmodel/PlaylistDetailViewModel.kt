package com.example.myapplication.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.albummodel.Album
import com.example.myapplication.model.playlistdetailmodel.PlaylistDetail
import com.example.myapplication.model.playlistmodel.PlaylistModel
import com.example.myapplication.model.postmodel.Post
import com.example.myapplication.model.postsmodel.Posts
import com.example.myapplication.network.ApiResponse
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.network.RetrofitServices

class PlaylistDetailViewModel : ViewModel() {
    var playlistDetailLiveData: LiveData<ApiResponse<PlaylistDetail>>? =null
    val retrofitClient= RetrofitClient().getRetrofit().create(RetrofitServices::class.java)
    fun getPlaylistDetail(hashMap:HashMap<String,String>) {
        playlistDetailLiveData=retrofitClient.getPlaylistDetail(hashMap)
    }
}