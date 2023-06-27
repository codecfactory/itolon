package com.example.myapplication.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.defaultmodel.DefaultModel
import com.example.myapplication.model.postsmodel.Posts
import com.example.myapplication.network.ApiResponse
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.network.RetrofitServices

class AdvertismentViewModel : ViewModel() {
    var adsLiveData: LiveData<ApiResponse<DefaultModel>>? =null
    val retrofitClient= RetrofitClient().getRetrofit().create(RetrofitServices::class.java)
    fun getAds() {
        adsLiveData=retrofitClient.getAds()
    }
}