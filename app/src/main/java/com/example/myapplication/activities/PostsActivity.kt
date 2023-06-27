package com.example.myapplication.activities
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.PostsAdapter
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.model.postsmodel.PostResult
import com.example.myapplication.model.postsmodel.Posts
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.viewmodel.PostsViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_posts.*
import kotlinx.android.synthetic.main.activity_posts.back_arrow_imageview
import org.json.JSONObject
import java.util.HashMap

class PostsActivity : BaseActivity() {
    var isScrolling: Boolean? = false
    var currentItems: Int = 0
    var totalItems: Int = 0
    var scrollOutItems: Int = 0
    var manager: LinearLayoutManager? = null
    var count: Int = 6
    var items: MutableList<PostResult> = mutableListOf()
    private lateinit var postsViewModel: PostsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postsViewModel = ViewModelProvider.NewInstanceFactory().create(PostsViewModel::class.java)
        setContentView(R.layout.activity_posts)
        init()
    }
    private fun init(){
        manager = LinearLayoutManager(this)
        posts_recyclerview.adapter = PostsAdapter(items,this, this)
        posts_recyclerview.layoutManager = manager
        posts_recyclerview.adapter?.notifyDataSetChanged()
        pagination()
        getApiData()
        back_arrow_imageview.setOnClickListener {
            finish()
        }
        swipeToRefresh.setOnRefreshListener {
            swipeToRefresh.isRefreshing=false
        }
    }

    private fun pagination() {
        posts_recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentItems = manager!!.childCount
                totalItems = manager!!.itemCount
                scrollOutItems = manager!!.findFirstVisibleItemPosition()
                if (isScrolling == true && currentItems + scrollOutItems == totalItems && dy > 0) {
                    isScrolling = false
                    count += 1
                }
            }
        })
    }
    private fun getResponse() {
        postsViewModel.postsLiveData!!.observe(this, Observer { tokenResponse ->
            val gson = Gson()
            val json = gson.toJson(tokenResponse)
            val jsonResponse = JSONObject(json)
            progressBar.dialog.dismiss()
            if (jsonResponse.has("body")) {
                val body = jsonResponse.getJSONObject("body")
                val response = gson.fromJson(body.toString(), Posts::class.java)
                if (response.meta.code == 205) {
                    if(response.result.size>0) {
                        items.addAll(response.result)
                        postVisibility(true)
                    }else{
                        postVisibility(false)
                    }
                    posts_recyclerview.adapter!!.notifyDataSetChanged()
                } else {
                    postVisibility(false)
                    showErrorDialog(response.meta.message)
                }
            }else{
                showErrorDialog("Server is not responding")
                postVisibility(false)
            }
        })
    }

    private fun getApiData(){
        if (Utils.getInstance().isNetworkConnected(this@PostsActivity)) {
            val hashMap = HashMap<String, String>()
            hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN,"")
            hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN,"")
            postsViewModel.getPosts(hashMap)
            getResponse()
            progressBar.show(this)
        }
        else{
            showErrorDialog("No Internet Connection")
        }
    }
    private fun showErrorDialog(message:String){
        ViewUtils.showSnackBar(this@PostsActivity,message,false,"Retry").subscribe{ res->
            if(res){
                getApiData()
            }
        }
    }
    private fun postVisibility(flag:Boolean){
        if(flag){
            swipeToRefresh.visibility= View.VISIBLE
            no_result_textview.visibility= View.GONE
        }else{
            swipeToRefresh.visibility= View.GONE
            no_result_textview.visibility= View.VISIBLE
        }
    }
}