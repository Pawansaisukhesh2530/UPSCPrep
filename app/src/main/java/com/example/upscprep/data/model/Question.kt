package com.example.upscprep.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Question(
    val q_id: String,
    val question_number: Int,
    val question_text: String,
    val question_type: String,
    val options: List<Option>,
    val correct_answer: String,
    val explanation: String,
    val marks: Int,
    val difficulty: String,
    val topic_tag: String
) : Parcelable

@Parcelize
data class Option(
    val option_id: String,
    val option_text: String
) : Parcelable

data class TestMetadata(
    val test_id: String,
    val test_title: String,
    val subject: String,
    val gs_paper: String,
    val unit: String,
    val sub_topic: String,
    val total_questions: Int,
    val total_marks: Int,
    val difficulty: String,
    val tags: List<String>
)

data class TestData(
    val test_metadata: TestMetadata,
    val questions: List<Question>
)

@Parcelize
data class UserAnswer(
    val questionId: String,
    val selectedOption: String,
    val isMarkedForReview: Boolean
) : Parcelable

data class TestResult(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val skippedAnswers: Int,
    val score: Double,
    val percentage: Double,
    val timeTaken: Long
)

