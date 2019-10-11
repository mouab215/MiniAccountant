package com.mourad.miniAccountant.dao

import androidx.room.*
import com.mourad.miniAccountant.model.Shift

@Dao
interface ShiftDao {

    @Query("SELECT * FROM jobTable")
    fun getAllShifts(): List<Shift>

    @Insert
    fun insertShift(shift: Shift)

    @Delete
    fun deleteShift(shift: Shift)

    @Update
    fun updateShift(shift: Shift)
}