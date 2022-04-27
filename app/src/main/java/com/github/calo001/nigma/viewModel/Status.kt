package com.github.calo001.nigma.viewModel

import android.graphics.Bitmap
import android.net.Uri
import io.appwrite.models.User

sealed interface SignUpStatus {
    object Idle: SignUpStatus
    object Loading: SignUpStatus
    object Success: SignUpStatus
    object Error: SignUpStatus
}

sealed interface SessionStatus {
    object Idle: SessionStatus
    object Loading: SessionStatus
    object SignInSuccess: SessionStatus
    class SessionStarted(val user: User): SessionStatus
    object LoggedOut: SessionStatus
    object Error: SessionStatus
}

sealed interface AddPuzzleStatus {
    class  Building(val puzzle: Puzzle): AddPuzzleStatus
    class  Uploading(val puzzle: Puzzle): AddPuzzleStatus
    object Success: AddPuzzleStatus
    object Error: AddPuzzleStatus
}

data class Puzzle(
    val id: String,
    val imgBitmap: Bitmap?,
    val fileName: String,
    val name: String,
    val description: String,
    val fileId: String? = null
) {
    companion object {
        val default get() = Puzzle("1", imgBitmap = null, "photo.png", "Puzzle name", "Description")
    }
}