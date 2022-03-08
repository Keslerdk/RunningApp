package com.example.runningapp.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)


    @Query("select * from running_table order by timestamp desc")
    fun getAllRunsSortedByDate() : LiveData<List<Run>>

    @Query("select * from running_table order by avgSpeedKIH desc")
    fun getAllRunsSortedBySpeed() : LiveData<List<Run>>

    @Query("select * from running_table order by distanceInMeters desc")
    fun getAllRunsSortedByDistance() : LiveData<List<Run>>

    @Query("select * from running_table order by timeInMillis desc")
    fun getAllRunsSortedByTime() : LiveData<List<Run>>

    @Query("select * from running_table order by caloriesBurned desc")
    fun getAllRunsSortedByCal() : LiveData<List<Run>>


    @Query("select AVG(timeInMillis) from running_table")
    fun getTotalAvdSpeed() : LiveData<Float>

    @Query("select SUM(timeInMillis) from running_table")
    fun getTotalDistance() : LiveData<Int>

    @Query("select SUM(timeInMillis) from running_table")
    fun getTotalTime() : LiveData<Long>

    @Query("select SUM(timeInMillis) from running_table")
    fun getTotalCalories() : LiveData<Int>

}