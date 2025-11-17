package com.example.upscprep.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

/**
 * Helper class for biometric authentication (fingerprint)
 * Used to secure Remember Me functionality with fingerprint verification
 */
class BiometricAuthHelper(private val activity: AppCompatActivity) {

    /**
     * Check if biometric authentication is available on the device
     * @return true if biometric is available, false otherwise
     */
    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(activity)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    /**
     * Get detailed biometric availability status
     * @return String message describing the biometric status
     */
    fun getBiometricStatus(): BiometricStatus {
        val biometricManager = BiometricManager.from(activity)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                BiometricStatus.Available

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                BiometricStatus.NoHardware("No fingerprint hardware available on this device")

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                BiometricStatus.HardwareUnavailable("Fingerprint hardware is currently unavailable")

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                BiometricStatus.NoneEnrolled("No fingerprint enrolled. Please add fingerprint in device settings")

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                BiometricStatus.SecurityUpdateRequired("Security update required for biometric authentication")

            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ->
                BiometricStatus.Unsupported("Biometric authentication is not supported on this device")

            BiometricManager.BIOMETRIC_STATUS_UNKNOWN ->
                BiometricStatus.Unknown("Biometric status unknown")

            else ->
                BiometricStatus.NotAvailable("Biometric authentication not available")
        }
    }

    /**
     * Verify biometric authentication for Remember Me feature
     * Shows biometric prompt and returns success/failure via callbacks
     */
    fun verifyForRememberMe(
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
        onError: (String) -> Unit
    ) {
        val biometricStatus = getBiometricStatus()

        when (biometricStatus) {
            is BiometricStatus.Available -> {
                showBiometricPrompt(onSuccess, onFailure, onError)
            }
            else -> {
                onError(biometricStatus.message)
            }
        }
    }

    /**
     * Show the biometric authentication prompt
     */
    private fun showBiometricPrompt(
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // Fingerprint verified successfully
                    onSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Single attempt failed, but user can try again
                    // Don't call onFailure here - let them retry
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                            // User cancelled the authentication
                            onFailure()
                        }
                        BiometricPrompt.ERROR_LOCKOUT,
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                            // Too many failed attempts
                            onError("Too many failed attempts. Please try again later.")
                        }
                        BiometricPrompt.ERROR_TIMEOUT -> {
                            // Timeout occurred
                            onError("Authentication timeout. Please try again.")
                        }
                        else -> {
                            // Other errors
                            onError(errString.toString())
                        }
                    }
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Enable Remember Me")
            .setSubtitle("Verify your identity")
            .setDescription("Touch the fingerprint sensor to save your login credentials securely")
            .setNegativeButtonText("Cancel")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .setConfirmationRequired(true)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    /**
     * Sealed class to represent biometric availability status
     */
    sealed class BiometricStatus(val message: String) {
        object Available : BiometricStatus("Biometric available")
        data class NoHardware(val error: String) : BiometricStatus(error)
        data class HardwareUnavailable(val error: String) : BiometricStatus(error)
        data class NoneEnrolled(val error: String) : BiometricStatus(error)
        data class SecurityUpdateRequired(val error: String) : BiometricStatus(error)
        data class Unsupported(val error: String) : BiometricStatus(error)
        data class Unknown(val error: String) : BiometricStatus(error)
        data class NotAvailable(val error: String) : BiometricStatus(error)
    }
}

