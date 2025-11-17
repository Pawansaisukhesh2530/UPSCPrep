package com.example.upscprep.data.repository

import android.content.Context
import com.example.upscprep.data.analytics.AnalyticsDatabase
import com.example.upscprep.data.analytics.SubjectProgressRaw
import com.example.upscprep.data.database.AppDatabase
import com.example.upscprep.data.database.TestAttemptDao
import com.example.upscprep.data.database.WeakAreaRow
import com.example.upscprep.data.model.TestAttempt
import com.example.upscprep.utils.UsageTracker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class SubjectProgress(
    val subjectName: String,
    val completed: Int,
    val total: Int,
) {
    val percentage: Int get() = if (total == 0) 0 else (completed * 100) / total
}

data class TestStatistics(
    val totalTests: Int,
    val avgScore: Double,
    val bestScore: Double,
    val avgTimeMinutes: Double
)

data class WeakArea(
    val subject: String,
    val avgScore: Double,
    val attempts: Int
)

data class ActivityItem(
    val description: String,
    val timeMillis: Long
)

class DashboardRepository(context: Context) {
    private val analyticsDb = AnalyticsDatabase.get(context)
    private val testDao: TestAttemptDao = AppDatabase.getDatabase(context).testAttemptDao()
    private val usage = UsageTracker(context)

    suspend fun getOverallProgress(): Int = withContext(Dispatchers.IO) {
        val total = analyticsDb.trackingDao().getTotalCount()
        val completed = analyticsDb.trackingDao().getCompletedCount()
        if (total == 0) 0 else (completed * 100) / total
    }

    suspend fun getSubjectProgress(): List<SubjectProgress> = withContext(Dispatchers.IO) {
        analyticsDb.trackingDao().getSubjectProgressRaw().map { it.toModel() }
            .sortedByDescending { it.percentage }
    }

    suspend fun getTestStatistics(): TestStatistics = withContext(Dispatchers.IO) {
        val total = testDao.getTotalAttempts()
        val avg = testDao.getAverageScore() ?: 0.0
        val best = testDao.getBestScore() ?: 0.0
        val attempts = testDao.getAllAttempts()
        val avgTime = if (attempts.isEmpty()) 0.0 else attempts.map { it.timeTakenSeconds / 60.0 }.average()
        TestStatistics(total, avg, best, avgTime)
    }

    suspend fun getRecentTests(limit: Int = 5): List<TestAttempt> = withContext(Dispatchers.IO) {
        testDao.getRecentAttempts(limit)
    }

    suspend fun getWeakAreas(limit: Int = 3): List<WeakArea> = withContext(Dispatchers.IO) {
        testDao.getWeakAreasRaw(limit).map { it.toModel() }
    }

    suspend fun getRecentActivity(): List<ActivityItem> = withContext(Dispatchers.IO) {
        analyticsDb.activityLogDao().getRecent().map { ActivityItem(it.description, it.timestamp) }
    }

    fun getTodayStudyMillis(): Long = usage.getTodayStudyMillis()
    fun getStudyStreak(): Int = usage.getCurrentStreak()

    private fun SubjectProgressRaw.toModel() = SubjectProgress(
        subjectName = subject_name,
        completed = completed,
        total = total
    )

    private fun WeakAreaRow.toModel() = WeakArea(
        subject = subject,
        avgScore = avgScore,
        attempts = attempts
    )
}
