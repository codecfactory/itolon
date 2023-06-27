package com.example.myapplication.activities

import android.content.Intent
import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import com.example.myapplication.R
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login_option.*

class LoginOptionActivity : BaseActivity() {
    var callbackManager: CallbackManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id))
        FacebookSdk.sdkInitialize(applicationContext)
//        if (BuildConfig.DEBUG) {
        FacebookSdk.setIsDebugEnabled(false)
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
//        }
        setContentView(R.layout.activity_login_option)
        val metrics: DisplayMetrics =
            DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)

        playMusic()
        setVideoViewFullScreen()
        facebook_textview.setOnClickListener {
            if (Utils.getInstance().isNetworkConnected(this)) {
                LoginManager.getInstance().logOut()
                facebookLoginButton.performClick()
            } else {
//                Utils.getInstance().infoDialog(this@LoginActivity, "Message", "No Internet Connection", false, false, null, "Ok", "Restart", false)
            }
            facebookLogin()
        }
        if(SharedPref.read(SharedPref.IS_LOGIN, false)){
            if (SharedPref.read(SharedPref.USER_TYPE, "").equals("user")) {
                val i = Intent(this@LoginOptionActivity, MainActivity::class.java)
                startActivity(i)
            }else{
                if(SharedPref.read(SharedPref.IS_FORM_SUBMITTED,false)){
                    startActivity(Intent(this@LoginOptionActivity, MainActivity::class.java).putExtra("verifyType","artist"))
                }else{
                    startActivity(Intent(this@LoginOptionActivity, MainActivity::class.java).putExtra("verifyType","artist"))
                }
            }
            finish()
        }
        inscrire_btn.setOnClickListener {
            getFcmToken()
            startActivity(Intent(this@LoginOptionActivity, SignupActivity::class.java))
            finish()
        }
        passoword_textview.setOnClickListener {
            getFcmToken()
            startActivity(Intent(this@LoginOptionActivity, LoginActivity::class.java))
            finish()
        }
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
    private fun setVideoViewFullScreen(){
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val params =
            video_view.layoutParams
        var width = metrics.widthPixels
        var height = metrics.heightPixels
        video_view.minimumWidth=width
        video_view.minimumHeight=height
//        video_view.setLayoutParams(params)
    }
    private fun playMusic() {
         val uri: Uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.two)
        video_view.setVideoURI(uri)
        video_view.start()
        video_view.setOnPreparedListener { mp ->
            mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
            mp.isLooping = true
            mp.setVolume(0.0f,0.0f)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode === RC_SIGN_IN) {
//            val result =
//                Auth.GoogleSignInApi.getSignInResultFromIntent(data)
////            handleSignInResult(result!!)
//            val task: Task<GoogleSignInAccount> =
//                GoogleSignIn.getSignedInAccountFromIntent(data)
////            val result: GoogleSignInResult? = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
//
////            val acct: GoogleSignInAccount = result!!.getSignInAccount()!!
////            val authCode = acct.serverAuthCode
////Log.e("authcode",authCode.toString())
//            handleSignInResult(task)
//
//        }
    }
    private fun facebookLogin() {
        callbackManager = CallbackManager.Factory.create()
        facebookLoginButton.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val loggedOut = AccessToken.getCurrentAccessToken() == null
                if (!loggedOut) {
//                    progressBar.show(this@LoginOptionActivity)
                    val hashMap=HashMap<String,String>()

                    hashMap["token"] = AccessToken.getCurrentAccessToken().token.toString()
//                    hashMap["token"] = "EAALV2CkvCKgBAP1aZC8tVRSCEq1MrBUJk87zpVGT998jeZCHeXDZB02bpcf5L4g8mgUIZCVg4Fe52UA0zDTFvn4ZBZAYrYgo2DiSCTCL8kzKnoL6ZBpNqz9pV5zPmtJBwu3vyIJThvQTOZA8ap0zpEHKGoimtoC2J0Q92DrWbBYVxJHyZBgLdZBd4Q9xHeUh3a1tn2WJNAQvpcagiN86OQ1m7ln0oZCkMO3XVHI28EDDxKv7LtCz00TYUXc3yaRTWXqJhkZD"
                    hashMap["type"] = "0"
//                    userViewModel.registerSocialUser(hashMap)
//                    getResponse()
//                    Log.e("accessToken",AccessToken.getCurrentAccessToken().token.toString())
//                    getUserProfile(AccessToken.getCurrentAccessToken())
                }
            }
            override fun onCancel() {
                Log.e("cancel","cancel")
                // App code
            }
            override fun onError(exception: FacebookException) {
                Log.wtf("loginResultException", exception.toString())
                // App code
            }
        })
    }

}