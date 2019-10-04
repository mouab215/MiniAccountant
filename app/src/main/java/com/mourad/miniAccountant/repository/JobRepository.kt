package com.mourad.miniAccountant.repository

import android.content.Context
import com.mourad.miniAccountant.dao.JobDao
import com.mourad.miniAccountant.model.Job
import com.mourad.miniAccountant.room.JobRoomDatabase

class JobRepository(context: Context) {

    private var jobDao: JobDao

    init {
        val jobRoomDatabase = JobRoomDatabase.getDatabase(context)
        jobDao = jobRoomDatabase!!.jobDao()
    }

    suspend fun getAllJobs(): List<Job> = jobDao.getAllJobs()

    suspend fun insertJob(job: Job) = jobDao.insertJob(job)

    suspend fun deleteJob(job: Job) = jobDao.deleteJob(job)

    suspend fun updateJob(job: Job) = jobDao.updateJob(job)

}
