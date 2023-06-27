package com.example.myapplication.activities

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.myapplication.R
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.model.createplaylistmodel.CreatePlaylistModel
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.viewmodel.CreatePlaylistViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.activity_create_playlist.*
import org.json.JSONObject
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import java.io.File

class CreatePlaylistActivity : BaseActivity() {
    private lateinit var createPlaylistViewModel: CreatePlaylistViewModel
    var snackbarwithbutton: Snackbar? = null
    var imageFileSelect:File?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createPlaylistViewModel =
            ViewModelProvider.NewInstanceFactory().create(CreatePlaylistViewModel::class.java)
        setContentView(R.layout.activity_create_playlist)
        back_imageview.setOnClickListener { finish() }
        create_playlist_textview.setOnClickListener {
            if (playlist_name_edittext.text.toString().isNullOrEmpty()) {
                ViewUtils.showSnackBar(this@CreatePlaylistActivity, "Please Enter Playlist Name", true, "Retry")
            } else if (playlist_description_edittext.text.toString().isNullOrEmpty()) {
                ViewUtils.showSnackBar(this@CreatePlaylistActivity, "Please Enter Playlist Description", true, "Retry")
            } else {
                getApiData()
            }
        }
        add_to_playlist_imageview_layout.setOnClickListener {
            EasyImage.openChooserWithGallery(this, "Select", 3)

        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, object : DefaultCallback() {
            override fun onImagePicked(imageFile: File?, source: EasyImage.ImageSource?, type: Int) {
                Glide.with(this@CreatePlaylistActivity).load(imageFile!!)
                    .into(object : SimpleTarget<Drawable?>() {
                                           override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable?>?
                        ) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                add_to_playlist_imageview_layout.setBackground(resource)
                            }
                        }
                    })
//                Glide.with(this@CreatePlaylistActivity).load(imageFile!!).placeholder(R.drawable.ic_user_profile).error(R.drawable.ic_user_profile).into(playlist_image)
                imageFileSelect=imageFile!!
            }
            override fun onImagePickerError(e: Exception?, source: EasyImage.ImageSource?, type: Int) {
            }
        })
    }
    private fun getApiData(){
        if (Utils.getInstance().isNetworkConnected(this@CreatePlaylistActivity)) {
            val hashMap = HashMap<String, String>()
            hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN, "")
            hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN, "")
            hashMap["name"] = playlist_name_edittext.text.toString()
            hashMap["desc"] = playlist_description_edittext.text.toString()
//            hashMap["playlist_id"] = intent.getStringExtra("id")
//            createPlaylistViewModel.createPlaylist(hashMap)
//            getResponse()
            createPlaylist(playlist_name_edittext.text.toString(), playlist_description_edittext.text.toString())
            progressBar.show(this)
        }
        else{
            showErrorDialog("No Internet Connection")
        }
    }
    private fun showErrorDialog(message:String){
        ViewUtils.showSnackBar(this@CreatePlaylistActivity,message,false,"Retry").subscribe{ res->
            if(res){
                getApiData()
            }
        }
    }

    private fun getResponse() {
        createPlaylistViewModel.createPlaylistListLiveData!!.observe(this, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), CreatePlaylistModel::class.java)
                if (response.meta.code == 210) {
                    snackbarwithbutton=Snackbar.make(findViewById(R.id.parent_layout), response.meta.message, Snackbar.LENGTH_LONG)
                    snackbarwithbutton!!.show();
                    finish()
                } else {
                    showErrorDialog(response.meta.message)
                }
            }
        })
    }

    private fun createPlaylist(name:String,description:String) {

        try {
       var ion=     Ion.with(this@CreatePlaylistActivity)
                .load("http://44.231.47.188/api/user/create_update_playlist?device_token="+SharedPref.read(SharedPref.REFRESH_TOKEN,"")+"&outh_token="+
                        SharedPref.read(SharedPref.AUTH_TOKEN,"")+"&name="+name.trim().toString()+"&desc="+ description.trim()
                )
               if(imageFileSelect!=null) {
                   ion.setMultipartFile("upload", imageFileSelect)
               }
            ion.asJsonObject().setCallback { e, result ->
                    if (e == null) {
                        progressBar.dialog.dismiss()
                        if (result.get("meta").asJsonObject.get("code").asInt == 210) {
                            snackbarwithbutton=Snackbar.make(findViewById(R.id.parent_layout), result.get("meta").asJsonObject.get("message").asString , Snackbar.LENGTH_LONG)
                            snackbarwithbutton!!.show();
                            finish()
                        } else {
                            showErrorDialog(result.get("meta").asJsonObject.get("message").asString)
                        }
                    } else {
                        progressBar.dialog.dismiss()
                        showErrorDialog(e.message.toString())
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}