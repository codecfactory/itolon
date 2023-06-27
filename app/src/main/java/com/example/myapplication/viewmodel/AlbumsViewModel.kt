package com.example.myapplication.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.albummodel.Album
import com.example.myapplication.model.postmodel.Post
import com.example.myapplication.model.postsmodel.Posts
import com.example.myapplication.network.ApiResponse
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.network.RetrofitServices

class AlbumsViewModel : ViewModel() {
    var albumsLiveData: LiveData<ApiResponse<Album>>? =null
    val retrofitClient= RetrofitClient().getRetrofit().create(RetrofitServices::class.java)
    fun getAlbums(hashMap:HashMap<String,String>) {
        albumsLiveData=retrofitClient.getAlbums(hashMap)
    }
}