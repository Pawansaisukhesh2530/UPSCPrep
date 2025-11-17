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
}

