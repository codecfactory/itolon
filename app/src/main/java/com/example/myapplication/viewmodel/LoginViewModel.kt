package com.example.myapplication.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.loginmodel.Login
import com.example.myapplication.network.ApiResponse
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.network.RetrofitServices

class LoginViewModel : ViewModel() {
    val userLoginService= RetrofitClient().getRetrofit().create(RetrofitServices::class.java)
    var loginLiveData: LiveData<ApiResponse<Login>>? =null
     fun loginUser(hashMap:HashMap<String,String>) {
        loginLiveData=userLoginService.loginUser(hashMap)
    }
}