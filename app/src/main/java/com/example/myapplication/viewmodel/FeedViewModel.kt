package com.example.myapplication.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.postmodel.Post
import com.example.myapplication.model.postsmodel.Posts
import com.example.myapplication.model.screenimagesmodel.ScreenImages
import com.example.myapplication.network.ApiResponse
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.network.RetrofitServices

class FeedViewModel : ViewModel() {
    var postsLiveData: LiveData<ApiResponse<Post>>? =null
    var screenImagesLiveData: LiveData<ApiResponse<ScreenImages>>? =null
    val retrofitClient= RetrofitClient().getRetrofit().create(RetrofitServices::class.java)
    fun getFeeds(hashMap:HashMap<String,String>) {
        postsLiveData=retrofitClient.getHomePosts(hashMap)
    }
    fun getScreenImages() {
        screenImagesLiveData=retrofitClient.getScreenImages()
    }
}