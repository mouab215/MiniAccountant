package com.mourad.miniAccountant.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.mourad.miniAccountant.dao.JobDao
import com.mourad.miniAccountant.model.Job
import com.mourad.miniAccountant.room.AccountantRoomDatabase

class JobRepository(context: Context) {

    private var jobDao: JobDao

    init {
        val accountantRoomDatabase = AccountantRoomDatabase.getDatabase(context)
        jobDao = accountantRoomDatabase!!.jobDao()
    }

    fun getAllJobs(): LiveData<List<Job>> = jobDao.getAllJobs()

    fun getJob(jobId: Long): LiveData<Job?> = jobDao.getJob(jobId)

    fun insertJob(job: Job) = jobDao.insertJob(job)

    fun deleteJob(job: Job) = jobDao.deleteJob(job)

    fun updateJob(job: Job) = jobDao.updateJob(job)

}
