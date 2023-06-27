package com.example.myapplication.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.ArtistTrackAdapter
import com.example.myapplication.adapters.ClickListener
import com.example.myapplication.adapters.SongCategoryAdapter
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.model.artisttracks.ArtistTrack
import com.example.myapplication.model.artisttracks.TrackResult
import com.example.myapplication.model.categorymodel.CategoryModel
import com.example.myapplication.model.categorymodel.CategoryResult
import com.example.myapplication.model.deleteartistsongmodel.DeleteSong
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.viewmodel.ContentViewModel
import com.google.gson.Gson
import com.koushikdutta.ion.Ion
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
import com.lassi.domain.media.LassiOption
import com.lassi.domain.media.MediaType
import com.lassi.presentation.builder.Lassi
import com.lassi.presentation.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_artist_profile.*
import org.json.JSONObject
import java.io.File
import java.util.HashMap


class ArtistProfileActivity : BaseActivity(), ClickListener {
    val MEDIA_REQUEST_CODE = 101
    var clickPosition = -1
    private lateinit var contentViewModel: ContentViewModel
    var items: MutableList<TrackResult> = mutableListOf()
    var categoryItems: MutableList<CategoryResult> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contentViewModel =
            ViewModelProvider.NewInstanceFactory().create(ContentViewModel::class.java)
        setContentView(R.layout.activity_artist_profile)
        setArtistsAdapter()
        setCategoryAdapter()
        upload_icon_imageview.setOnClickListener {
            startActivityShowResult()
        }
        val hashMap = HashMap<String, String>()
        hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN, "")
        hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN, "")
        contentViewModel.getFeeds(hashMap)
        getResponse()
        progressBar.show(this@ArtistProfileActivity)
    }

    private fun setArtistsAdapter() {
        val layoutManager = LinearLayoutManager(this@ArtistProfileActivity, LinearLayoutManager.HORIZONTAL, false)
        artist_track_recyclerview.layoutManager = layoutManager
        artist_track_recyclerview.adapter = ArtistTrackAdapter(items, this, this, this)
    }
    private fun setCategoryAdapter() {
        val layoutManager = GridLayoutManager(this@ArtistProfileActivity, 2)
        song_category_recyclerview.layoutManager = layoutManager
        song_category_recyclerview.adapter = SongCategoryAdapter(categoryItems, this, this, this)
    }

    private fun getResponse() {
        contentViewModel.postsLiveData!!.observe(this, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), ArtistTrack::class.java)
                if (response.meta.code == 205) {
                    items.addAll(response.result)
                    artist_track_recyclerview.adapter!!.notifyDataSetChanged()
                }
            }
            val hashMap = HashMap<String, String>()
            hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN, "")
            hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN, "")
            contentViewModel.getCategories(hashMap)
            getCategoryResponse()
        })
    }
    private fun getCategoryResponse() {
        contentViewModel.categoriesLiveData!!.observe(this, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), CategoryModel::class.java)
                if (response.meta.code == 205) {
                    categoryItems.addAll(response.result)
                    song_category_recyclerview.adapter!!.notifyDataSetChanged()
                }
            }
        })
    }

    private fun getDeleteSongResponse() {
        contentViewModel.deletePostLiveData!!.observe(this, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), DeleteSong::class.java)
                if (response.meta.code == 210) {
                    items.removeAt(clickPosition)
                    artist_track_recyclerview.adapter!!.notifyDataSetChanged()
                    clickPosition = -1
                }
                showDialog(response.meta.message)
            }
        })
    }

    private fun startActivityShowResult() {
        val intent = Lassi(this)
            .with(LassiOption.GALLERY)
            .setMaxCount(50)
            .setGridSize(3)
            .setMediaType(MediaType.VIDEO)
            .setCompressionRation(10)
            .setMinTime(0)
            .setMaxTime(100)
            .setSupportedFileTypes("mp4", "mkv", "webm", "avi", "flv", "3gp")
            .setStatusBarColor(R.color.colorPrimaryDark)
            .setToolbarResourceColor(R.color.colorPrimary)
            .setProgressBarColor(R.color.colorAccent)
            .setPlaceHolder(R.drawable.ic_image_placeholder)
            .setErrorDrawable(R.drawable.ic_image_placeholder)
            .setCropType(CropImageView.CropShape.RECTANGLE)
            .setCropAspectRatio(1, 1)
            .enableFlip()
            .enableRotate()
            .enableActualCircleCrop()
            .build()
        startActivityForResult(intent, MEDIA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                MEDIA_REQUEST_CODE -> {
                    val selectedMedia =
                        data.getSerializableExtra(KeyUtils.SELECTED_MEDIA) as ArrayList<MiMedia>
                    uploadFile(File(selectedMedia.get(0).path))
                }
            }
        }
    }

    private fun uploadFile(file: File) {
        try {
            progressBar.show(this)
            Ion.with(this@ArtistProfileActivity).load("http://44.231.47.188/api/content/create?device_token=" + SharedPref.read(SharedPref.REFRESH_TOKEN, "") + "&outh_token=" + SharedPref.read(SharedPref.AUTH_TOKEN, "") + "&name=" + "Test" + "&description=" + "description" + "&category_id=" + "1")
                .setMultipartFile("uploads", file)
                .asJsonObject().setCallback { e, result ->
                    if (e == null) {
                        progressBar.dialog.dismiss()
                        if (result.get("meta").asJsonObject.get("code").asInt == 210) {
                            showDialog(result.get("meta").asJsonObject.get("message").asString)
                        } else {
                            showDialog(result.get("meta").asJsonObject.get("message").asString)
                        }
                    } else {
                        showDialog(e.message.toString())
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(position: Int, songId: Int) {
        val hashMap = HashMap<String, String>()
        clickPosition = position
        hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN, "")
        hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN, "")
        hashMap["song_id"] = songId.toString()
        contentViewModel.deleteContent(hashMap)
        getDeleteSongResponse()
        progressBar.show(this@ArtistProfileActivity)
    }
}