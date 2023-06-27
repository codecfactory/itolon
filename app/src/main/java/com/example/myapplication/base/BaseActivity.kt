package com.example.myapplication.base
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.view.CustomProgressBar
import com.example.myapplication.R
import com.example.myapplication.prefrences.SharedPref
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.kishandonga.csbx.CustomSnackbar
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.alert_dialog.view.*

open class BaseActivity : AppCompatActivity() {
    val progressBar = CustomProgressBar()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPref.init(this)
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

    fun showSnackBar(message:String,hideAction:Boolean):PublishSubject<Boolean>{
        val res=PublishSubject.create<Boolean>()
        CustomSnackbar(this).show {
            customView(R.layout.snack_layout)
            padding(10)
            duration(LENGTH_LONG)
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
}