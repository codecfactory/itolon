package com.example.myapplication.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.example.myapplication.R
import com.example.myapplication.activities.LoginActivity
import com.example.myapplication.prefrences.SharedPref
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.kishandonga.csbx.CustomSnackbar
import io.reactivex.rxjava3.subjects.PublishSubject

object ViewUtils {
    fun showSnackBar(context:Context,message:String,hideAction:Boolean,title:String): PublishSubject<Boolean> {
        val res= PublishSubject.create<Boolean>()
        CustomSnackbar(context).show {
            customView(R.layout.snack_layout)
            padding(10)
            if(!hideAction) {
                duration(BaseTransientBottomBar.LENGTH_INDEFINITE)
            }
            else{
                duration(BaseTransientBottomBar.LENGTH_LONG)
            }
            withCustomView {
                it.findViewById<TextView>(R.id.message_textview).text = message
                if(!hideAction) {
//                    duration(BaseTransientBottomBar.LENGTH_INDEFINITE)
                    it.findViewById<View>(R.id.btnUndo).visibility= View.VISIBLE
                    it.findViewById<TextView>(R.id.btnUndo).text = title
                    it.findViewById<TextView>(R.id.btnUndo).setOnClickListener {
                        dismiss()
                        res.onNext(true)
                    }
                }
                else{
                    duration(BaseTransientBottomBar.LENGTH_LONG)
                }
            }
        }
        return res
    }

    fun show(context: Context,messageShow: String):PublishSubject<Boolean>{
        val res=PublishSubject.create<Boolean>()

        MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Background)
            .setTitle(R.string.app_name)
            .setMessage(messageShow)
            .setPositiveButton(R.string.ok){
                dialog, which ->
                res.onNext(true)

            }
        .setNegativeButton(R.string.cancel){
            dialog, which ->
            res.onNext(false)

        }
        .show()
        return res
    }

    private fun showSingleButtonMaterialDialog(context: Context, messageShow: String):PublishSubject<Boolean>{
        val res=PublishSubject.create<Boolean>()
        MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Background)
            .setTitle(R.string.app_name)
            .setMessage(messageShow)
            .setPositiveButton(R.string.ok){
                    dialog, which ->
                res.onNext(true)

            }
        return res
    }
    fun clearLocalStorage(context: Context,message: String,messageCode:Int) {
        if (messageCode == 303) {
            showSingleButtonMaterialDialog(context, message).subscribe { res->
                if(res){
                    SharedPref.clear()
                    ActivityCompat.finishAffinity(context as Activity)
                    context.startActivity(Intent(context , LoginActivity::class.java))
                }
            }
        }
    }
}