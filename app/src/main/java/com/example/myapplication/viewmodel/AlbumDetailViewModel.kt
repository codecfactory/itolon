package com.example.myapplication.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.albumdetailmodel.AlbumDetailModel
import com.example.myapplication.model.albummodel.Album
import com.example.myapplication.model.postmodel.Post
import com.example.myapplication.model.postsmodel.Posts
import com.example.myapplication.network.ApiResponse
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.network.RetrofitServices

class AlbumDetailViewModel : ViewModel() {
    var albumDetailLiveData: LiveData<ApiResponse<AlbumDetailModel>>? =null
    val retrofitClient= RetrofitClient().getRetrofit().create(RetrofitServices::class.java)
    fun getAlbumDetails(hashMap:HashMap<String,String>) {
        albumDetailLiveData=retrofitClient.getAlbumDetails(hashMap)
    }
}