package com.example.myapplication.activities
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.R
import com.example.myapplication.prefrences.SharedPref
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        getFcmToken()
        Handler().postDelayed({
            if(SharedPref.read(SharedPref.IS_LOGIN, false)){
                if (SharedPref.read(SharedPref.USER_TYPE, "").equals("user")) {
                    val i = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(i)
                }else{
                    if(SharedPref.read(SharedPref.IS_FORM_SUBMITTED,false)){
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java).putExtra("verifyType","artist"))
                    }else{
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java).putExtra("verifyType","artist"))
                    }
                }
                }
            else{
                val i = Intent(this@SplashActivity, LoginOptionActivity::class.java)
                startActivity(i)
            }
            finish()
        }, 2 * 1000)
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
}