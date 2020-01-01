package com.mourad.miniAccountant.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mourad.miniAccountant.model.Job
import com.mourad.miniAccountant.model.Shift

@Dao
interface ShiftDao {

    @Query("SELECT * FROM shiftTable")
    fun getAllShifts(): LiveData<List<Shift>>

    @Query("SELECT * FROM shiftTable WHERE job_id = :jobId")
    fun getAllShiftsOfJob(jobId: Long): LiveData<List<Shift>>

    @Insert
    fun insertShift(shift: Shift)

    @Delete
    fun deleteShift(shift: Shift)

    @Update
    fun updateShift(shift: Shift)
}