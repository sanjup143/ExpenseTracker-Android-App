package com.sanju.expensetracker.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.sanju.expensetracker.R
import com.sanju.expensetracker.databinding.ActivityLoginBinding
import com.sanju.expensetracker.ui.home.HomeActivity
import com.sanju.expensetracker.utils.BiometricHelper
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val authViewModel: AuthViewModel by viewModels()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeAuthState()
        setupBiometricLogin()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        binding.tvRegister.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        }
    }

    private fun setupBiometricLogin() {
        val currentUser = firebaseAuth.currentUser

        if (
            currentUser != null &&
            BiometricHelper.isBiometricAvailable(this)
        ) {
            BiometricHelper.showBiometricPrompt(
                activity = this,
                onSuccess = {
                    openHomeScreen()
                },
                onError = { message ->
                    Toast.makeText(
                        this,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty()) {
            binding.etEmail.error = getString(R.string.email_required)
            return
        }

        if (password.isEmpty()) {
            binding.etPassword.error = getString(R.string.password_required)
            return
        }

        authViewModel.loginUser(email, password)
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            authViewModel.authState.collect { state ->

                when (state) {
                    is AuthState.Idle -> {
                        binding.btnLogin.isEnabled = true
                        binding.btnLogin.text = getString(R.string.login)
                    }

                    is AuthState.Loading -> {
                        binding.btnLogin.isEnabled = false
                        binding.btnLogin.text = getString(R.string.please_wait)
                    }

                    is AuthState.Success -> {
                        binding.btnLogin.isEnabled = true
                        binding.btnLogin.text = getString(R.string.login)

                        Toast.makeText(
                            this@LoginActivity,
                            state.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        openHomeScreen()
                    }

                    is AuthState.Error -> {
                        binding.btnLogin.isEnabled = true
                        binding.btnLogin.text = getString(R.string.login)

                        Toast.makeText(
                            this@LoginActivity,
                            state.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun openHomeScreen() {
        startActivity(
            Intent(
                this,
                HomeActivity::class.java
            )
        )

        finish()
    }
}