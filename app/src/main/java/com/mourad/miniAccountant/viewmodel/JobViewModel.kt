package com.mourad.miniAccountant.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.mourad.miniAccountant.model.Job
import com.mourad.miniAccountant.repository.JobRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JobViewModel(application: Application) : AndroidViewModel(application) {


    private val jobRepository = JobRepository(application.applicationContext)
    private val mainScope = CoroutineScope(Dispatchers.Main)

    val job = MutableLiveData<Job?>()
    val jobs = jobRepository.getAllJobs()

    fun insertJob() {
        mainScope.launch {
            withContext(Dispatchers.IO) {
                jobRepository.insertJob(job.value!!)
            }
        }
    }

    fun updateJob() {
        mainScope.launch {
            withContext(Dispatchers.IO) {
                jobRepository.updateJob(job.value!!)
            }
        }
    }

    fun deleteJob() {
        mainScope.launch {
            withContext(Dispatchers.IO) {
                jobRepository.deleteJob(job.value!!)
            }
        }
    }

    fun updateViewModelJobById(id: Long): JobViewModel {
        mainScope.launch {
            this@JobViewModel.job.value = jobRepository.getJob(id).value
        }
        return this
    }

    fun updateViewModelJob(job: Job): JobViewModel {
        mainScope.launch {
            this@JobViewModel.job.value = job
        }
        return this
    }

}
