package com.example.myapplication.activities
import android.os.Bundle
import com.example.myapplication.R
import com.example.myapplication.base.BaseActivity
import kotlinx.android.synthetic.main.fragment_search.*

class SearchActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_search)
        back_arrow_imageview.setOnClickListener {
            finish()
        }
    }
}