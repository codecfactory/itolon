package com.example.myapplication.activities

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.fragments.*
import com.example.myapplication.model.CartModel
import com.example.myapplication.prefrences.Constants
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.RxBus
import com.example.myapplication.utils.ViewUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.observers.DisposableObserver
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity(), View.OnClickListener {
    private var mReceiver: BroadcastReceiver? = null
    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }
    private fun init() {
        cart_imageview.bringToFront()
        main_cart_layout.bringToFront()
        actionbar_notifcation_textview.bringToFront()
        cart_imageview.setOnClickListener {
            startActivity(Intent(this@MainActivity,CartDetailActivity::class.java))
        }
        if(Constants.cartArrayList.size>0){
            actionbar_notifcation_textview.text = Constants.cartArrayList.size.toString()
            actionbar_notifcation_textview.visibility=View.GONE
            cart_imageview.visibility=View.GONE
        }
        if (SharedPref.read(SharedPref.USER_TYPE, "").equals("artist")) {
            addFragment(StatisticsFragment(), false, "one")
            bottom_navigation.inflateMenu(R.menu.artist_menu)
        } else if (SharedPref.read(SharedPref.USER_TYPE, "").equals("executive")) {
            addFragment(StatisticsFragment(), false, "one")
            bottom_navigation.inflateMenu(R.menu.executive_menu)
        } else {
            addFragment(FeedFragment(), false, "one")
            bottom_navigation.inflateMenu(R.menu.bottom_navigation_menu)
        }
        bottom_navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
        Dexter.withContext(this).withPermissions(Manifest.permission.CAMERA,  Manifest.permission.WRITE_EXTERNAL_STORAGE,  Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(object :
            MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
            }
            override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken?) { /* ... */
            }
        }).check()
        disposable = RxBus.subscribe(Consumer<Any?> {
            if(Constants.cartArrayList.size>0){
                startActivity(Intent(this@MainActivity,CartDetailActivity::class.java))
//            cart_imageview.bringToFront()
//            main_cart_layout.bringToFront()
//            actionbar_notifcation_textview.bringToFront()
//            actionbar_notifcation_textview.visibility = View.VISIBLE
//            cart_imageview.visibility = View.VISIBLE
//            actionbar_notifcation_textview.text = Constants.cartArrayList.size.toString()
        }else{
//                actionbar_notifcation_textview.visibility = View.GONE
//                cart_imageview.visibility = View.GONE
            }
        })
    }
    var navigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    addFragment(FeedFragment(), false, "one")
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_search -> {
                    Constants.isSearch=false
                    addFragment(SearchFragment(), false, "one")
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_notifications -> {
                    addFragment(SettingFragment(), false, "one")
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    addFragment(ArtistProfileFragment(), false, "one")
                    return@OnNavigationItemSelectedListener true
                }
//                R.id.navigation_playlist -> {
//                    addFragment(PlaylistsFragment(), false, "one")
//                    return@OnNavigationItemSelectedListener true
//                }
                R.id.navigation_categories -> {
                    Constants.isSearch=true
                    addFragment(AlbumsCategoryFragment(), false, "one")
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_statistics -> {
                    addFragment(StatisticsFragment(), false, "one")
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private fun addFragment(fragment: Fragment, addToBackStack: Boolean, tag: String) {
        val manager = supportFragmentManager
        val ft = manager.beginTransaction()
        if (addToBackStack) {
            ft.addToBackStack(tag)
        }
        ft.replace(R.id.container_frame, fragment, tag)
        ft.commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        ViewUtils.show(this@MainActivity,"Are you sure you want to close this application?").subscribe{ res->
            if(res){
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (mReceiver != null) {
                this.unregisterReceiver(this.mReceiver)
            }
        } catch (ignored: Exception) {
        }
    }

    override fun onClick(v: View?) {
    }
}