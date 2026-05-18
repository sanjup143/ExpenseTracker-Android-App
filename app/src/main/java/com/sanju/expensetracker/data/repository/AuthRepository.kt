package com.sanju.expensetracker.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun loginUser(
        email: String,
        password: String
    ): Result<String> {

        return try {

            firebaseAuth.signInWithEmailAndPassword(email, password).await()

            Result.success("Login Successful")

        } catch (e: Exception) {

            Result.failure(e)

        }
    }

    suspend fun registerUser(
        email: String,
        password: String
    ): Result<String> {

        return try {

            firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            Result.success("Registration Successful")

        } catch (e: Exception) {

            Result.failure(e)

        }
    }
}