package com.example.myapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.view.CustomProgressBar
import com.example.myapplication.R
import com.example.myapplication.activities.*
import com.example.myapplication.model.defaultmodel.DefaultModel
import com.example.myapplication.prefrences.Constants
import com.example.myapplication.viewmodel.AdvertismentViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_search.view.*
import org.json.JSONObject

class SearchFragment : Fragment() {
    val progressBar = CustomProgressBar()
    private lateinit var advertismentViewModel: AdvertismentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        advertismentViewModel = ViewModelProvider.NewInstanceFactory().create(AdvertismentViewModel::class.java)
        init(view)
        return view
    }

    private fun init(view: View) {
//        getAdvertisments()
        view.stars_layout.setOnClickListener {
            Constants.isSearch=false
            startActivity(Intent(activity, StarsActivity::class.java).putExtra("title","Top Stars"))
        }
        view.mes_layout.setOnClickListener {
            Constants.isSearch=false
            startActivity(Intent(activity, UserDownloadActivity::class.java).putExtra("title","Mes Telechargements"))
        }
        view.artist_layout.setOnClickListener {
            startActivity(Intent(activity, AllArtistsActivity::class.java).putExtra("title","Artists"))
        }
        view.albems_layout.setOnClickListener {
            Constants.isSearch=true
            startActivity(Intent(activity, AlbumActivity::class.java).putExtra("title","Albems"))
        }
        view.playlist_layout.setOnClickListener {
            Constants.isSearch=false
            startActivity(Intent(activity, PlaylistsActivity::class.java).putExtra("title","Playlist"))
        }
    }
    private fun getAdvertisments() {
        progressBar.show(activity!!)
        advertismentViewModel.adsLiveData!!.observe(activity!!, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), DefaultModel::class.java)
                if (response.meta.code == 210) {
                } else {
//                    showErrorDialog(response.meta.message)
                }
            }else{
//                showErrorDialog("Server is not responding")
            }
        })
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
}