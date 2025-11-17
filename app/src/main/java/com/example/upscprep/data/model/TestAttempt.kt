package com.example.upscprep.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_attempts")
data class TestAttempt(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val testType: String,  // "subject", "gs_paper", "unit"
    val subjectName: String?,
    val unitName: String?,
    val gsPaper: String?,
    val attemptDate: Long,
    val questionsJson: String,  // Serialized questions
    val answersJson: String,    // Serialized user answers
    val correctCount: Int,
    val wrongCount: Int,
    val skippedCount: Int,
    val score: Double,
    val percentage: Double,
    val timeTakenSeconds: Int
)

