package com.example.upscprep.data.analytics

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TrackingItemEntity::class, ActivityLogEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AnalyticsDatabase : RoomDatabase() {
    abstract fun trackingDao(): TrackingDao
    abstract fun activityLogDao(): ActivityLogDao

    companion object {
        @Volatile private var INSTANCE: AnalyticsDatabase? = null

        fun get(context: Context): AnalyticsDatabase = INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                AnalyticsDatabase::class.java,
                "analytics_db"
            ).fallbackToDestructiveMigration()
             .build()
             .also { INSTANCE = it }
        }
    }
}

