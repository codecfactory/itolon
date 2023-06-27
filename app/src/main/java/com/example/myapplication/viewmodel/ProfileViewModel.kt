package com.example.myapplication.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.defaultmodel.DefaultModel
import com.example.myapplication.model.loginmodel.Login
import com.example.myapplication.model.profilemodel.Profile
import com.example.myapplication.network.ApiResponse
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.network.RetrofitServices

class ProfileViewModel : ViewModel() {
    val retrofitClient= RetrofitClient().getRetrofit().create(RetrofitServices::class.java)
    var logoutLiveData: LiveData<ApiResponse<DefaultModel>>? =null
    var profileLiveData: LiveData<ApiResponse<Profile>>? =null
     fun getUserProfile(hashMap:HashMap<String,String>) {
         profileLiveData=retrofitClient.getUserProfile(hashMap)
    }
    fun logoutUser(hashMap:HashMap<String,String>) {
        logoutLiveData=retrofitClient.logoutUser(hashMap)
    }
}