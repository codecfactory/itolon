package com.example.myapplication.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.allartistmodel.AllArtistModel
import com.example.myapplication.network.ApiResponse
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.network.RetrofitServices

class AllArtistsViewModel : ViewModel() {
    var artistLiveData: LiveData<ApiResponse<AllArtistModel>>? =null
    private val retrofitClient= RetrofitClient().getRetrofit().create(RetrofitServices::class.java)
    fun getAllArtists(hashMap:HashMap<String,String>) {
        artistLiveData=retrofitClient.getAllArtists(hashMap)
    }
}