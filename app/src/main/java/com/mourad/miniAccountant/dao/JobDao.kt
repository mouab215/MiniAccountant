package com.mourad.miniAccountant.dao

import androidx.room.*
import com.mourad.miniAccountant.model.Job

@Dao
interface JobDao {

    @Query("SELECT * FROM jobTable")
    fun getAllJobs(): List<Job>

    @Insert
    fun insertJob(job: Job)

    @Delete
    fun deleteJob(job: Job)

    @Update
    fun updateJob(job: Job)
}