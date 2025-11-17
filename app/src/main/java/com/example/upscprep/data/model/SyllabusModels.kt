package com.example.upscprep.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data models for UPSC Syllabus structure loaded from JSON
 * Hierarchy: Subject → Unit → SubTopic → TrackingItem
 */

/**
 * Represents a tracking item (leaf level) with flashcard information
 */
@Parcelize
data class TrackingItem(
    val item_id: String,
    val item_name: String,
    val suggested_flashcard_qty: Int,
    var isCompleted: Boolean = false
) : Parcelable

/**
 * Represents a sub-topic containing tracking items
 */
@Parcelize
data class SubTopic(
    val sub_topic_name: String,
    val tracking_items: List<TrackingItem>
) : Parcelable {
    /**
     * Get total number of tracking items
     */
    fun getTotalItems(): Int = tracking_items.size

    /**
     * Get total suggested flashcards
     */
    fun getTotalFlashcards(): Int = tracking_items.sumOf { it.suggested_flashcard_qty }

    /**
     * Get completed items count
     */
    fun getCompletedItems(): Int = tracking_items.count { it.isCompleted }
}

/**
 * Represents a unit containing sub-topics
 */
@Parcelize
data class Unit(
    val unit_name: String,
    val sub_topics: List<SubTopic>
) : Parcelable {
    /**
     * Get total number of sub-topics
     */
    fun getTotalSubTopics(): Int = sub_topics.size

    /**
     * Get total tracking items across all sub-topics
     */
    fun getTotalItems(): Int = sub_topics.sumOf { it.getTotalItems() }

    /**
     * Get total suggested flashcards
     */
    fun getTotalFlashcards(): Int = sub_topics.sumOf { it.getTotalFlashcards() }
}

/**
 * Represents a subject with units (main container)
 */
@Parcelize
data class SyllabusSubject(
    val subject: String,
    val gs_paper: String,
    val units: List<Unit>
) : Parcelable {
    /**
     * Get total number of units
     */
    fun getTotalUnits(): Int = units.size

    /**
     * Get total number of sub-topics
     */
    fun getTotalSubTopics(): Int = units.sumOf { it.getTotalSubTopics() }

    /**
     * Get total tracking items
     */
    fun getTotalTrackingItems(): Int = units.sumOf { it.getTotalItems() }

    /**
     * Get total suggested flashcards
     */
    fun getTotalFlashcards(): Int = units.sumOf { it.getTotalFlashcards() }

    /**
     * Get all tracking items across all units
     */
    fun getAllTrackingItems(): List<TrackingItem> {
        return units.flatMap { unit ->
            unit.sub_topics.flatMap { it.tracking_items }
        }
    }

    /**
     * Get completion percentage
     */
    fun getCompletionPercentage(): Float {
        val allItems = getAllTrackingItems()
        if (allItems.isEmpty()) return 0f
        val completed = allItems.count { it.isCompleted }
        return (completed.toFloat() / allItems.size.toFloat()) * 100f
    }
}

