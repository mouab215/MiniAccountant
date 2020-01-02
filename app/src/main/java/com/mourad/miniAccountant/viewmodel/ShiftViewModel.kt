package com.mourad.miniAccountant.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.mourad.miniAccountant.model.Job
import com.mourad.miniAccountant.model.Shift
import com.mourad.miniAccountant.repository.ShiftRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShiftViewModel(application: Application, job: Job) : AndroidViewModel(application) {


    private val shiftRepository = ShiftRepository(application.applicationContext)
    private val mainScope = CoroutineScope(Dispatchers.Main)

    val shift = MutableLiveData<Shift?>()
    val shifts = shiftRepository.getAllShiftsOfJob(job.id!!.toLong())

    fun deleteShift() {
        mainScope.launch {
            withContext(Dispatchers.IO) {
                shiftRepository.deleteShift(shift.value!!)
            }
        }
    }

    fun updateShift() {
        mainScope.launch {
            withContext(Dispatchers.IO) {
                shiftRepository.updateShift(shift.value!!)
            }
        }
    }

    fun insertShift() {
        mainScope.launch {
            withContext(Dispatchers.IO) {
                shiftRepository.insertShift(shift.value!!)
            }
        }
    }
//
//    fun deleteAllGames() {
//        mainScope.launch {
//            withContext(Dispatchers.IO) {
//                gameRepository.deleteAllGames()
//            }
//        }
//    }

}
