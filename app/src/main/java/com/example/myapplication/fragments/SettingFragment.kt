package com.example.myapplication.fragments

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.view.CustomProgressBar
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.activities.*
import com.example.myapplication.model.defaultmodel.DefaultModel
import com.example.myapplication.model.profilemodel.Profile
import com.example.myapplication.prefrences.Constants
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.viewmodel.ProfileViewModel
import com.google.gson.Gson
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import org.json.JSONObject
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class SettingFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel
    val progressBar = CustomProgressBar()
    private var mView:View ?= null
    var myLocale: Locale? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_settings, container, false)
        profileViewModel = ViewModelProvider.NewInstanceFactory().create(ProfileViewModel::class.java)
        init(mView!!)
        return mView
    }

    private fun init(view: View) {
        mView!!.user_name_textview.text=SharedPref.read(SharedPref.USER_NAME,"")
        mView!!.version_textview.text=Utils.getInstance().getVersionCode(requireContext())
        mView!!.post_layout.setOnClickListener {
            startActivity(Intent(activity, PostsActivity::class.java))
        }
        if (SharedPref.read(SharedPref.LANGUAGE_TYPE, "").isNullOrEmpty()||SharedPref.read(SharedPref.LANGUAGE_TYPE,"").equals("English")) {
            mView!!.language_change_textview.text="English"
        }else{
            mView!!.language_change_textview.text="French"
        }
            mView!!.language_change_textview.setOnClickListener {
            if (SharedPref.read(SharedPref.LANGUAGE_TYPE, "").isNullOrEmpty()||SharedPref.read(SharedPref.LANGUAGE_TYPE,"").equals("English")) {
                SharedPref.write(SharedPref.LANGUAGE_TYPE,"French")
                changeLanguage("Are you sure want to convert app language to French")
            }else{
                SharedPref.write(SharedPref.LANGUAGE_TYPE,"English")
                changeLanguage("Are you sure want to convert app language to English")
            }
        }
        mView!!.privacy_url_layout.setOnClickListener {
            startActivity(Intent(activity, WebViewActivity::class.java).putExtra("url","http://itolon.com/privacy-policy").putExtra("title","Privacy Policy"))
        }
        mView!!.term_and_condition_layout.setOnClickListener {
            startActivity(Intent(activity, WebViewActivity::class.java).putExtra("url","http://itolon.com/terms-of-service").putExtra("title","Term & Condition"))
        }
        val hashMap = HashMap<String, String>()
        hashMap["outh_token"] =SharedPref.read(SharedPref.AUTH_TOKEN,"")
        hashMap["device_token"] =SharedPref.read(SharedPref.REFRESH_TOKEN,"")
        hashMap["user_id"] =SharedPref.read(SharedPref.USER_ID,"")
        profileViewModel.getUserProfile(hashMap)
        getResponse()
        progressBar.show(activity as MainActivity)
        view.logout_textview.setOnClickListener {
            ViewUtils.show(requireContext(),"Are you sure you want to logout?").subscribe{ res->
                if(res){
                    if (Utils.getInstance().isNetworkConnected(requireActivity())) {
                        logoutUser()
                    } else{
                        showErrorDialog("No Internet Connection")
                    }
                }
            }
        }
        if(Constants.cartArrayList.size>0){
            view.cart_layout.visibility=View.VISIBLE
            view.cart_divider.visibility=View.VISIBLE
            mView!!.cart_textview.text=Constants.cartArrayList.size.toString()+" tracks"
        }else{
            view.cart_layout.visibility=View.GONE
            view.cart_divider.visibility=View.GONE
        }
        view.profile_image.setOnClickListener {
            EasyImage.openChooserWithGallery(this, "Select", 3)
        }
        view.my_playlist_layout.setOnClickListener {
            startActivity(Intent(requireActivity(),MyPlaylistActivity::class.java))
        }
        view.cart_layout.setOnClickListener {
            startActivity(Intent(requireActivity(),CartDetailActivity::class.java))
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        EasyImage.handleActivityResult(requestCode, resultCode, data, activity, object : DefaultCallback() {
                override fun onImagePicked(imageFile: File?, source: EasyImage.ImageSource?, type: Int) {
                    Glide.with(requireActivity()).load(imageFile!!).placeholder(R.drawable.ic_user_profile).error(R.drawable.ic_user_profile).into(profile_image)
                    uploadFile(imageFile)
                }
                override fun onImagePickerError(e: Exception?, source: EasyImage.ImageSource?, type: Int) {
                }
            })
    }


    private fun setLocale(lang: String) {
        myLocale = Locale(lang)
        val res: Resources = resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
        finishAffinity(activity!!)
        startActivity(Intent(activity!!, LoginOptionActivity::class.java))
    }
    override fun onDetach() {
    super.onDetach()
    try {
        if (progressBar.dialog.isShowing) {
            progressBar.dialog.dismiss()
        }
    } catch (e: Exception) {
    }
}
    private fun changeLanguage(message: String) {
        ViewUtils.show(requireContext(), message).subscribe { res ->
            if (res) {
                if (SharedPref.read(SharedPref.LANGUAGE_TYPE, "").isNullOrEmpty()) {
                    setLocale("")
                } else {
                    setLocale("fr")
                }
            }
        }
    }
    private fun logoutUser(){
        val hashMap = java.util.HashMap<String, String>()
        hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN, "")
        hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN, "")
        profileViewModel.logoutUser(hashMap)
        progressBar.show(activity!!)
        getLogoutResponse()
    }
    private fun showErrorDialog(message:String){
        ViewUtils.showSnackBar(requireContext(),message,false,"Retry").subscribe{res->
            if(res){
                logoutUser()
            }
        }
    }
    private fun getResponse() {
        profileViewModel.profileLiveData!!.observe(this.viewLifecycleOwner, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), Profile::class.java)
                if (response.meta.code == 205) {
                    mView!!.user_name_textview.text=response.result.name +" "+response.result.secondName
                    Glide.with(activity!!).load("http://44.231.47.188"+response.result.photo).error(R.drawable.ic_user).placeholder(R.drawable.ic_user).into(profile_image)
                }
            }
        })
    }
    private fun getLogoutResponse() {
        profileViewModel.logoutLiveData!!.observe(this.viewLifecycleOwner, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), DefaultModel::class.java)
                if (response.meta.code == 233) {
                    val imageUrl=SharedPref.read(SharedPref.IMAGE_URL,"")
                    SharedPref.clear()
                    SharedPref.write(SharedPref.IMAGE_URL,imageUrl)
                    finishAffinity(activity!!)
                    startActivity(Intent(activity, LoginActivity::class.java))
                }else{
                    showErrorDialog(response.meta.message.toString())
                }
            }else{
                ViewUtils.showSnackBar(requireContext(),"Server is not responding",true,"")
            }
        })
    }

    private fun uploadFile(file: File) {
        try {
            progressBar.show(requireContext())
            Ion.with(requireContext()).load("http://44.231.47.188/api/user/update?device_token="+SharedPref.read(SharedPref.REFRESH_TOKEN,"")+"&outh_token="+ SharedPref.read(SharedPref.AUTH_TOKEN,""))
                .setMultipartFile("image", file)
                .asJsonObject().setCallback { e, result ->
                    if (e == null) {
                        progressBar.dialog.dismiss()
                        if (result.get("meta").asJsonObject.get("code").asInt == 210) {
                            SharedPref.write(SharedPref.IMAGE_URL, file.absolutePath)
                            Glide.with(requireActivity()).load(file).placeholder(R.drawable.ic_user_profile).error(R.drawable.ic_user_profile).into(profile_image)
                        } else {
                            ViewUtils.showSnackBar(requireContext(), result.get("meta").asJsonObject.get("message").asString, true, "")
                        }
                    } else {
                        progressBar.dialog.dismiss()
                        ViewUtils.showSnackBar(requireContext(), e.message.toString(), true, "")
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}