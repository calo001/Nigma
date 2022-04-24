package com.github.calo001.nigma.repository

import android.util.Log
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.Session
import io.appwrite.models.User
import io.appwrite.services.Account
import javax.inject.Inject

class RemoteUserRepository @Inject constructor(private val account: Account) {
    suspend fun createUser(username: String, email: String, password: String): Result<User> {
        return try {
            val user = account.create("unique()", email, password, username)
            Result.success(user)
        } catch (e: AppwriteException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<Session> {
        return try {
            val result = account.createSession(email, password)
            Result.success(result)
        } catch (e: AppwriteException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentSession(): Result<User> {
        return try {
            val currentSession = account.get()
            Result.success(currentSession)
        } catch (e: AppwriteException) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            account.deleteSessions()
            Result.success(Unit)
        } catch (e: AppwriteException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}