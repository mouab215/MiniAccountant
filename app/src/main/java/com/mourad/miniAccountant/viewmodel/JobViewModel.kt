package com.mourad.miniAccountant.viewmodel

import android.app.Application
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
//    fun deleteShift() {
//        mainScope.launch {
//            withContext(Dispatchers.IO) {
//                shiftRepository.deleteShift(shift.value!!)
//            }
//        }
//    }
//
//    fun updateShift() {
//        mainScope.launch {
//            withContext(Dispatchers.IO) {
//                shiftRepository.updateShift(shift.value!!)
//            }
//        }
//    }
//
//
//    fun deleteAllGames() {
//        mainScope.launch {
//            withContext(Dispatchers.IO) {
//                gameRepository.deleteAllGames()
//            }
//        }
//    }

}
