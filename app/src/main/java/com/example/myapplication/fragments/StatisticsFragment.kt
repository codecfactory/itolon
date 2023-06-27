package com.example.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.view.CustomProgressBar
import com.example.myapplication.R
import com.example.myapplication.viewmodel.ProfileViewModel
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.fragment_statistics.view.*


class StatisticsFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel
    val progressBar = CustomProgressBar()
    private var mView:View ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_statistics, container, false)
        init(mView!!)
        return mView
    }

    private fun init(view: View) {
//        val graph = findViewById(R.id.graph) as GraphView
        var series: LineGraphSeries<DataPoint?>? =
            LineGraphSeries(arrayOf<DataPoint>(DataPoint(0.0, 1.0),
                    DataPoint(1.0, 5.0),
                    DataPoint(2.0, 3.0),
                    DataPoint(3.0, 2.0),
                    DataPoint(4.0, 6.0)
                )
            )
        view.graph.addSeries(series)

    }

}