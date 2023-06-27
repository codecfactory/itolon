package com.example.myapplication.activities
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.model.loginmodel.Login
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.viewmodel.LoginViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login_user.*
import kotlinx.android.synthetic.main.activity_login_user.password_editText
import org.json.JSONObject

class LoginActivity : BaseActivity() , AdapterView.OnItemSelectedListener {
    private lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel = ViewModelProvider.NewInstanceFactory().create(LoginViewModel::class.java)
        setContentView(R.layout.activity_login_user)
        login_button.setOnClickListener {
            when {
                mobile_number_editText.text.toString().isNullOrEmpty() -> {
                    ViewUtils.showSnackBar(this@LoginActivity,"Please Enter Number",true,"")
                }
                password_editText.text.toString().isNullOrEmpty() -> {
                    ViewUtils.showSnackBar(this@LoginActivity,"Please Enter Password",true,"")
                }
                else -> {
                    callLoginApi()
                }
            }
        }
        signup_textview.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
        }
        // userType.toLowerCase()
//        categories = ArrayList()
//        spinner.prompt="Select Option"
//        categories!!.add("Select")
//        categories!!.add("User")
//        categories!!.add("Artist")
////        categories!!.add("Executive")
//        val customAdapter = CustomSpinnerAdapter(applicationContext, categories )
//        spinner.setAdapter(customAdapter)
//        spinner.onItemSelectedListener = this
        getFcmToken()
    }

    private fun getFcmToken(){
        try {
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                val token = task.result?.token
                SharedPref.write(SharedPref.REFRESH_TOKEN,token)
            })
        }catch (ignored:Exception){
        }
    }
    private fun callLoginApi(){
        if (Utils.getInstance().isNetworkConnected(this@LoginActivity)) {
            val hashMap = HashMap<String, String>()
            hashMap["phone"] = mobile_number_editText.text.toString()
            hashMap["user_type"] = "user"
            hashMap["password"] = password_editText.text.toString()
            hashMap["device_token"] =SharedPref.read(SharedPref.REFRESH_TOKEN,"")
            hashMap["device_type"] ="android"
            loginViewModel.loginUser(hashMap)
            getResponse()
            progressBar.show(this@LoginActivity)
        }
        else{
            showErrorDialog("No Internet Connection")
        }
    }
    private fun showErrorDialog(message:String){
        ViewUtils.showSnackBar(this@LoginActivity,message,false,"Retry").subscribe{ res->
            if(res){
                callLoginApi()
            }
        }
    }
    private fun getResponse() {
        loginViewModel.loginLiveData!!.observe(this, Observer { tokenResponse ->
            try {
                val gson = Gson()
                val json = gson.toJson(tokenResponse)
                val jsonResponse = JSONObject(json)
                progressBar.dialog.dismiss()
                if (jsonResponse.has("body")) {
                    val body = jsonResponse.getJSONObject("body")
                    val response = gson.fromJson(body.toString(), Login::class.java)
                    if (response.meta.code == 232) {
                        if (response.result.verified == 1) {
                            SharedPref.write(SharedPref.IS_LOGIN,true)
                            SharedPref.write(SharedPref.USER_TYPE, response.result.role.toString())
                            if (!response.result.name.isNullOrEmpty()) {
                                SharedPref.write(SharedPref.USER_NAME, response.result.name.toString())
                            }
                            SharedPref.write(SharedPref.AUTH_TOKEN, response.result.authCode.toString())
                            SharedPref.write(SharedPref.USER_ID, response.result.userId.toString())
                            SharedPref.write(SharedPref.IS_FORM_SUBMITTED, response.result.formSubmitted)
                            if(response.result.role.toString().equals("user")) {
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            } else {
                                if (response.result.formSubmitted) {
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java).putExtra("verifyType", "artist"))
                                } else {
                                    startActivity(Intent(this@LoginActivity, ApplicationActivity::class.java).putExtra("verifyType", "artist"))
                                }
                            }
                            finish()
                        } else {
                            ViewUtils.showSnackBar(this@LoginActivity,"User is not verified",true,"Retry")
                        }
                    } else {
                        showErrorDialog(response.meta.message)
                    }
                }else{
                    if(jsonResponse.get("errorCode")==500) {
                        showErrorDialog("Server is not responding")
                    }else{
                        showErrorDialog(jsonResponse.get("errorMessage").toString())
                    }
                }
            } catch (e: Exception) {
           e.printStackTrace()
            }
        })
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(arg0: AdapterView<*>?, arg1: View?, position: Int, id: Long) {
        if(position!=0){
//            userType=categories!!.get(position)
//            Toast.makeText(applicationContext, categories!!.get(position).toString(), Toast.LENGTH_LONG).show()
        }
    }
}
