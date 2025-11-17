package com.example.upscprep.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.upscprep.data.model.TestAttempt

@Database(entities = [TestAttempt::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun testAttemptDao(): TestAttemptDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "upsc_prep_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

