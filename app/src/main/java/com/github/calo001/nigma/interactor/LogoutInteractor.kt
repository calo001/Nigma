package com.github.calo001.nigma.interactor

import com.github.calo001.nigma.repository.RemoteUserRepository
import javax.inject.Inject

class LogoutInteractor @Inject constructor(private val repository: RemoteUserRepository) {
    suspend fun logout() = repository.logout()
}