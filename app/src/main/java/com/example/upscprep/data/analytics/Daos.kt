package com.example.upscprep.data.analytics

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TrackingDao {
    @Query("SELECT COUNT(*) FROM tracking_items")
    suspend fun getTotalCount(): Int

    @Query("SELECT COUNT(*) FROM tracking_items WHERE status = 'completed'")
    suspend fun getCompletedCount(): Int

    @Query(
        "SELECT subject_name, COUNT(*) as total, SUM(CASE WHEN status='completed' THEN 1 ELSE 0 END) as completed " +
            "FROM tracking_items GROUP BY subject_name"
    )
    suspend fun getSubjectProgressRaw(): List<SubjectProgressRaw>

    @Query("SELECT * FROM tracking_items WHERE status != 'completed' ORDER BY updated_at DESC LIMIT 5")
    suspend fun getUpcomingTasks(): List<TrackingItemEntity>
}

@Dao
interface ActivityLogDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(log: ActivityLogEntity): Long

    @Query("SELECT * FROM activity_log ORDER BY timestamp DESC LIMIT 10")
    suspend fun getRecent(): List<ActivityLogEntity>
}

/** Projections **/
data class SubjectProgressRaw(
    val subject_name: String,
    val total: Int,
    val completed: Int
)

