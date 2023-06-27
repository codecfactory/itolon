package com.example.myapplication.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.addcommentsmodel.AddCommentModel
import com.example.myapplication.model.defaultmodel.DefaultModel
import com.example.myapplication.model.postsmodel.Posts
import com.example.myapplication.network.ApiResponse
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.network.RetrofitServices

class PostsViewModel : ViewModel() {
    var postsLiveData: LiveData<ApiResponse<Posts>>? =null
    var commentsLiveData: LiveData<ApiResponse<AddCommentModel>>? =null
    val retrofitClient= RetrofitClient().getRetrofit().create(RetrofitServices::class.java)
    fun getPosts(hashMap:HashMap<String,String>) {
        postsLiveData=retrofitClient.getPosts(hashMap)
    }
    fun addComments(hashMap:HashMap<String,String>) {
        commentsLiveData=retrofitClient.addComments(hashMap)
    }
}