package com.example.myapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.view.CustomProgressBar
import com.example.myapplication.R
import com.example.myapplication.activities.*
import com.example.myapplication.model.defaultmodel.DefaultModel
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.viewmodel.ProfileViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_artist_profile.view.*
import kotlinx.android.synthetic.main.fragment_artist_profile.view.logout_textview
import kotlinx.android.synthetic.main.fragment_artist_profile.view.user_name_textview
import org.json.JSONObject
import java.util.HashMap

class ArtistProfileFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel
    val progressBar = CustomProgressBar()
    private var mView:View ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_artist_profile, container, false)
        profileViewModel = ViewModelProvider.NewInstanceFactory().create(ProfileViewModel::class.java)
        init(mView!!)
        return mView
    }

    private fun init(view: View) {
        mView!!.user_name_textview.text=SharedPref.read(SharedPref.USER_NAME,"")
        view.artist_profile_layout.setOnClickListener {
           startActivity(Intent(activity, ArtistProfileActivity::class.java))
        }
        view.artist_biography_layout.setOnClickListener {
           startActivity(Intent(activity, ArtistBiographyActivity::class.java))
        }
        view.logout_textview.setOnClickListener {
            val hashMap = HashMap<String, String>()
            hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN, "")
            hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN, "")
            profileViewModel.logoutUser(hashMap)
            progressBar.show(activity!!)
            getResponse()
        }
    }
    private fun getResponse() {
        profileViewModel.logoutLiveData!!.observe(this.viewLifecycleOwner, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), DefaultModel::class.java)
                if (response.meta.code == 233) {
                    SharedPref.clear()
                    finishAffinity(activity!!)
                    startActivity(Intent(activity, SplashActivity::class.java).putExtra("title","Stars"))
                }
                else{
                    Toast.makeText(activity!!,response.meta.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}