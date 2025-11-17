package com.example.upscprep.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.upscprep.data.model.StudyStats
import com.example.upscprep.data.model.Subject
import com.example.upscprep.data.repository.SubjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Dashboard Screen
 */
class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val subjectRepository = SubjectRepository(application.applicationContext)

    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    private val _stats = MutableStateFlow(StudyStats())
    val stats: StateFlow<StudyStats> = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadData()
    }

    /**
     * Load subjects and statistics from JSON
     */
    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _subjects.value = subjectRepository.getAllSubjects()
                _stats.value = subjectRepository.getStudyStats()
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Refresh data
     */
    fun refresh() {
        loadData()
    }

    /**
     * Get repository instance for other screens
     */
    fun getRepository(): SubjectRepository {
        return subjectRepository
    }
}

