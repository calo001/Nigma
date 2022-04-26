package com.github.calo001.nigma.interactor

import com.github.calo001.nigma.repository.RemoteUserRepository
import javax.inject.Inject

class RealtimePuzzles @Inject constructor(private val repository: RemoteUserRepository) {
    suspend fun subscribeRealtime() = repository.subscribeRealtime()

    suspend fun getPuzzleList() = repository.getAllPuzzles()
}