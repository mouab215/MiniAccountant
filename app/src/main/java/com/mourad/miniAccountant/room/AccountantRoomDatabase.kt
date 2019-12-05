package com.mourad.miniAccountant.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mourad.miniAccountant.dao.JobDao
import com.mourad.miniAccountant.dao.ShiftDao
import com.mourad.miniAccountant.model.Job
import com.mourad.miniAccountant.model.Shift
import com.mourad.miniAccountant.util.Converters

@Database(entities = [Shift::class, Job::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AccountantRoomDatabase : RoomDatabase() {

    abstract fun shiftDao(): ShiftDao
    abstract fun jobDao(): JobDao

    companion object {
        private const val DATABASE_NAME = "ACCOUNTANT_DATABASE"

        @Volatile
        private var accountantRoomDatabaseInstance: AccountantRoomDatabase? = null

        fun getDatabase(context: Context): AccountantRoomDatabase? {
            if (accountantRoomDatabaseInstance == null) {
                synchronized(AccountantRoomDatabase::class.java) {
                    if (accountantRoomDatabaseInstance == null) {
                        accountantRoomDatabaseInstance = Room.databaseBuilder(
                            context.applicationContext,
                            AccountantRoomDatabase::class.java, DATABASE_NAME
                        )
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }
            return accountantRoomDatabaseInstance
        }
    }

}
