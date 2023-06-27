package com.example.myapplication.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.categorymodel.CategoryModel
import com.example.myapplication.model.deleteartistsongmodel.DeleteSong
import com.example.myapplication.model.postmodel.Post
import com.example.myapplication.model.postsmodel.Posts
import com.example.myapplication.model.screenimagesmodel.ScreenImages
import com.example.myapplication.network.ApiResponse
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.network.RetrofitServices

class ContentViewModel : ViewModel() {
    var postsLiveData: LiveData<ApiResponse<Post>>? =null
    var deletePostLiveData: LiveData<ApiResponse<DeleteSong>>? =null
    var categoriesLiveData: LiveData<ApiResponse<CategoryModel>>? =null
    var screenImagesLiveData: LiveData<ApiResponse<ScreenImages>>? =null
    val retrofitClient= RetrofitClient().getRetrofit().create(RetrofitServices::class.java)
    fun getFeeds(hashMap:HashMap<String,String>) {
        postsLiveData=retrofitClient.getHomePosts(hashMap)
    }
    fun deleteContent(hashMap:HashMap<String,String>) {
        deletePostLiveData=retrofitClient.deleteContent(hashMap)
    }
    fun getCategories(hashMap:HashMap<String,String>) {
        categoriesLiveData=retrofitClient.getArtistSongCategories(hashMap)
    }
}