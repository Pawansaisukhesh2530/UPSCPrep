package com.example.upscprep.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Centralized theme engine that supports Light, Dark and System modes with instant switching.
 * Call [applySavedTheme] on application start so the UI picks the persisted mode immediately.
 */
object ThemeManager {

    private const val PREFS_NAME = "upsc_settings"
    private const val THEME_PREF_KEY = "app_theme"

    enum class ThemeMode {
        LIGHT, DARK, SYSTEM_DEFAULT
    }

    private val themeFlow = MutableStateFlow(ThemeMode.SYSTEM_DEFAULT)

    fun themeState(): StateFlow<ThemeMode> = themeFlow

    fun applyTheme(mode: ThemeMode) {
        val nightMode = when (mode) {
            ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            ThemeMode.SYSTEM_DEFAULT -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
        themeFlow.value = mode
    }

    fun saveThemePreference(context: Context, mode: ThemeMode) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(THEME_PREF_KEY, mode.name)
        }
        themeFlow.value = mode
    }

    fun getThemePreference(context: Context): ThemeMode {
        val stored = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(THEME_PREF_KEY, ThemeMode.SYSTEM_DEFAULT.name)
        return runCatching { ThemeMode.valueOf(stored.orEmpty()) }.getOrElse { ThemeMode.SYSTEM_DEFAULT }
    }

    fun applySavedTheme(context: Context) {
        val saved = getThemePreference(context)
        themeFlow.value = saved
        applyTheme(saved)
    }
}

