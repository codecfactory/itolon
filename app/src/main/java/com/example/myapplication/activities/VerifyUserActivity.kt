package com.example.myapplication.activities
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.viewmodel.SignupViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_code_verification.*
import kotlinx.android.synthetic.main.activity_register.login_textview
import org.json.JSONObject

class VerifyUserActivity : BaseActivity() {
    private lateinit var signupViewModel: SignupViewModel
    private var verificationType="phone_verify"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signupViewModel = ViewModelProvider.NewInstanceFactory().create(SignupViewModel::class.java)
        setContentView(R.layout.activity_code_verification)
        if(intent.hasExtra("verifyType")){
            verificationType="sign_in"
        }
        login_textview.setOnClickListener {
            startActivity(Intent(this@VerifyUserActivity, LoginActivity::class.java))
            finish()
        }
        restrictEditText(digit_one_editText)
        restrictEditText(digit_two_editText)
        restrictEditText(digit_three_editText)
        restrictEditText(digit_four_editText)
        digit_one_editText.addTextChangedListener(GenericTextWatcher(digit_one_editText, digit_two_editText))
        digit_two_editText.addTextChangedListener(GenericTextWatcher(digit_two_editText, digit_three_editText))
        digit_three_editText.addTextChangedListener(GenericTextWatcher(digit_three_editText, digit_four_editText))
        digit_four_editText.addTextChangedListener(GenericTextWatcher(digit_four_editText, null))
        digit_one_editText.setOnKeyListener(GenericKeyEvent(digit_one_editText, null))
        digit_two_editText.setOnKeyListener(GenericKeyEvent(digit_two_editText, digit_one_editText))
        digit_three_editText.setOnKeyListener(GenericKeyEvent(digit_three_editText, digit_two_editText))
        digit_four_editText.setOnKeyListener(GenericKeyEvent(digit_four_editText,digit_three_editText))
        verify_code_button.setOnClickListener {
            when {
                digit_one_editText.text.toString().isNullOrEmpty() -> {
                    showDialog("Please Enter Code")
                }
                digit_two_editText.text.toString().isNullOrEmpty() -> {
                    showDialog("Please Enter Code")
                }
                digit_three_editText.text.toString().isNullOrEmpty() -> {
                    showDialog("Please Enter Code")
                }
                digit_four_editText.text.toString().isNullOrEmpty() -> {
                    showDialog("Please Enter Code")
                }
                else -> {
                    val hashMap = HashMap<String, String>()
                    hashMap["type"] = verificationType
                    hashMap["code"] = digit_one_editText.text.toString().trim()+""+digit_two_editText.text.toString().trim()+""+digit_three_editText.text.toString().trim()+""+digit_four_editText.text.toString().trim()
                    hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN,"")
                    hashMap["outh_token"] =SharedPref.read(SharedPref.AUTH_TOKEN,"")
                    signupViewModel.verifyUser(hashMap)
                    getResponse()
                    progressBar.show(this@VerifyUserActivity)
                }
            }
        }
    }

    private fun getApiData(){
        if (Utils.getInstance().isNetworkConnected(this@VerifyUserActivity)) {
            val hashMap = HashMap<String, String>()
            hashMap["type"] = verificationType
            hashMap["code"] = digit_one_editText.text.toString().trim()+""+digit_two_editText.text.toString().trim()+""+digit_three_editText.text.toString().trim()+""+digit_four_editText.text.toString().trim()
            hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN,"")
            hashMap["outh_token"] =SharedPref.read(SharedPref.AUTH_TOKEN,"")
            signupViewModel.verifyUser(hashMap)
            getResponse()
            progressBar.show(this@VerifyUserActivity)
        }
        else{
            showErrorDialog("No Internet Connection")
        }
    }
    private fun showErrorDialog(message:String){
        ViewUtils.showSnackBar(this@VerifyUserActivity, message, false, "Retry").subscribe{ res->
            if(res){
                getApiData()
            }
        }
    }



    private fun getResponse() {
        signupViewModel.signupLiveData!!.observe(this, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if(jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                finishAffinity()
                startActivity(Intent(this@VerifyUserActivity,LoginActivity::class.java))
            }else{
                ViewUtils.showSnackBar(this@VerifyUserActivity, "Server is not responding", true, "Retry")
            }
        })
    }
    private fun restrictEditText(editText:EditText){
        val maxTextLength = 1
        editText.setFilters(arrayOf<InputFilter>(LengthFilter(maxTextLength)))

    }
}
class GenericKeyEvent internal constructor(private val currentView: EditText, private val previousView: EditText?) : View.OnKeyListener{
    override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if(event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.digit_one_editText && currentView.text.isEmpty()) {
            //If current is empty then previous EditText's number will also be deleted
            previousView!!.text = null
            previousView.requestFocus()
            return true
        }
        return false
    }
}

class GenericTextWatcher internal constructor(private val currentView: View, private val nextView: View?):TextWatcher {
    override fun afterTextChanged(editable: Editable) { // TODO Auto-generated method stub
        val text = editable.toString()
        when (currentView.id) {
            R.id.digit_one_editText -> if (text.length == 1) nextView!!.requestFocus()
            R.id.digit_two_editText -> if (text.length == 1) nextView!!.requestFocus()
            R.id.digit_three_editText -> if (text.length == 1) nextView!!.requestFocus()
        }
    }

    override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) { // TODO Auto-generated method stub
    }

    override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) { // TODO Auto-generated method stub
    }
}