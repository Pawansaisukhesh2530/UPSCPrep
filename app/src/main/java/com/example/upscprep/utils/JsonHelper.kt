package com.example.upscprep.utils

import android.content.Context
import com.example.upscprep.data.model.SyllabusSubject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

/**
 * Utility object for loading and parsing JSON syllabus data
 */
object JsonHelper {

    /**
     * Load syllabus data from assets/upsc_complete_syllabus.json
     * @param context Android context to access assets
     * @return List of SyllabusSubject objects or empty list on error
     */
    fun loadSyllabusFromAssets(context: Context): List<SyllabusSubject> {
        return try {
            // Read JSON file from assets
            val jsonString = context.assets.open("upsc_complete_syllabus.json")
                .bufferedReader()
                .use { it.readText() }

            // Parse JSON to List<SyllabusSubject>
            val gson = Gson()
            val listType = object : TypeToken<List<SyllabusSubject>>() {}.type
            val subjects: List<SyllabusSubject> = gson.fromJson(jsonString, listType)

            subjects
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Load syllabus data with error callback
     */
    fun loadSyllabusFromAssets(
        context: Context,
        onSuccess: (List<SyllabusSubject>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val subjects = loadSyllabusFromAssets(context)
            if (subjects.isNotEmpty()) {
                onSuccess(subjects)
            } else {
                onError(Exception("Empty or invalid JSON file"))
            }
        } catch (e: Exception) {
            onError(e)
        }
    }
}

