package com.example.upscprep.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.upscprep.MainActivity
import com.example.upscprep.R
import com.example.upscprep.utils.SecurePreferences
import com.google.firebase.auth.FirebaseAuth

/**
 * SplashActivity - Entry point for auto-login functionality
 *
 * Flow:
 * 1. Shows splash screen for 2 seconds
 * 2. Checks if user has saved credentials with Remember Me enabled
 * 3. If yes: validates Firebase session and navigates to Dashboard
 * 4. If no: navigates to LoginActivity
 */
class SplashActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val splashDelay = 2000L // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Delay splash screen and check auto-login
        Handler(Looper.getMainLooper()).postDelayed({
            checkAutoLogin()
        }, splashDelay)
    }

    /**
     * Checks if auto-login is possible
     * Validates both stored credentials and Firebase session
     */
    private fun checkAutoLogin() {
        try {
            // Check if user has valid saved credentials
            val hasValidCredentials = SecurePreferences.hasValidCredentials(this)

            // Check if Firebase session is still active
            val currentUser = firebaseAuth.currentUser

            if (hasValidCredentials && currentUser != null) {
                // User is already authenticated and has Remember Me enabled
                // Navigate directly to MainActivity
                navigateToMain(currentUser.displayName ?: "User")
            } else {
                // No valid credentials or session expired
                // Navigate to LoginActivity
                navigateToLogin()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // On any error, navigate to login
            navigateToLogin()
        }
    }

    /**
     * Navigate to MainActivity (Dashboard)
     */
    private fun navigateToMain(userName: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("USER_NAME", userName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    /**
     * Navigate to LoginActivity
     */
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}

