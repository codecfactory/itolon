package com.example.myapplication.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.allartistmodel.AllArtistModel
import com.example.myapplication.model.userdownloadsmodel.UserDownloadModel
import com.example.myapplication.network.ApiResponse
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.network.RetrofitServices

class UserDownloadsViewModel : ViewModel() {
    var userDownloadLiveData: LiveData<ApiResponse<UserDownloadModel>>? =null
    private val retrofitClient= RetrofitClient().getRetrofit().create(RetrofitServices::class.java)
    fun getUserDownloads(hashMap:HashMap<String,String>) {
        userDownloadLiveData=retrofitClient.getUserDownloads(hashMap)
    }
}