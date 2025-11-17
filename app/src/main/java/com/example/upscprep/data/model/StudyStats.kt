package com.example.upscprep.data.model

/**
 * Data class representing study statistics
 */
data class StudyStats(
    val weeklyStudyMinutes: Int = 0,  // Total minutes studied this week
    val currentStreak: Int = 0,        // Current study streak in days
    val completedTopics: Int = 0,      // Total completed topics across all subjects
    val totalSubjects: Int = 0,        // Total number of subjects
    val todayStudySessions: Int = 0,   // Number of study sessions today
    val todayTopicsCovered: Int = 0,   // Topics covered today
    val todayPracticeQuestions: Int = 0 // Practice questions attempted today
) {
    /**
     * Get formatted weekly study time
     */
    val weeklyStudyTimeFormatted: String
        get() {
            val hours = weeklyStudyMinutes / 60
            val minutes = weeklyStudyMinutes % 60
            return "${hours}h ${minutes}m"
        }

    /**
     * Get formatted study time for display
     */
    val studyTimeDisplay: String
        get() {
            val hours = weeklyStudyMinutes / 60
            return if (hours > 0) "${hours}h" else "${weeklyStudyMinutes}m"
        }
}

