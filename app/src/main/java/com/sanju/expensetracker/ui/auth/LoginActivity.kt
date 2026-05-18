package com.sanju.expensetracker.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sanju.expensetracker.R
import com.sanju.expensetracker.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeAuthState()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        binding.tvRegister.setOnClickListener {
            startActivity(
                android.content.Intent(
                    this,
                    RegisterActivity::class.java
                )
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

                        startActivity(
                            android.content.Intent(
                                this@LoginActivity,
                                com.sanju.expensetracker.ui.home.HomeActivity::class.java
                            )
                        )
                        finish()
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
}