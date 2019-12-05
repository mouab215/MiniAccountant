package com.mourad.miniAccountant.dao

import androidx.room.*
import com.mourad.miniAccountant.model.Job

@Dao
interface JobDao {

    @Query("SELECT * FROM jobTable")
    fun getAllJobs(): List<Job>

    @Insert
    fun insertJob(shift: Job)

    @Delete
    fun deleteJob(shift: Job)

    @Update
    fun updateJob(shift: Job)
}