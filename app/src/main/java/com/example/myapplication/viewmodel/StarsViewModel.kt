package com.example.myapplication.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.albummodel.Album
import com.example.myapplication.model.postmodel.Post
import com.example.myapplication.model.postsmodel.Posts
import com.example.myapplication.model.starsmodel.StarsModel
import com.example.myapplication.network.ApiResponse
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.network.RetrofitServices

class StarsViewModel : ViewModel() {
    var starsLiveData: LiveData<ApiResponse<StarsModel>>? =null
    val retrofitClient= RetrofitClient().getRetrofit().create(RetrofitServices::class.java)
    fun getStars(hashMap:HashMap<String,String>) {
        starsLiveData=retrofitClient.getStarArtists(hashMap)
    }
}