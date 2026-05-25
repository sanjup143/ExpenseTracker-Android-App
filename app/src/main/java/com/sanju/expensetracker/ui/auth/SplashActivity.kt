package com.sanju.expensetracker.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.sanju.expensetracker.ui.home.HomeActivity
import com.sanju.expensetracker.utils.BiometricHelper

class SplashActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkLoginSession()
    }

    private fun checkLoginSession() {
        if (firebaseAuth.currentUser != null) {
            showBiometricOrOpenHome()
        } else {
            openLoginScreen()
        }
    }

    private fun showBiometricOrOpenHome() {
        if (BiometricHelper.isBiometricAvailable(this)) {
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

                    openLoginScreen()
                }
            )
        } else {
            openHomeScreen()
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

    private fun openLoginScreen() {
        startActivity(
            Intent(
                this,
                LoginActivity::class.java
            )
        )

        finish()
    }
}