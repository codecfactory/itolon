package com.example.myapplication.activities
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_webview.*
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.WebChromeClient
import com.example.myapplication.view.CustomProgressBar
import com.example.myapplication.R

class WebViewActivity :AppCompatActivity(){
    val progressBar= CustomProgressBar()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(intent.getStringExtra("title").equals("Payment")){
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        setContentView(R.layout.activity_webview)
        if(intent.hasExtra("title")) {
            page_title_textview.text =intent.getStringExtra("title")
        }
        progress_circular.bringToFront()
        progress_circular.visibility= View.GONE
        progressBar.show(this)
        if(intent.hasExtra("url")){
            startWebView(intent.getStringExtra("url"))
        }
        back_arrow.setOnClickListener {
            finishAffinity()
            startActivity(Intent(this@WebViewActivity,MainActivity::class.java))
        }
    }
    private fun startWebView(url: String) {
        webview.webChromeClient = WebChromeClient()
        webview.webViewClient = WebViewClient()
        webview.clearCache(true)
        webview.clearHistory()
        webview.settings.javaScriptEnabled = true
        webview.settings.javaScriptCanOpenWindowsAutomatically = true
        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
            override fun onPageFinished(view: WebView, url: String) {
                progressBar.dialog.dismiss()
                progress_circular.visibility= View.GONE
            }
            override fun onReceivedError(view: WebView, errorCode: Int,description: String,failingUrl: String) {
            }
        }
        webview.loadUrl(url)
    }
}