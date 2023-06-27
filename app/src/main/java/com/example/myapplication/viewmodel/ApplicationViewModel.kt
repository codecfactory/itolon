package com.example.myapplication.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.application.Application
import com.example.myapplication.model.loginmodel.Login
import com.example.myapplication.network.ApiResponse
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.network.RetrofitServices

class ApplicationViewModel : ViewModel() {
    val retrofitClient= RetrofitClient().getRetrofit().create(RetrofitServices::class.java)
    var applicationLiveData: LiveData<ApiResponse<Application>>? =null
     fun submitApplication(hashMap:HashMap<String,String>) {
         applicationLiveData=retrofitClient.submitApplication(hashMap)
    }
}