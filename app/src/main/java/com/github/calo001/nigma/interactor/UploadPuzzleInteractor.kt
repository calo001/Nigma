package com.github.calo001.nigma.interactor

import com.github.calo001.nigma.repository.RemoteUserRepository
import com.github.calo001.nigma.viewModel.Puzzle
import javax.inject.Inject

class UploadPuzzleInteractor @Inject constructor(private val repository: RemoteUserRepository) {
    suspend fun uploadPuzzleImage(puzzle: Puzzle): Result<io.appwrite.models.File> {
        return repository.uploadPuzzleImage(puzzle)
    }

    suspend fun savePuzzle(puzzle: Puzzle) = repository.savePuzzle(puzzle)
}