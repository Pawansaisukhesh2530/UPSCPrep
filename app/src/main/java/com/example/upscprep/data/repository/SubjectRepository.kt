package com.example.upscprep.data.repository

import com.example.upscprep.data.model.Subject
import com.example.upscprep.data.model.StudyStats
import com.example.upscprep.ui.theme.*

/**
 * Repository for managing subjects and study statistics
 */
class SubjectRepository {

    /**
     * Get all UPSC subjects with sample data
     */
    fun getAllSubjects(): List<Subject> {
        return listOf(
            Subject("History", totalTopics = 45, completedTopics = 28, color = SubjectRed),
            Subject("Geography", totalTopics = 38, completedTopics = 15, color = SubjectGreen),
            Subject("Polity", totalTopics = 52, completedTopics = 42, color = SubjectBlue),
            Subject("Economics", totalTopics = 40, completedTopics = 20, color = SubjectOrange),
            Subject("Environment", totalTopics = 30, completedTopics = 18, color = SubjectTeal),
            Subject("Science & Tech", totalTopics = 35, completedTopics = 10, color = SubjectPurple),
            Subject("Current Affairs", totalTopics = 60, completedTopics = 35, color = SubjectCyan),
            Subject("Ethics", totalTopics = 25, completedTopics = 20, color = SubjectPink),
            Subject("Essay Writing", totalTopics = 15, completedTopics = 8, color = SubjectAmber),
            Subject("Ancient India", totalTopics = 28, completedTopics = 22, color = SubjectIndigo),
            Subject("Modern India", totalTopics = 32, completedTopics = 25, color = SubjectLime),
            Subject("World History", totalTopics = 30, completedTopics = 12, color = SubjectYellow)
        )
    }

    /**
     * Get study statistics
     */
    fun getStudyStats(): StudyStats {
        val subjects = getAllSubjects()
        val totalCompleted = subjects.sumOf { it.completedTopics }

        return StudyStats(
            weeklyStudyMinutes = 1245,  // 20 hours 45 minutes
            currentStreak = 12,
            completedTopics = totalCompleted,
            totalSubjects = subjects.size,
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
}

