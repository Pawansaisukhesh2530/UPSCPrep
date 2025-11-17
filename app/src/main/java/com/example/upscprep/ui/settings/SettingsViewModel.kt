package com.example.upscprep.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.upscprep.utils.SecurePreferences
import com.example.upscprep.utils.ThemeHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Settings Screen
 * Manages app theme and user profile settings
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    // Current theme state
    private val _currentTheme = MutableStateFlow(ThemeHelper.getSavedTheme(context))
    val currentTheme: StateFlow<String> = _currentTheme.asStateFlow()

    // Current username state
    private val _currentUsername = MutableStateFlow("")
    val currentUsername: StateFlow<String> = _currentUsername.asStateFlow()

    // Save status for UI feedback
    private val _saveStatus = MutableStateFlow<SaveStatus>(SaveStatus.Idle)
    val saveStatus: StateFlow<SaveStatus> = _saveStatus.asStateFlow()

    init {
        loadUserSettings()
    }

    /**
     * Load user settings from storage
     */
    private fun loadUserSettings() {
        viewModelScope.launch {
            // Load current theme
            _currentTheme.value = ThemeHelper.getSavedTheme(context)

            // Load username from SecurePreferences or intent
            val savedEmail = SecurePreferences.getSavedEmail(context)
            _currentUsername.value = savedEmail?.substringBefore("@") ?: "Aspirant"
        }
    }

    /**
     * Change app theme
     * @param theme New theme (light, dark, system)
     */
    fun changeTheme(theme: String) {
        viewModelScope.launch {
            // Save theme preference
            ThemeHelper.saveTheme(context, theme)

            // Apply theme immediately
            ThemeHelper.applyTheme(theme)

            // Update state
            _currentTheme.value = theme
        }
    }

    /**
     * Update username
     * @param newUsername New username to save
     */
    fun updateUsername(newUsername: String) {
        viewModelScope.launch {
            if (newUsername.isBlank()) {
                _saveStatus.value = SaveStatus.Error("Username cannot be empty")
                return@launch
            }

            if (newUsername.length < 3) {
                _saveStatus.value = SaveStatus.Error("Username must be at least 3 characters")
                return@launch
            }

            try {
                // Save username to SharedPreferences
                val prefs = context.getSharedPreferences("upsc_settings", android.content.Context.MODE_PRIVATE)
                prefs.edit().putString("username", newUsername).apply()

                // Update state
                _currentUsername.value = newUsername
                _saveStatus.value = SaveStatus.Success("Username updated successfully")

                // Reset status after 2 seconds
                kotlinx.coroutines.delay(2000)
                _saveStatus.value = SaveStatus.Idle
            } catch (e: Exception) {
                _saveStatus.value = SaveStatus.Error("Failed to save username: ${e.message}")
            }
        }
    }

    /**
     * Reset all settings to defaults
     */
    fun resetToDefaults() {
        viewModelScope.launch {
            // Reset theme to system default
            changeTheme(ThemeHelper.THEME_SYSTEM)

            // Reset username to default
            _currentUsername.value = "Aspirant"

            _saveStatus.value = SaveStatus.Success("Settings reset to defaults")

            // Reset status after 2 seconds
            kotlinx.coroutines.delay(2000)
            _saveStatus.value = SaveStatus.Idle
        }
    }

    /**
     * Clear save status
     */
    fun clearSaveStatus() {
        _saveStatus.value = SaveStatus.Idle
    }

    /**
     * Sealed class for save status feedback
     */
    sealed class SaveStatus {
        object Idle : SaveStatus()
        data class Success(val message: String) : SaveStatus()
        data class Error(val message: String) : SaveStatus()
    }
}

