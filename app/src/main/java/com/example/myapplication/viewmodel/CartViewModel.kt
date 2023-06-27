package com.example.myapplication.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.cartapimodel.CartApiModel
import com.example.myapplication.network.ApiResponse
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.network.RetrofitServices

class CartViewModel : ViewModel() {
    val cartService= RetrofitClient().getRetrofit().create(RetrofitServices::class.java)
    var cartLiveData: LiveData<ApiResponse<CartApiModel>>? =null
     fun addToCart(hashMap:HashMap<String,String>) {
         cartLiveData=cartService.addToCart(hashMap)
    }
}