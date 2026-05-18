package com.sanju.expensetracker.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sanju.expensetracker.R
import com.sanju.expensetracker.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            registerUser()
        }

        observeAuthState()
    }

    private fun registerUser() {

        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty()) {
            binding.etEmail.error = getString(R.string.email_required_caps)
            return
        }

        if (password.isEmpty()) {
            binding.etPassword.error = getString(R.string.password_required_caps)
            return
        }

        authViewModel.registerUser(email, password)
    }

    private fun observeAuthState() {

        lifecycleScope.launch {

            authViewModel.authState.collect { state ->

                when (state) {

                    is AuthState.Loading -> {
                        binding.btnRegister.text = getString(R.string.please_wait)
                        binding.btnRegister.isEnabled = false
                    }

                    is AuthState.Success -> {

                        binding.btnRegister.text = getString(R.string.register)
                        binding.btnRegister.isEnabled = true

                        Toast.makeText(
                            this@RegisterActivity,
                            state.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    }

                    is AuthState.Error -> {

                        binding.btnRegister.text = getString(R.string.register)
                        binding.btnRegister.isEnabled = true

                        Toast.makeText(
                            this@RegisterActivity,
                            state.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    else -> Unit
                }
            }
        }
    }
}