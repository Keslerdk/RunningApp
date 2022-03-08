package com.example.runningapp.repositories

import com.example.runningapp.db.Run
import com.example.runningapp.db.RunDao
import javax.inject.Inject

class MainRepository @Inject constructor(private val runDao: RunDao){

    suspend fun insertRun(run: Run) = runDao.insertRun(run)
    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    fun getAllRunsSortedByDate() = runDao.getAllRunsSortedByDate()
    fun getAllRunsSortedByCal() = runDao.getAllRunsSortedByCal()
    fun getAllRunsSortedByDistance() = runDao.getAllRunsSortedByDistance()
    fun getAllRunsSortedBySpeed() = runDao.getAllRunsSortedBySpeed()
    fun getAllRunsSortedByTime() = runDao.getAllRunsSortedByTime()

    fun getTotalAvdSpeed() = runDao.getTotalAvdSpeed()
    fun getTotalDistance() = runDao.getTotalDistance()
    fun getTotalCalories() = runDao.getTotalCalories()
    fun getTotalTime() = runDao.getTotalTime()
}