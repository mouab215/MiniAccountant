package com.mourad.miniAccountant.repository

import android.content.Context
import com.mourad.miniAccountant.dao.ShiftDao
import com.mourad.miniAccountant.model.Shift
import com.mourad.miniAccountant.room.ShiftRoomDatabase

class ShiftRepository(context: Context) {

    private var shiftDao: ShiftDao

    init {
        val shiftRoomDatabase = ShiftRoomDatabase.getDatabase(context)
        shiftDao = shiftRoomDatabase!!.shiftDao()
    }

    suspend fun getAllShifts(): List<Shift> = shiftDao.getAllShifts()

    suspend fun insertShift(shift: Shift) = shiftDao.insertShift(shift)

    suspend fun deleteShift(shift: Shift) = shiftDao.deleteShift(shift)

    suspend fun updateShift(shift: Shift) = shiftDao.updateShift(shift)

}
