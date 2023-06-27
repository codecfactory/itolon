package com.example.myapplication.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.signupmodell.Signup
import com.example.myapplication.network.ApiResponse
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.network.RetrofitServices

class SignupViewModel : ViewModel() {
    var signupLiveData: LiveData<ApiResponse<Signup>>? =null
    val retrofitClient= RetrofitClient().getRetrofit().create(RetrofitServices::class.java)
    public fun registerUser(hashMap:HashMap<String,String>) {
    signupLiveData=retrofitClient.signupUser(hashMap)
    }
    public fun verifyUser(hashMap:HashMap<String,String>) {
    signupLiveData=retrofitClient.verifyUser(hashMap)
    }
}