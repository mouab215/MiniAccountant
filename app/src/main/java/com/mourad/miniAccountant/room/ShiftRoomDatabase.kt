package com.mourad.miniAccountant.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mourad.miniAccountant.dao.ShiftDao
import com.mourad.miniAccountant.model.Shift
import com.mourad.miniAccountant.util.Converters

@Database(entities = [Shift::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ShiftRoomDatabase : RoomDatabase() {

    abstract fun shiftDao(): ShiftDao

    companion object {
        private const val DATABASE_NAME = "SHIFT_DATABASE"

        @Volatile
        private var shiftRoomDatabaseInstance: ShiftRoomDatabase? = null

        fun getDatabase(context: Context): ShiftRoomDatabase? {
            if (shiftRoomDatabaseInstance == null) {
                synchronized(ShiftRoomDatabase::class.java) {
                    if (shiftRoomDatabaseInstance == null) {
                        shiftRoomDatabaseInstance = Room.databaseBuilder(
                            context.applicationContext,
                            ShiftRoomDatabase::class.java, DATABASE_NAME
                        )
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }
            return shiftRoomDatabaseInstance
        }
    }

}
