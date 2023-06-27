package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.model.signupmodell.Signup
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.viewmodel.SignupViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONObject

class SignupActivity : BaseActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var signupViewModel: SignupViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signupViewModel = ViewModelProvider.NewInstanceFactory().create(SignupViewModel::class.java)
        setContentView(R.layout.activity_register)
        init()
    }
    private fun init(){
        login_textview.setOnClickListener {
            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
            finish()
        }
        signup_button.setOnClickListener {
            Toast.makeText(this@SignupActivity, "Signup", Toast.LENGTH_LONG).show()
            when {
                username_editText.text.toString().isNullOrEmpty() -> {
                    ViewUtils.showSnackBar(this@SignupActivity,"Please Enter Name",true,"Retry")
                }
                mobile_editText.text.toString().isNullOrEmpty() -> {
                    ViewUtils.showSnackBar(this@SignupActivity,"Please Enter Number",true,"Retry")
                }
                password_editText.text.toString().isNullOrEmpty() -> {
                    ViewUtils.showSnackBar(this@SignupActivity,"Please Enter Password",true,"Retry")
                }
//                userType.isNullOrEmpty() -> {
//                    showDialog("Please Select User Type")
//                }
                else -> {
                    getApiData()
                }
            }
        }
//        categories = ArrayList()
//        spinner.prompt="Select Option"
//        categories!!.add("Select")
//        categories!!.add("User")
//        categories!!.add("Artist")
////        categories!!.add("Executive")
//        val customAdapter = CustomSpinnerAdapter(applicationContext, categories )
//        spinner.setAdapter(customAdapter)
//        spinner.onItemSelectedListener = this
    }
    private fun getResponse() {
        signupViewModel.signupLiveData!!.observe(this, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), Signup::class.java)
                if (response.meta.code == 211) {
                    SharedPref.write(SharedPref.AUTH_TOKEN, response.result.authCode.toString())
                    startActivity(Intent(this@SignupActivity, VerifyUserActivity::class.java))
                }else{
                    showErrorDialog(response.meta.message)
                }
            }else{
                if(jsonResponse.get("errorCode")==500) {
                    showErrorDialog("Server is not responding")
                }else{
                    showErrorDialog(jsonResponse.get("errorMessage").toString())
                }
            }
        })
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(arg0: AdapterView<*>?, arg1: View?, position: Int, id: Long) {
//        if(position!=0){
//            userType= categories!![position]
//            Toast.makeText(applicationContext, categories!![position].toString(), Toast.LENGTH_LONG).show()
//        }
    }

    private fun getApiData(){
        if (Utils.getInstance().isNetworkConnected(this@SignupActivity)) {
            val hashMap = HashMap<String, String>()
            hashMap["name"] = username_editText.text.toString()
            hashMap["user_type"] = "user"
            hashMap["password"] = password_editText.text.toString()
            hashMap["phone"] = mobile_editText.text.toString()
            hashMap["email"] = email_editText.text.toString()
            hashMap["device_token"] =SharedPref.read(SharedPref.REFRESH_TOKEN,"")
            hashMap["device_type"] ="android"
            signupViewModel.registerUser(hashMap)
            getResponse()
            progressBar.show(this@SignupActivity)
        }
        else{
            showErrorDialog("No Internet Connection")
        }
    }
    private fun showErrorDialog(message:String){
        ViewUtils.showSnackBar(this@SignupActivity,message,false,"Retry").subscribe{ res->
            if(res){
                getApiData()
            }
        }
    }
}
