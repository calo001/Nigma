package com.github.calo001.nigma.interactor

import com.github.calo001.nigma.repository.RemoteUserRepository
import io.appwrite.models.User
import javax.inject.Inject

class CurrentSessionInteractor @Inject constructor(private val repository: RemoteUserRepository) {
    suspend fun currentSession() = repository.getCurrentSession()
}