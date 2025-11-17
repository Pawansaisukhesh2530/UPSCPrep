package com.example.upscprep.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.upscprep.data.model.StudyStats
import com.example.upscprep.data.model.Subject
import com.example.upscprep.data.repository.DashboardRepository
import com.example.upscprep.data.repository.SubjectRepository
import com.example.upscprep.data.model.TestAttempt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for Dashboard Screen
 */
class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val subjectRepository = SubjectRepository(application.applicationContext)
    private val dashboardRepository = DashboardRepository(application.applicationContext)

    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    private val _stats = MutableStateFlow(StudyStats())
    val stats: StateFlow<StudyStats> = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Combined live dashboard statistics for Compose UI
    data class DashboardStats(
        val overallProgress: Int = 0,
        val subjectProgress: List<com.example.upscprep.data.repository.SubjectProgress> = emptyList(),
        val testStats: com.example.upscprep.data.repository.TestStatistics = com.example.upscprep.data.repository.TestStatistics(0, 0.0, 0.0, 0.0),
        val recentActivity: List<com.example.upscprep.data.repository.ActivityItem> = emptyList(),
        val recentTests: List<TestAttempt> = emptyList(),
        val weakAreas: List<com.example.upscprep.data.repository.WeakArea> = emptyList(),
        val studyStreak: Int = 0,
        val timeTodayMillis: Long = 0L
    )

    private val _dashboardStats = MutableStateFlow(DashboardStats())
    val dashboardStats: StateFlow<DashboardStats> = _dashboardStats.asStateFlow()

    init {
        loadData()
        loadDashboardData()
    }

    /**
     * Load subjects and baseline statistics from JSON
     */
    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _subjects.value = subjectRepository.getAllSubjects()
                _stats.value = subjectRepository.getStudyStats()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Load dynamic, real dashboard data from Room
     */
    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val overall = withContext(Dispatchers.IO) { dashboardRepository.getOverallProgress() }
                val subjects = withContext(Dispatchers.IO) { dashboardRepository.getSubjectProgress() }
                val tests = withContext(Dispatchers.IO) { dashboardRepository.getTestStatistics() }
                val activity = withContext(Dispatchers.IO) { dashboardRepository.getRecentActivity() }
                val recentTests = withContext(Dispatchers.IO) { dashboardRepository.getRecentTests() }
                val weak = withContext(Dispatchers.IO) { dashboardRepository.getWeakAreas() }
                val streak = dashboardRepository.getStudyStreak()
                val timeToday = dashboardRepository.getTodayStudyMillis()
                _dashboardStats.value = DashboardStats(
                    overallProgress = overall,
                    subjectProgress = subjects,
                    testStats = tests,
                    recentActivity = activity,
                    recentTests = recentTests,
                    weakAreas = weak,
                    studyStreak = streak,
                    timeTodayMillis = timeToday
                )
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Refresh both static and dynamic data */
    fun refresh() {
        loadData()
        loadDashboardData()
    }

    /** Get repository for other screens */
    fun getRepository(): SubjectRepository {
        return subjectRepository
    }
}
