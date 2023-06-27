package com.example.myapplication.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.allartistmodel.AllArtistModel
import com.example.myapplication.model.artistdetailmodel.ArtistDetail
import com.example.myapplication.network.ApiResponse
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.network.RetrofitServices

class ArtistDetailsViewModel : ViewModel() {
    var artistDetailsLiveData: LiveData<ApiResponse<ArtistDetail>>? =null
    private val retrofitClient= RetrofitClient().getRetrofit().create(RetrofitServices::class.java)
    fun getArtistDetails(hashMap:HashMap<String,String>) {
        artistDetailsLiveData=retrofitClient.getArtistDetails(hashMap)
    }
}