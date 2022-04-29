package com.github.calo001.nigma.interactor

import com.github.calo001.nigma.repository.RemoteUserRepository
import io.appwrite.models.User
import javax.inject.Inject

class CreateUserInteractor @Inject constructor(private val repository: RemoteUserRepository){
    suspend fun createUser(username: String, email: String, password: String): Result<User> {
        return repository.createUser(
            username = username,
            email = email,
            password = password
        )
    }
}