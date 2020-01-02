package com.mourad.miniAccountant.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mourad.miniAccountant.model.Job

class MyViewModelFactory(private val job: Job, private val application: Application): ViewModelProvider.NewInstanceFactory() {
    override fun <T: ViewModel> create(modelClass:Class<T>): T {
        return ShiftViewModel(application, job) as T
    }
}