package com.example.upscprep.data.model

import androidx.compose.ui.graphics.Color

/**
 * Data class representing a UPSC subject
 * @param name Subject name
 * @param totalTopics Total number of topics in this subject
 * @param completedTopics Number of topics completed
 * @param color Color associated with this subject
 */
data class Subject(
    val name: String,
    val totalTopics: Int,
    val completedTopics: Int,
    val color: Color
) {
    /**
     * Calculate completion percentage
     */
    val progressPercentage: Float
        get() = if (totalTopics > 0) (completedTopics.toFloat() / totalTopics.toFloat()) else 0f

    /**
     * Get formatted progress string
     */
    val progressText: String
        get() = "$completedTopics/$totalTopics Topics"
}

