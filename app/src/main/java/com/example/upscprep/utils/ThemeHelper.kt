package com.example.upscprep.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Helper object for managing app theme changes
 * Handles theme switching between Light, Dark, and System Default modes
 */
object ThemeHelper {

    // SharedPreferences keys
    private const val PREFS_NAME = "upsc_settings"
    private const val KEY_THEME = "app_theme"

    // Theme constants
    const val THEME_LIGHT = "light"
    const val THEME_DARK = "dark"
    const val THEME_SYSTEM = "system"

    // In-memory reactive state so Compose can observe changes immediately
    private val themeFlow = MutableStateFlow(THEME_SYSTEM)

    fun themeStateFlow(): StateFlow<String> = themeFlow

    /**
     * Apply theme based on saved preference
     * @param themePref Theme preference value (light, dark, system)
     */
    fun applyTheme(themePref: String) {
        when (themePref) {
            THEME_LIGHT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            THEME_DARK -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            THEME_SYSTEM -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            else -> {
                // Default to system theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
        // Update in-memory state
        themeFlow.value = themePref
    }

    /**
     * Save theme preference to SharedPreferences
     * @param context Application context
     * @param theme Theme to save (light, dark, system)
     */
    fun saveTheme(context: Context, theme: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Use KTX edit extension for clarity
        prefs.edit {
            putString(KEY_THEME, theme)
        }

        // Keep in-memory state in sync
        themeFlow.value = theme
    }

    /**
     * Get saved theme preference
     * @param context Application context
     * @return Saved theme preference, defaults to "system"
     */
    fun getSavedTheme(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_THEME, THEME_SYSTEM) ?: THEME_SYSTEM
    }

    /**
     * Apply saved theme from SharedPreferences
     * Should be called on app launch
     * @param context Application context
     */
    fun applySavedTheme(context: Context) {
        val savedTheme = getSavedTheme(context)
        // Update in-memory flow first
        themeFlow.value = savedTheme
        applyTheme(savedTheme)
    }
}
