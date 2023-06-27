package com.example.myapplication.activities
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.myapplication.view.CustomProgressBar
import com.example.myapplication.R
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.ViewUtils
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.kishandonga.csbx.CustomSnackbar
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.alert_dialog.view.*
import java.util.*

open class BaseActivity : AppCompatActivity() {
    val progressBar = CustomProgressBar()
    var myLocale: Locale? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeActionBarColor()
        SharedPref.init(this)
        if(SharedPref.read(SharedPref.LANGUAGE_TYPE,"").isNullOrEmpty()||SharedPref.read(SharedPref.LANGUAGE_TYPE,"").equals("English")) {
            val languageToLoad = "" // your language
            val locale = Locale(languageToLoad)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        }else{
            val languageToLoad = "fr" // your language
            val locale = Locale(languageToLoad)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        }
//        setLocale("fr")
    }
    fun showDialog(message: String) {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.alert_dialog, null)
        val mBuilder = AlertDialog.Builder(this).setView(mDialogView).setTitle("")
        val mAlertDialog = mBuilder.show()
        val errorMessageTextView = mDialogView.error_message
        val dividerView = mDialogView.divider_view
        val okButton = mDialogView.okButton
        val cancelButton = mDialogView.dialogCancelBtn
        cancelButton.visibility = View.GONE
        dividerView.visibility = View.GONE
        errorMessageTextView.text = message
        okButton.setOnClickListener {
            mAlertDialog.dismiss()
        }
        cancelButton.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }
     fun changeActionBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.BLACK
        }
    }
    fun setLocale(lang: String?) {
        myLocale = Locale(lang)
        val res: Resources = resources
        val dm: DisplayMetrics = res.getDisplayMetrics()
        val conf: Configuration = res.getConfiguration()
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
        ActivityCompat.finishAffinity(this)
//        val refresh =
//        startActivity(Intent(activity!!, LoginOptionActivity::class.java))
    }
    fun showSnackBar(message:String,hideAction:Boolean): PublishSubject<Boolean> {
        val res= PublishSubject.create<Boolean>()
        CustomSnackbar(this).show {
            customView(R.layout.snack_layout)
            padding(10)
            duration(BaseTransientBottomBar.LENGTH_LONG)
            withCustomView {
                it.findViewById<TextView>(R.id.message_textview).text = message
                if(hideAction) {
                    it.findViewById<View>(R.id.btnUndo).visibility=View.VISIBLE
                    it.findViewById<View>(R.id.btnUndo).setOnClickListener {
                        dismiss()
                        res.onNext(true)
                    }
                }
            }
        }
        return res
    }

    fun clearLocalStorage(message: String,messageCode:Int) {
        if (messageCode == 401) {
            ViewUtils.show(this, message).subscribe { res->
                if(res){
                    SharedPref.clear()
                    ActivityCompat.finishAffinity(this@BaseActivity)
                    startActivity(Intent(this@BaseActivity, LoginActivity::class.java))
                }
            }
        }
    }
}