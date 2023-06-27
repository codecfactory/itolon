package com.example.myapplication.activities
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.model.application.Application
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.viewmodel.ApplicationViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_application.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class ApplicationActivity : com.example.myapplication.activities.BaseActivity() {
    private lateinit var applicationViewModel: ApplicationViewModel
    var mediaContentList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applicationViewModel = ViewModelProvider.NewInstanceFactory().create(ApplicationViewModel::class.java)
        setContentView(R.layout.activity_application)
        mediaContentList.clear()
        submit_button.setOnClickListener {
            when {
                username_editText.text.toString().isNullOrEmpty() -> {
                    showDialog("Please Enter Name")
                }
                enterprise_name_editText.text.toString().isNullOrEmpty() -> {
                    showDialog("Please Enter Enterprise Name")
                }
                region_edittext.text.toString().isNullOrEmpty() -> {
                    showDialog("Please Enter Region")
                }
                address_editText.text.toString().isNullOrEmpty() -> {
                    showDialog("Please Enter Address")
                }
                phone_edittext.text.toString().isNullOrEmpty() -> {
                    showDialog("Please Enter Tel Numero")
                }
                email_edittext.text.toString().isNullOrEmpty() -> {
                    showDialog("Please Enter Email")
                }
                else -> {
                    getApiData()
                }
            }
        }
    }
    private fun getApiData(){
        if (Utils.getInstance().isNetworkConnected(this@ApplicationActivity)) {
            val hashMap = HashMap<String, String>()
            hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN, "")
            hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN, "")
            hashMap["name"] = username_editText.text.toString()
            hashMap["second_name"] = enterprise_name_editText.text.toString()
            hashMap["country"] = region_edittext.text.toString()
            hashMap["address"] = address_editText.text.toString()
            hashMap["phone"] = phone_edittext.text.toString()
            hashMap["email"] = email_edittext.text.toString()
            hashMap["agree_tc"] = "1"
            hashMap["agree_tc"] = "1"
            hashMap["website"] = "https://" + website_edittext.text.toString()
            applicationViewModel.submitApplication(hashMap)
            getResponse()
            progressBar.show(this)
        }
        else{
            showErrorDialog("No Internet Connection")
        }
    }

    private fun showErrorDialog(message:String){
        ViewUtils.showSnackBar(this@ApplicationActivity,message,false,"Retry").subscribe{ res->
            if(res){
                getApiData()
            }
        }
    }
    private fun getResponse() {
        applicationViewModel.applicationLiveData!!.observe(this, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), Application::class.java)
                if (response.meta.code == 201) {
                    startActivity(Intent(this@ApplicationActivity,MainActivity::class.java))
                }
                else{
                    showErrorDialog(response.meta.message)
                }
            }
            else{
                if(jsonResponse.get("errorCode")==500) {
                    showErrorDialog("Server is not responding")
                }else{
                    showErrorDialog(jsonResponse.get("errorMessage").toString())
                }
            }
        })
    }
}