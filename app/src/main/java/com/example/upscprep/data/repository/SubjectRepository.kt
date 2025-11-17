package com.example.upscprep.data.repository

import android.content.Context
import com.example.upscprep.data.model.Subject
import com.example.upscprep.data.model.StudyStats
import com.example.upscprep.data.model.SyllabusSubject
import com.example.upscprep.ui.theme.*
import com.example.upscprep.utils.JsonHelper

/**
 * Repository for managing subjects and study statistics
 * Now loads data dynamically from JSON file
 */
class SubjectRepository(private val context: Context) {

    // Cache for syllabus data
    private var syllabusSubjects: List<SyllabusSubject> = emptyList()

    init {
        loadSyllabusData()
    }

    /**
     * Load syllabus data from JSON
     */
    private fun loadSyllabusData() {
        syllabusSubjects = JsonHelper.loadSyllabusFromAssets(context)
    }

    /**
     * Get all syllabus subjects (raw JSON data)
     */
    fun getSyllabusSubjects(): List<SyllabusSubject> {
        if (syllabusSubjects.isEmpty()) {
            loadSyllabusData()
        }
        return syllabusSubjects
    }

    /**
     * Get all UPSC subjects for display (converted to UI model)
     */
    fun getAllSubjects(): List<Subject> {
        if (syllabusSubjects.isEmpty()) {
            loadSyllabusData()
        }

        val colors = listOf(
            SubjectRed, SubjectGreen, SubjectBlue, SubjectOrange,
            SubjectTeal, SubjectPurple, SubjectCyan, SubjectPink,
            SubjectAmber, SubjectIndigo, SubjectLime, SubjectYellow
        )

        return syllabusSubjects.mapIndexed { index, syllabusSubject ->
            val totalTopics = syllabusSubject.getTotalSubTopics()
            // Simulate completion (you can track this in SharedPreferences or database)
            val completedTopics = (totalTopics * 0.3).toInt() // 30% completed for demo

            Subject(
                name = syllabusSubject.subject,
                totalTopics = totalTopics,
                completedTopics = completedTopics,
                color = colors[index % colors.size]
            )
        }
    }

    /**
     * Get study statistics calculated from JSON data
     */
    fun getStudyStats(): StudyStats {
        if (syllabusSubjects.isEmpty()) {
            loadSyllabusData()
        }

        val subjects = getAllSubjects()
        val totalCompleted = subjects.sumOf { it.completedTopics }
        val totalFlashcards = syllabusSubjects.sumOf { it.getTotalFlashcards() }

        return StudyStats(
            weeklyStudyMinutes = 1245,  // 20 hours 45 minutes (can be tracked)
            currentStreak = 12,
            completedTopics = totalCompleted,
            totalSubjects = syllabusSubjects.size,
            todayStudySessions = 3,
            todayTopicsCovered = 8,
            todayPracticeQuestions = 45
        )
    }

    /**
     * Get subject by name
     */
    fun getSubjectByName(name: String): Subject? {
        return getAllSubjects().find { it.name == name }
    }

    /**
     * Get syllabus subject by name (full JSON data)
     */
    fun getSyllabusSubjectByName(name: String): SyllabusSubject? {
        if (syllabusSubjects.isEmpty()) {
            loadSyllabusData()
        }
        return syllabusSubjects.find { it.subject == name }
    }

    /**
     * Refresh data from JSON
     */
    fun refresh() {
        loadSyllabusData()
    }
}


