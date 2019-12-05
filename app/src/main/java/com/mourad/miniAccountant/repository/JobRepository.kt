package com.mourad.miniAccountant.repository

import android.content.Context
import com.mourad.miniAccountant.dao.JobDao
import com.mourad.miniAccountant.model.Job
import com.mourad.miniAccountant.model.Shift
import com.mourad.miniAccountant.room.AccountantRoomDatabase

class JobRepository(context: Context) {

    private var jobDao: JobDao

    init {
        val accountantRoomDatabase = AccountantRoomDatabase.getDatabase(context)
        jobDao = accountantRoomDatabase!!.jobDao()
    }

    suspend fun getAllJobs(): List<Job> = jobDao.getAllJobs()

    suspend fun insertJob(job: Job) = jobDao.insertJob(job)

    suspend fun deleteJob(job: Job) = jobDao.deleteJob(job)

    suspend fun updateJob(job: Job) = jobDao.updateJob(job)

}
