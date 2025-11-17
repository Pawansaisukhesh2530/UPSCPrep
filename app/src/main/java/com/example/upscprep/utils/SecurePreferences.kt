package com.example.upscprep.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * SecurePreferences - Singleton helper for encrypted credential storage
 *
 * Uses EncryptedSharedPreferences with AES256_GCM encryption
 * Master key stored in Android Keystore for maximum security
 *
 * Features:
 * - Secure email storage
 * - AES-256 encrypted password storage
 * - Remember Me preference
 * - Clear credentials on logout
 */
object SecurePreferences {

    // SharedPreferences file name
    private const val PREFS_FILE_NAME = "upsc_prep_secure_prefs"

    // Keys for stored data
    private const val KEY_EMAIL = "saved_email"
    private const val KEY_PASSWORD = "saved_password"
    private const val KEY_REMEMBER_ME = "remember_me"

    /**
     * Creates and returns EncryptedSharedPreferences instance
     * Uses MasterKey with AES256_GCM encryption scheme
     *
     * @param context Application context
     * @return EncryptedSharedPreferences instance
     */
    private fun getEncryptedPreferences(context: Context): SharedPreferences {
        // Create or retrieve MasterKey from Android Keystore
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        // Create EncryptedSharedPreferences with AES256_SIV for keys and AES256_GCM for values
        return EncryptedSharedPreferences.create(
            context,
            PREFS_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Saves user credentials securely
     * Email is always saved, password only saved if rememberMe is true
     *
     * @param context Application context
     * @param email User's email address
     * @param password User's password (encrypted if rememberMe is true)
     * @param rememberMe Whether to save password for auto-login
     */
    fun saveCredentials(context: Context, email: String, password: String, rememberMe: Boolean) {
        try {
            val prefs = getEncryptedPreferences(context)
            val editor = prefs.edit()

            // Always save email for convenience
            editor.putString(KEY_EMAIL, email)

            // Save remember me preference
            editor.putBoolean(KEY_REMEMBER_ME, rememberMe)

            // Only save password if remember me is enabled
            if (rememberMe) {
                editor.putString(KEY_PASSWORD, password)
            } else {
                // Clear password if remember me is disabled
                editor.remove(KEY_PASSWORD)
            }

            // Apply changes asynchronously
            editor.apply()
        } catch (e: Exception) {
            // Log error but don't crash the app
            e.printStackTrace()
        }
    }

    /**
     * Retrieves saved email address
     *
     * @param context Application context
     * @return Saved email or null if not found
     */
    fun getSavedEmail(context: Context): String? {
        return try {
            val prefs = getEncryptedPreferences(context)
            prefs.getString(KEY_EMAIL, null)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Retrieves saved password (only if remember me was enabled)
     *
     * @param context Application context
     * @return Saved password or null if not found or remember me disabled
     */
    fun getSavedPassword(context: Context): String? {
        return try {
            val prefs = getEncryptedPreferences(context)
            // Only return password if remember me is enabled
            if (prefs.getBoolean(KEY_REMEMBER_ME, false)) {
                prefs.getString(KEY_PASSWORD, null)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Checks if Remember Me feature is enabled
     *
     * @param context Application context
     * @return true if remember me is enabled, false otherwise
     */
    fun isRememberMeEnabled(context: Context): Boolean {
        return try {
            val prefs = getEncryptedPreferences(context)
            prefs.getBoolean(KEY_REMEMBER_ME, false)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Clears all saved credentials
     * Should be called on logout
     *
     * @param context Application context
     */
    fun clearCredentials(context: Context) {
        try {
            val prefs = getEncryptedPreferences(context)
            val editor = prefs.edit()

            // Remove all saved credentials
            editor.remove(KEY_EMAIL)
            editor.remove(KEY_PASSWORD)
            editor.remove(KEY_REMEMBER_ME)

            // Apply changes immediately for logout
            editor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Checks if credentials are saved and valid
     * Useful for auto-login functionality
     *
     * @param context Application context
     * @return true if both email and password are saved with remember me enabled
     */
    fun hasValidCredentials(context: Context): Boolean {
        return try {
            val email = getSavedEmail(context)
            val password = getSavedPassword(context)
            val rememberMe = isRememberMeEnabled(context)

            !email.isNullOrEmpty() && !password.isNullOrEmpty() && rememberMe
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

