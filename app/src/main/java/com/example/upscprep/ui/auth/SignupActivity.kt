package com.example.upscprep.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.upscprep.databinding.ActivitySignupBinding

/**
 * Signup Activity for user registration
 * Uses ViewBinding and MVVM pattern with LoginViewModel
 */
class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupClickListeners()
    }

    /**
     * Sets up observers for LiveData from ViewModel
     */
    private fun setupObservers() {
        viewModel.signupState.observe(this) { state ->
            when (state) {
                is LoginViewModel.SignupState.Loading -> {
                    // Show loading UI
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSignup.isEnabled = false
                }

                is LoginViewModel.SignupState.Success -> {
                    // Hide loading UI
                    binding.progressBar.visibility = View.GONE
                    binding.btnSignup.isEnabled = true

                    // Show success message
                    Toast.makeText(
                        this,
                        "Account created successfully!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Return to login screen
                    finish()
                }

                is LoginViewModel.SignupState.Error -> {
                    // Hide loading UI
                    binding.progressBar.visibility = View.GONE
                    binding.btnSignup.isEnabled = true

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
        // Signup button click
        binding.btnSignup.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            viewModel.signup(name, email, password, confirmPassword)
        }

        // Login text click
        binding.tvLogin.setOnClickListener {
            finish()
        }
    }
}

