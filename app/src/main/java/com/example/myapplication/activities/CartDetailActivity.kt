package com.example.myapplication.activities
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.CartClickListener
import com.example.myapplication.adapters.CartDetailAdapter
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.model.CartModel
import com.example.myapplication.model.albumdetailmodel.AlbumDetailModel
import com.example.myapplication.model.albumdetailmodel.Song
import com.example.myapplication.model.cartapimodel.CartApiModel
import com.example.myapplication.prefrences.Constants
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.RxBus
import com.example.myapplication.utils.Utils
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.viewmodel.CartViewModel
import com.google.gson.Gson
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import kotlinx.android.synthetic.main.activity_artists_biographie.*
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_cart.back_arrow_imageview
import kotlinx.android.synthetic.main.activity_cart.no_result_textview
import kotlinx.android.synthetic.main.activity_cart.profile_image
import kotlinx.android.synthetic.main.activity_cart.titleTextview
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class CartDetailActivity : BaseActivity(), CartClickListener {
    var isScrolling: Boolean? = false
    var currentItems: Int = 0
    var totalItems: Int = 0
    var scrollOutItems: Int = 0
    var manager: LinearLayoutManager? = null
    var count: Int = 6
    var clickPosition=-1
    var playFilePath=""
    var playSongPosition=-1
    var items: MutableList<Song> = mutableListOf()
    private var filePath = ""
    private var mediaPlayer: MediaPlayer? = null
    private var duration=""
    val songsArrayList=ArrayList<String>()
    val videoArrayList=ArrayList<String>()
    val idsArray=ArrayList<String>()
    var totalPrice=0
    private lateinit var cartViewModel: CartViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cartViewModel = ViewModelProvider.NewInstanceFactory().create(CartViewModel::class.java)
        setContentView(R.layout.activity_cart)
        init()
    }
    private fun init(){
        manager = LinearLayoutManager(this)
        cart_recyclerview.adapter = CartDetailAdapter(Constants.cartArrayList,this, this)
        cart_recyclerview.layoutManager = manager
        pagination()
        back_arrow_imageview.setOnClickListener {
            finish()
        }
        profile_image.setOnClickListener {
            startActivity(Intent(this@CartDetailActivity, ArtistsActivity::class.java))
        }
        payment_textview.setOnClickListener {
                addToCartApiCall()
        }
        process_payment_layout.setOnClickListener {
            startActivity(Intent(this@CartDetailActivity, PaymentActivity::class.java))
        }
        for(i in 0 until Constants.cartArrayList.size){
            totalPrice += Constants.cartArrayList[i].price
        }
        if(totalPrice>0) {
            total_price_textview.text = "€ " + totalPrice.toString()
        }else{
            total_price_textview.text = "€ " +0
        }
    }
    private fun addToCartApiCall(){
        if (Utils.getInstance().isNetworkConnected(this@CartDetailActivity)) {
            if (Constants.cartArrayList.size > 0) {
                for (i in 0 until Constants.cartArrayList.size) {
                    idsArray.add(Constants.cartArrayList[i].id.toString())
                }
                val allIds: String = TextUtils.join(",", idsArray)

                val hashMap = HashMap<String, String>()
                hashMap["outh_token"] = SharedPref.read(SharedPref.AUTH_TOKEN, "")
                hashMap["device_token"] = SharedPref.read(SharedPref.REFRESH_TOKEN, "")
                hashMap["song_ids"] = allIds
                hashMap["amount"] = totalPrice.toString()
                cartViewModel.addToCart(hashMap)
                getResponse()
                progressBar.show(this)
            } else {
                showErrorDialog("No Internet Connection")
            }
        }else{
            ViewUtils.showSnackBar(this@CartDetailActivity,"There is no item in cart",true,"")
        }
    }
    private fun showErrorDialog(message:String){
        ViewUtils.showSnackBar(this@CartDetailActivity,message,false,"Retry").subscribe{ res->
            if(res){
                addToCartApiCall()
            }
        }
    }

    private fun pagination() {
        cart_recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
        cartViewModel.cartLiveData!!.observe(this, Observer { tokenResponse ->
            try {
                val gson = Gson()
                val json = gson.toJson(tokenResponse)
                val jsonResponse = JSONObject(json)
                progressBar.dialog.dismiss()
                if (jsonResponse.has("body")) {
                    val body = jsonResponse.getJSONObject("body")
                    val response = gson.fromJson(body.toString(), CartApiModel::class.java)
                    if (response.meta.code == 210) {
                        if (response.result.paymentUrl != null) {
                            startActivity(Intent(this, WebViewActivity::class.java).putExtra("url", response.result.paymentUrl).putExtra("title", "Payment"))
                            finish()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    private fun downloadFile(url: String,position:Int) {
        if(!progressBar.dialog.isShowing) {
            progressBar.show(this)
        }
        FileLoader.with(this).load(url, false) //2nd parameter is optioal, pass true to force load from network
            .fromDirectory("test4", FileLoader.DIR_INTERNAL).asFile(object : FileRequestListener<File> {
                override fun onLoad(request: FileLoadRequest, response: FileResponse<File>) {
                    val loadedFile = response.body
                    if(loadedFile.path.contains(".mp3")||loadedFile.path.contains(".m4a")) {
                        items[position].duration=getDuration( loadedFile.path)
                        songsArrayList.add(loadedFile.path)
                    }else{
                        items[position].duration=getDuration( loadedFile.path)
                        videoArrayList.add(loadedFile.path)
                    }
                    clickPosition=-1
                    popular_tracks_recyclerview.adapter!!.notifyDataSetChanged()
                    progressBar.dialog.dismiss()
                }
                override fun onError(request: FileLoadRequest, t: Throwable) {
                    progressBar.dialog.dismiss()
                }
            })
    }
    override fun onClick(position: Int, songUrl: String) {
        clickPosition=position
    }

    override fun onItemDeleteClick(position: Int) {
        ViewUtils.show(this,"Are you sure you want to remove this product from cart").subscribe {res->
            if(res){
                Constants.cartArrayList.removeAt(position)
                cart_recyclerview.adapter!!.notifyDataSetChanged()
                RxBus.publish( CartModel(-1,"","",-1));
            }
        }
    }

    private fun trackVisibility(flag:Boolean){
        if(flag){
            track_scrollview.visibility= View.VISIBLE
            no_result_textview.visibility= View.GONE
        }else{
            track_scrollview.visibility= View.GONE
            no_result_textview.visibility= View.VISIBLE
        }
    }
    private fun getDuration(filePath: String):String {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer!!.setDataSource(filePath)
        mediaPlayer!!.prepare()
        duration = Utils.getInstance().getDurationInMinutes(mediaPlayer!!.duration.toLong())
        return duration
    }
    private fun computePosition(songUrl: String):Int{
        val fileName=Utils.getInstance().getLastString(songUrl)
        for(i in 0 until Constants.songsArrayList.size){
            if(Constants.songsArrayList.get(i).contains(fileName)){
                playSongPosition=i
                playFilePath= Constants.songsArrayList.get(i)
            }
        }
        return playSongPosition
    }
}