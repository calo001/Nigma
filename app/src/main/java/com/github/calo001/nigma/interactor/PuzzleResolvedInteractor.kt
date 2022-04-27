package com.github.calo001.nigma.interactor

import com.github.calo001.nigma.repository.RemoteUserRepository
import com.github.calo001.nigma.ui.model.PuzzleView
import javax.inject.Inject

class PuzzleResolvedInteractor @Inject constructor(private val repository: RemoteUserRepository) {
    suspend fun updatePuzzleItem(puzzleView: PuzzleView, userId: String) = repository.updatePuzzleItem(puzzleView, userId)
}