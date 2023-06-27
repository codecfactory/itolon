package com.example.myapplication.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.defaultmodel.DefaultModel
import com.example.myapplication.model.userdownloadsmodel.UserDownloadModel
import com.example.myapplication.network.ApiResponse
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.network.RetrofitServices

class SongDownloadViewModel : ViewModel() {
    var songDownloadLiveData: LiveData<ApiResponse<DefaultModel>>? =null
    private val retrofitClient= RetrofitClient().getRetrofit().create(RetrofitServices::class.java)
    fun downloadSong(hashMap:HashMap<String,String>) {
        songDownloadLiveData=retrofitClient.addDownload(hashMap)
    }
}