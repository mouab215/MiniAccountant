package com.mourad.miniAccountant.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mourad.miniAccountant.dao.JobDao
import com.mourad.miniAccountant.model.Job
import com.mourad.miniAccountant.util.Converters

@Database(entities = [Job::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class JobRoomDatabase : RoomDatabase() {

    abstract fun jobDao(): JobDao

    companion object {
        private const val DATABASE_NAME = "JOB_DATABASE"

        @Volatile
        private var jobRoomDatabaseInstance: JobRoomDatabase? = null

        fun getDatabase(context: Context): JobRoomDatabase? {
            if (jobRoomDatabaseInstance == null) {
                synchronized(JobRoomDatabase::class.java) {
                    if (jobRoomDatabaseInstance == null) {
                        jobRoomDatabaseInstance = Room.databaseBuilder(
                            context.applicationContext,
                            JobRoomDatabase::class.java, DATABASE_NAME
                        )
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }
            return jobRoomDatabaseInstance
        }
    }

}
