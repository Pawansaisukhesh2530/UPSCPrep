package com.example.upscprep.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.upscprep.MainActivity
import com.example.upscprep.databinding.ActivityLoginBinding
import com.example.upscprep.utils.BiometricAuthHelper
import com.example.upscprep.utils.SecurePreferences
import com.google.android.material.snackbar.Snackbar

/**
 * Login Activity for user authentication
 * Uses ViewBinding and MVVM pattern with LoginViewModel
 * Features secure Remember Me functionality with encrypted credentials
 *
 * SECURITY FEATURE: Remember Me requires fingerprint authentication
 * - When user checks Remember Me, fingerprint verification is triggered
 * - Credentials are only saved after successful fingerprint authentication
 * - If fingerprint fails/cancelled, checkbox is unchecked
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var cbRememberMe: CheckBox
    private lateinit var biometricHelper: BiometricAuthHelper

    // Track if Remember Me is actually enabled (after fingerprint verification)
    private var rememberMeEnabled = false

    // Flag to prevent multiple biometric prompts
    private var biometricInProgress = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Remember Me checkbox
        cbRememberMe = binding.cbRememberMe

        // Initialize biometric helper
        biometricHelper = BiometricAuthHelper(this)

        setupObservers()
        setupClickListeners()
        setupRememberMeCheckbox()

        // Load saved credentials if any
        loadSavedCredentials()
    }

    /**
     * Sets up observers for LiveData from ViewModel
     */
    private fun setupObservers() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginViewModel.LoginState.Loading -> {
                    // Show loading UI
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }

                is LoginViewModel.LoginState.Success -> {
                    // Hide loading UI
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true

                    // Save credentials ONLY if Remember Me is enabled (verified by fingerprint)
                    val email = binding.etEmail.text.toString().trim()
                    val password = binding.etPassword.text.toString()

                    if (rememberMeEnabled) {
                        // Remember Me was verified with fingerprint - save credentials
                        SecurePreferences.saveCredentials(this, email, password, true)
                    } else {
                        // Remember Me not enabled - clear any previously saved credentials
                        SecurePreferences.clearCredentials(this)
                    }

                    // Show success message
                    Toast.makeText(
                        this,
                        "Welcome, ${state.user.name}!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Navigate to MainActivity
                    val intent = Intent(this, MainActivity::class.java).apply {
                        putExtra("USER_NAME", state.user.name)
                        putExtra("USER_EMAIL", state.user.email)
                    }
                    startActivity(intent)
                    finish()
                }

                is LoginViewModel.LoginState.Error -> {
                    // Hide loading UI
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true

                    // Show error message
                    Toast.makeText(
                        this,
                        state.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    /**
     * Sets up click listeners for UI elements
     */
    private fun setupClickListeners() {
        // Login button click
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            viewModel.login(email, password)
        }

        // Sign up text click
        binding.tvSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Sets up Remember Me checkbox with fingerprint authentication requirement
     *
     * SECURITY FLOW:
     * 1. User clicks checkbox
     * 2. If checking: Trigger fingerprint authentication first
     * 3. On fingerprint success: Keep checkbox checked, enable Remember Me
     * 4. On fingerprint failure: Uncheck checkbox, show error message
     * 5. User can still login without Remember Me if fingerprint fails
     */
    private fun setupRememberMeCheckbox() {
        cbRememberMe.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && !rememberMeEnabled) {
                // User just checked the box - verify fingerprint first
                // Temporarily remove listener to prevent triggering when we uncheck programmatically
                cbRememberMe.setOnCheckedChangeListener(null)
                cbRememberMe.isChecked = false
                // Re-attach listener
                setupRememberMeCheckbox()
                // Request fingerprint
                requestBiometricForRememberMe()
            } else if (!isChecked && rememberMeEnabled) {
                // User manually unchecked - disable Remember Me
                disableRememberMe()
            }
        }
    }

    /**
     * Request biometric authentication for enabling Remember Me
     */
    private fun requestBiometricForRememberMe() {
        // Prevent multiple concurrent biometric prompts
        if (biometricInProgress) {
            return
        }

        // Check if biometric is available
        if (!biometricHelper.isBiometricAvailable()) {
            val status = biometricHelper.getBiometricStatus()
            showBiometricError(status.message)
            return
        }

        biometricInProgress = true

        // Show biometric prompt
        biometricHelper.verifyForRememberMe(
            onSuccess = {
                // Fingerprint verified successfully!
                biometricInProgress = false
                enableRememberMe()
            },
            onFailure = {
                // User cancelled fingerprint authentication
                biometricInProgress = false
                showMessage("Fingerprint verification cancelled. Remember Me not enabled.")
            },
            onError = { error ->
                // Fingerprint authentication error
                biometricInProgress = false
                showBiometricError(error)
            }
        )
    }

    /**
     * Enable Remember Me after successful fingerprint verification
     */
    private fun enableRememberMe() {
        rememberMeEnabled = true

        // Temporarily remove listener to avoid triggering it when we check programmatically
        cbRememberMe.setOnCheckedChangeListener(null)
        cbRememberMe.isChecked = true
        // Re-attach listener
        setupRememberMeCheckbox()

        // Show success feedback with Snackbar
        Snackbar.make(
            binding.root,
            "✓ Remember Me enabled with fingerprint protection",
            Snackbar.LENGTH_SHORT
        ).show()

        // Optional: Haptic feedback
        try {
            @Suppress("DEPRECATION")
            binding.root.performHapticFeedback(
                android.view.HapticFeedbackConstants.LONG_PRESS,
                android.view.HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )
        } catch (_: Exception) {
            // Haptic feedback not supported
        }
    }

    /**
     * Disable Remember Me
     */
    private fun disableRememberMe() {
        rememberMeEnabled = false
        // Don't need to uncheck checkbox here - user already did it
    }

    /**
     * Show biometric error message with guidance
     */
    private fun showBiometricError(error: String) {
        Snackbar.make(
            binding.root,
            error,
            Snackbar.LENGTH_LONG
        ).setAction("OK") {
            // Dismiss
        }.show()
    }

    /**
     * Show a general message to the user
     */
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Loads saved credentials from encrypted storage
     * Auto-fills email and password if Remember Me was enabled
     *
     * Note: If credentials were saved, it means fingerprint was previously verified
     * So we can enable Remember Me without prompting for fingerprint again
     */
    private fun loadSavedCredentials() {
        try {
            // Get saved email
            val savedEmail = SecurePreferences.getSavedEmail(this)
            if (!savedEmail.isNullOrEmpty()) {
                binding.etEmail.setText(savedEmail)
            }

            // Get remember me status and saved password
            val rememberMe = SecurePreferences.isRememberMeEnabled(this)
            if (rememberMe) {
                val savedPassword = SecurePreferences.getSavedPassword(this)
                if (!savedPassword.isNullOrEmpty()) {
                    binding.etPassword.setText(savedPassword)
                    // Enable Remember Me since credentials were saved (fingerprint was verified previously)
                    cbRememberMe.isChecked = true
                    rememberMeEnabled = true
                }
            }
        } catch (e: Exception) {
            // If there's any error loading credentials, just ignore and continue
            e.printStackTrace()
        }
    }
}

