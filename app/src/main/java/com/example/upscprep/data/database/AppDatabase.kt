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
                // Using fallbackToDestructiveMigration to avoid IllegalStateException when schema versions mismatch
                // If you reintroduce version upgrades later, add proper Migration objects and remove the fallback.
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "upsc_prep_database"
                ).fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
