package com.example.runningapp.ui

import android.content.Context
import android.widget.TextView
import com.example.runningapp.R
import com.example.runningapp.db.Run
import com.example.runningapp.other.TrackingUtility
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkView(private val runs: List<Run>, c: Context, layoutId: Int)
    : MarkerView(c, layoutId) {
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)

        if (e==null) return
        val curRunId = e.x.toInt()
        val run = runs[curRunId]

        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        findViewById<TextView>(R.id.marker_date)
        findViewById<TextView>(R.id.marker_date).text = dateFormat.format(run.timestamp)
        findViewById<TextView>(R.id.marker_distance).text = run.distanceInMeters.toString()
        findViewById<TextView>(R.id.marker_time).text = TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)
        findViewById<TextView>(R.id.marker_speed).text = run.avgSpeedKIH.toString()
        findViewById<TextView>(R.id.marker_calories).text = run.caloriesBurned.toString()
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }
}