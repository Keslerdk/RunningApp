package com.example.runningapp.ui.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runningapp.db.Run
import com.example.runningapp.other.SortType
import com.example.runningapp.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val runsSortedByTime = mainRepository.getAllRunsSortedByTime()
    private val runsSortedByCal = mainRepository.getAllRunsSortedByCal()
    private val runsSortedByDate = mainRepository.getAllRunsSortedByDate()
    private val runsSortedByDistance = mainRepository.getAllRunsSortedByDistance()
    private val runsSortedBySpeed = mainRepository.getAllRunsSortedBySpeed()

    val runs = MediatorLiveData<List<Run>>()

    private var _sortType = SortType.DATA
    val sortType get() = _sortType

    init {
        runs.addSource(runsSortedByDate) { result ->
            if (_sortType == SortType.DATA) runs.value = result
        }
        runs.addSource(runsSortedByTime) { result ->
            if (_sortType == SortType.TIME) runs.value = result
        }
        runs.addSource(runsSortedByCal) { result ->
            if (_sortType == SortType.BURNED_CALORIES) runs.value = result
        }
        runs.addSource(runsSortedByDistance) { result ->
            if (_sortType == SortType.DISTANCE) runs.value = result
        }
        runs.addSource(runsSortedBySpeed) { result ->
            if (_sortType == SortType.AVG_SPEED) runs.value = result
        }
    }

    fun sortRuns(type:SortType) = when (type) {
        SortType.DATA -> runsSortedByDate.value?.let { runs.value = it }
        SortType.TIME -> runsSortedByTime.value?.let { runs.value = it }
        SortType.BURNED_CALORIES -> runsSortedByCal.value?.let { runs.value = it }
        SortType.DISTANCE -> runsSortedByDistance.value?.let { runs.value = it }
        SortType.AVG_SPEED -> runsSortedBySpeed.value?.let { runs.value = it }
    }.also {
        this._sortType = type
    }

    fun insertRun(run: Run) =
        viewModelScope.launch {
            mainRepository.insertRun(run)
        }
}