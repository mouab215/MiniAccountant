package com.mourad.miniAccountant.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.mourad.miniAccountant.util.Helpers
import kotlinx.android.parcel.Parcelize
import java.util.*
import java.util.concurrent.TimeUnit

@Parcelize
@Entity(tableName = "shiftTable",
        foreignKeys = arrayOf(
            ForeignKey(entity = Job::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("job_id"),
                onDelete = CASCADE))
)
data class Shift(

    @ColumnInfo(name = "startDateTime")
    var startDateTime: Calendar,

    @ColumnInfo(name = "endDateTime")
    var endDateTime: Calendar,

    @ColumnInfo(name = "isPaid")
    var isPaid: Boolean,

    @ColumnInfo(name = "job_id")
    var jobId: Long,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null
): Parcelable {

    fun getSalary(hourlyWage: Double): Double {
        return hourlyWage * getWorkedHours()
    }

    fun getWorkedHours(): Double {
        var hourDifference = endDateTime.timeInMillis - startDateTime.timeInMillis
        var workedMinutes = String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(hourDifference)).toInt()
        return Helpers.minutesToRoundedHours(workedMinutes)
    }
}