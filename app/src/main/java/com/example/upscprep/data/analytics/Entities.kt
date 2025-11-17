package com.example.upscprep.data.analytics

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracking_items")
data class TrackingItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject_name: String,
    val unit_name: String,
    val topic_name: String,
    val status: String, // "not_started" | "in_progress" | "completed"
    val updated_at: Long = System.currentTimeMillis()
)

@Entity(tableName = "activity_log")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,         // e.g., "topic_completed", "test_attempt", "login"
    val description: String,  // human readable
    val timestamp: Long = System.currentTimeMillis()
)

