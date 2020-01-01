package com.mourad.miniAccountant.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.mourad.miniAccountant.dao.ShiftDao
import com.mourad.miniAccountant.model.Shift
import com.mourad.miniAccountant.room.AccountantRoomDatabase

class ShiftRepository(context: Context) {

    private var shiftDao: ShiftDao

    init {
        val shiftRoomDatabase = AccountantRoomDatabase.getDatabase(context)
        shiftDao = shiftRoomDatabase!!.shiftDao()
    }

    fun getAllShifts(): LiveData<List<Shift>> = shiftDao.getAllShifts()

    fun getAllShiftsOfJob(jobId: Long): LiveData<List<Shift>> = shiftDao.getAllShiftsOfJob(jobId)

    fun insertShift(shift: Shift) = shiftDao.insertShift(shift)

    fun deleteShift(shift: Shift) = shiftDao.deleteShift(shift)

    fun updateShift(shift: Shift) = shiftDao.updateShift(shift)

}
