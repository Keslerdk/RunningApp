package com.example.runningapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.runningapp.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    val totalAvdSpeed = mainRepository.getTotalAvdSpeed()
    val totalCalories = mainRepository.getTotalCalories()
    val totalDistance = mainRepository.getTotalDistance()
    val totalTime = mainRepository.getTotalTime()

    val runSortedByDate = mainRepository.getAllRunsSortedByDate()
}