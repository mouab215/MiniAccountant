package com.mourad.miniAccountant.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "jobTable")
data class Job(

    @ColumnInfo(name = "jobName")
    var name: String,

    @ColumnInfo(name = "hourlyWage")
    var hourlyWage: Double,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null
): Parcelable