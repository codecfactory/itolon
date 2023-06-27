package com.example.myapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.view.CustomProgressBar
import com.example.myapplication.R
import com.example.myapplication.activities.CategoriesDetailActivity
import com.example.myapplication.adapters.CategoriesAdapter
import com.example.myapplication.adapters.CategoriesItemClickListener
import com.example.myapplication.adapters.ViewPagerAdapter
import com.example.myapplication.model.categoriessongmodel.CategoriesResult
import com.example.myapplication.model.categoriessongmodel.CategoriesSongModel
import com.example.myapplication.model.categoriessongmodel.Song
import com.example.myapplication.prefrences.Constants
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.viewmodel.CategorySongViewModel
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.action_bar_layout.view.*
import kotlinx.android.synthetic.main.fragment_movie.*
import kotlinx.android.synthetic.main.fragment_movie.view.*
import org.json.JSONObject
import java.util.*

class AlbumsCategoryFragment : Fragment(), CategoriesItemClickListener {
    private lateinit var categorySongViewModel: CategorySongViewModel
    var mView:View?=null
    val progressBar = CustomProgressBar()
    var manager: GridLayoutManager? = null
    var items: MutableList<CategoriesResult> = mutableListOf()
    var searchItem: MutableList<CategoriesResult> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_movie, container, false)
        categorySongViewModel = ViewModelProvider.NewInstanceFactory().create(CategorySongViewModel::class.java)
        mView=view
        init(view)
        return view
    }
    private fun init(view: View) {
        view. titleTextview?.text = "Search"
        manager = GridLayoutManager(requireContext(),3)
        mView!!.categories_recyclerview.adapter = CategoriesAdapter(items,requireContext(), this)
        mView!!.categories_recyclerview.layoutManager = manager
        getApiData()
        setAdapter(items)
        mView!!.back_arrow_imageview.visibility=View.GONE
        mView!!.album_search_edittext.addTextChangedListener(object : TextWatcher {
            override  fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(!s.toString().isNullOrEmpty()){
                    searchValue(s.toString().toLowerCase())
                }
                else{
                    setAdapter(items)
                }
            }
            override fun afterTextChanged(editable: Editable?) {}
        })
    }
    private fun searchValue(value: String) {
        searchItem.clear()
        for (i in 0 until items.size) {
            if (items[i].name.toLowerCase().contains(value.toLowerCase())) {
                searchItem.add(items[i])
            }
        }
        setAdapter(searchItem)
    }
    private fun setAdapter(itemsArray:MutableList<CategoriesResult>){
        manager = GridLayoutManager(requireContext(),3)
        mView!!.categories_recyclerview.adapter = CategoriesAdapter(itemsArray,requireContext(), this)
        mView!!.categories_recyclerview.layoutManager = manager
    }
    private fun getApiData(){
        if (Utils.getInstance().isNetworkConnected(requireContext())) {
            val hashMap = HashMap<String, String>()
            hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN,"")
            hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN,"")
            categorySongViewModel.getCategoriesSongs(hashMap)
            getResponse()
            progressBar.show(activity!!)
        }
        else{
            showErrorDialog("No Internet Connection")
        }
    }
    private fun getResponse() {
        categorySongViewModel.songsLiveData!!.observe(viewLifecycleOwner, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), CategoriesSongModel::class.java)
                if (response.meta.code == 205) {
                    Constants.categorySongsArrayList.clear()
                    if(response.result!=null&&response.result.size>0) {
                        items.addAll(response.result)
                        mView!!.categories_recyclerview.adapter!!.notifyDataSetChanged()
                        for(i in 0 until response.result.size) {
                            mView!!.tabLayout!!.addTab(mView!!.tabLayout!!.newTab().setText(response.result[i].name))
                            Constants.categorySongsArrayList.addAll(response.result[i].songs)
                        }
                        mView!!.tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL
                        val adapter = ViewPagerAdapter(childFragmentManager, tabLayout!!.tabCount,response.result)
                        mView!!.viewPager!!.adapter = adapter
                        mView!!.viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
                        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                            override fun onTabSelected(tab: TabLayout.Tab) {
                                mView!!.viewPager!!.currentItem = tab.position
                            }
                            override fun onTabUnselected(tab: TabLayout.Tab) {
                            }
                            override fun onTabReselected(tab: TabLayout.Tab) {
                            }
                        })
                    }else{
//                        trackVisibility(false)
                    }
//                    feedRecyclerview.adapter!!.notifyDataSetChanged()
                } else {
//                    trackVisibility(false)
                    showErrorDialog(response.meta.message)
                }
            }else{
//                trackVisibility(false)
                showErrorDialog("Server is not responding")
            }
        })
    }
    private fun showErrorDialog(message:String){
        ViewUtils.showSnackBar(activity!!,message,false,"Retry").subscribe{ res->
            if(res){
                getApiData()
            }
        }
    }

    override fun onClick(position: Int,name:String) {
        startActivity(Intent(requireContext(),CategoriesDetailActivity::class.java).putExtra("items",items[position].songs as ArrayList<Song>).putExtra("name",name))
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