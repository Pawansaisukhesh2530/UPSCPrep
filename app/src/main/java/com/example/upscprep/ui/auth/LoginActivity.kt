package com.example.upscprep.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.upscprep.MainActivity
import com.example.upscprep.databinding.ActivityLoginBinding

/**
 * Login Activity for user authentication
 * Uses ViewBinding and MVVM pattern with LoginViewModel
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupClickListeners()
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
}

