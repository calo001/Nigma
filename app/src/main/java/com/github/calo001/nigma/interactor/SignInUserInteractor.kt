package com.github.calo001.nigma.interactor

import com.github.calo001.nigma.repository.RemoteUserRepository
import io.appwrite.models.Session
import javax.inject.Inject

class SignInUserInteractor @Inject constructor(private val repository: RemoteUserRepository) {
    suspend fun login(email: String, password: String): Result<Session> {
        return repository.login(email, password)
    }
}