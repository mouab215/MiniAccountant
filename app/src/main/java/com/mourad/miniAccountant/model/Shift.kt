package com.mourad.miniAccountant.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mourad.miniAccountant.util.Constants
import com.mourad.miniAccountant.util.Helpers
import kotlinx.android.parcel.Parcelize
import java.util.*
import java.util.concurrent.TimeUnit

@Parcelize
@Entity(tableName = "jobTable")
data class Shift(

    @ColumnInfo(name = "startDateTime")
    var startDateTime: Calendar,

    @ColumnInfo(name = "endDateTime")
    var endDateTime: Calendar,

    @ColumnInfo(name = "isPaid")
    var isPaid: Boolean,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null
): Parcelable {

    fun getSalary(): Double {
        return Constants.HOURLY_RATE * getWorkedHours()
    }

    fun getWorkedHours(): Double {
        var hourDifference = endDateTime.timeInMillis - startDateTime.timeInMillis
        var workedMinutes = String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(hourDifference)).toInt()
        return Helpers.minutesToRoundedHours(workedMinutes)
    }
}