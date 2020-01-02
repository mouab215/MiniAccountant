package com.mourad.miniAccountant.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mourad.miniAccountant.model.Job

@Dao
interface JobDao {

    @Query("SELECT * FROM jobTable ORDER BY jobName ASC")
    fun getAllJobs(): LiveData<List<Job>>

    @Query("SELECT * FROM jobTable WHERE id = :jobId")
    fun getJob(jobId: Long): LiveData<Job?>

    @Insert
    fun insertJob(shift: Job)

    @Delete
    fun deleteJob(shift: Job)

    @Update
    fun updateJob(shift: Job)
}