package com.example.runningapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.runningapp.R
import com.example.runningapp.databinding.FragmentStatisticsBinding
import com.example.runningapp.ui.viewmodels.StatisticsViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Math.round
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StatisticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()
        setUpBarChart()
    }

    private fun setUpBarChart() {
        binding.barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = resources.getColor(R.color.lavender)
            textColor = resources.getColor(R.color.lavender)
            setDrawGridLines(false)
        }

        binding.barChart.axisLeft.apply {
            axisLineColor = resources.getColor(R.color.lavender)
            textColor = resources.getColor(R.color.lavender)
            setDrawGridLines(false)
        }

        binding.barChart.axisRight.isEnabled = false
        binding.barChart.apply {
            description.text = getString(R.string.avg_speed_over_time)
            description.textColor = resources.getColor(R.color.lavender)
            description.textSize = 14f
            legend.isEnabled = false
        }
    }

    private fun subscribeToObservers() {
        viewModel.totalTime.observe(this, {
            it?.let {
                val totalTimeRun = TimeUnit.MILLISECONDS.toHours(it)
                binding.included.timeNum.text = "${totalTimeRun}h"
            }
        })

        viewModel.totalAvdSpeed.observe(this, {
            it?.let {
                binding.included.speedNum.text = "${it}km/h"
            }
        })

        viewModel.totalCalories.observe(this, {
            it?.let {
                val kcal = it / 1000f
                binding.included.caloriesNum.text = "${(kcal * 100f).roundToInt() / 100f}kcal"
            }
        })

        viewModel.totalDistance.observe(this, {
            it?.let {
                val km = it / 1000f
                val totalDistance = (km * 10f).roundToInt() / 10f
                binding.included.distanseNum.text = "${totalDistance}km"
            }
        })

        viewModel.runSortedByDate.observe(this, {
            it.let {
                val allAvgSpeed = it.indices.map { i -> BarEntry(i.toFloat(), it[i].avgSpeedKIH) }
                val barDataSet =
                    BarDataSet(allAvgSpeed, getString(R.string.avg_speed_over_time)).apply {
                        valueTextColor = resources.getColor(R.color.lavender)
                        valueTextSize = 12f
                        color = resources.getColor(R.color.sky_blue)
                    }

                binding.barChart.data = BarData(barDataSet)
                binding.barChart.marker =
                    CustomMarkView(it.reversed(), requireContext(), R.layout.custom_marker)
                binding.barChart.invalidate()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}