package com.example.upscprep.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.upscprep.data.model.TestAttempt

@Dao
interface TestAttemptDao {
    @Insert
    suspend fun insertAttempt(attempt: TestAttempt): Long

    @Query("SELECT * FROM test_attempts ORDER BY attemptDate DESC")
    suspend fun getAllAttempts(): List<TestAttempt>

    @Query("SELECT * FROM test_attempts WHERE testType = :type ORDER BY attemptDate DESC")
    suspend fun getAttemptsByType(type: String): List<TestAttempt>

    @Query("SELECT * FROM test_attempts WHERE id = :id")
    suspend fun getAttemptById(id: Long): TestAttempt?

    @Query("SELECT COUNT(*) FROM test_attempts")
    suspend fun getTotalAttempts(): Int

    @Query("SELECT AVG(percentage) FROM test_attempts")
    suspend fun getAverageScore(): Double?

    @Query("SELECT MAX(percentage) FROM test_attempts")
    suspend fun getBestScore(): Double?

    // Recent attempts (limit)
    @Query("SELECT * FROM test_attempts ORDER BY attemptDate DESC LIMIT :limit")
    suspend fun getRecentAttempts(limit: Int): List<TestAttempt>

    // Weak areas aggregated by subject
    @Query(
        "SELECT subjectName AS subject, AVG(percentage) AS avgScore, COUNT(*) AS attempts " +
            "FROM test_attempts WHERE subjectName IS NOT NULL GROUP BY subjectName HAVING avgScore < 60 " +
            "ORDER BY avgScore ASC LIMIT :limit"
    )
    suspend fun getWeakAreasRaw(limit: Int): List<WeakAreaRow>
}

// Projection for weak areas
data class WeakAreaRow(
    val subject: String,
    val avgScore: Double,
    val attempts: Int
)
