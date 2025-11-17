package com.example.upscprep.utils

import android.content.Context
import android.util.Log
import com.example.upscprep.data.model.Question
import com.example.upscprep.data.model.TestData
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.IOException

/**
 * Helper class to load and manage quiz questions from JSON assets
 */
object QuestionLoaderHelper {

    private const val TAG = "QuestionLoaderHelper"
    private const val ASSIGNMENTS_BASE_PATH = "assignments"

    /**
     * Load questions from a specific subject folder
     * @param context Application context
     * @param folderName Name of the subject folder (e.g., "history", "polity")
     * @return List of all questions from that folder
     */
    fun loadQuestionsFromFolder(context: Context, folderName: String): List<Question> {
        val questions = mutableListOf<Question>()
        val folderPath = "$ASSIGNMENTS_BASE_PATH/$folderName"

        try {
            val files = context.assets.list(folderPath) ?: emptyArray()
            Log.d(TAG, "Found ${files.size} files in $folderPath")

            files.forEach { fileName ->
                if (fileName.endsWith(".json")) {
                    val filePath = "$folderPath/$fileName"
                    val questionsFromFile = loadQuestionsFromFile(context, filePath)
                    questions.addAll(questionsFromFile)
                    Log.d(TAG, "Loaded ${questionsFromFile.size} questions from $fileName")
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error loading questions from $folderPath", e)
        }

        return questions
    }

    /**
     * Load questions from multiple subject folders
     * @param context Application context
     * @param folderNames List of folder names
     * @return Combined list of questions from all folders
     */
    fun loadQuestionsFromMultipleFolders(context: Context, folderNames: List<String>): List<Question> {
        val allQuestions = mutableListOf<Question>()

        folderNames.forEach { folderName ->
            val questions = loadQuestionsFromFolder(context, folderName)
            allQuestions.addAll(questions)
            Log.d(TAG, "Loaded ${questions.size} questions from $folderName")
        }

        Log.d(TAG, "Total questions loaded from ${folderNames.size} folders: ${allQuestions.size}")
        return allQuestions
    }

    /**
     * Load questions from a single JSON file
     * @param context Application context
     * @param filePath Full path to the JSON file
     * @return List of questions from that file
     */
    private fun loadQuestionsFromFile(context: Context, filePath: String): List<Question> {
        return try {
            val jsonString = context.assets.open(filePath).bufferedReader().use { it.readText() }
            val testData = Gson().fromJson(jsonString, TestData::class.java)
            testData.questions
        } catch (e: IOException) {
            Log.e(TAG, "Error reading file: $filePath", e)
            emptyList()
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "Error parsing JSON from: $filePath", e)
            emptyList()
        }
    }

    /**
     * Filter questions by unit or sub-topic
     * @param questions List of questions to filter
     * @param unit Unit or sub-topic name to filter by
     * @return Filtered list of questions
     */
    fun filterQuestionsByUnit(questions: List<Question>, unit: String): List<Question> {
        return questions.filter {
            it.topic_tag.equals(unit, ignoreCase = true)
        }
    }

    /**
     * Get exactly N random questions from the list
     * @param questions Source list of questions
     * @param count Number of questions to select (default 15)
     * @return List of random questions (may be less than count if not enough available)
     */
    fun getRandomQuestions(questions: List<Question>, count: Int = 15): List<Question> {
        return if (questions.size <= count) {
            questions.shuffled()
        } else {
            questions.shuffled().take(count)
        }
    }

    /**
     * Get unique units/topics from a list of questions
     * @param questions List of questions
     * @return List of unique unit names
     */
    fun getUniqueUnits(questions: List<Question>): List<String> {
        return questions.map { it.topic_tag }
            .distinct()
            .sorted()
    }

    /**
     * Get all available subjects (folder names)
     * @param context Application context
     * @return List of subject folder names
     */
    fun getAvailableSubjects(context: Context): List<String> {
        return try {
            context.assets.list(ASSIGNMENTS_BASE_PATH)?.toList() ?: emptyList()
        } catch (e: IOException) {
            Log.e(TAG, "Error listing subjects", e)
            emptyList()
        }
    }

    /**
     * Get question count for a subject
     * @param context Application context
     * @param folderName Subject folder name
     * @return Total number of questions available
     */
    fun getQuestionCountForSubject(context: Context, folderName: String): Int {
        return loadQuestionsFromFolder(context, folderName).size
    }

    /**
     * Get questions for a specific GS Paper
     * @param context Application context
     * @param paper GS Paper number ("GS I", "GS II", "GS III", "GS IV")
     * @return List of questions for that paper
     */
    fun getQuestionsForGSPaper(context: Context, paper: String): List<Question> {
        val folders = when (paper.uppercase()) {
            "GS I", "GS1" -> listOf("history", "geography")
            "GS II", "GS2" -> listOf("polity", "ethics") // Will filter ethics for governance
            "GS III", "GS3" -> listOf("economy")
            "GS IV", "GS4" -> listOf("ethics")
            else -> emptyList()
        }

        return loadQuestionsFromMultipleFolders(context, folders)
    }
}

