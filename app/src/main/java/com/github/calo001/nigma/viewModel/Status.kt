package com.github.calo001.nigma.viewModel

import android.graphics.Bitmap
import android.net.Uri
import com.github.calo001.nigma.repository.model.UserInfo
import io.appwrite.models.User

sealed interface SignUpStatus {
    object Idle: SignUpStatus
    object Loading: SignUpStatus
    object Success: SignUpStatus
    class Error(val error: Throwable): SignUpStatus
}

sealed interface SessionStatus {
    object Idle: SessionStatus
    object Loading: SessionStatus
    object SignInSuccess: SessionStatus
    class SessionStarted(val user: UserInfo): SessionStatus
    class UpdatingSession(val user: UserInfo?): SessionStatus
    object LoggedOut: SessionStatus
    class Error(val email: String, val password: String, val error: Throwable): SessionStatus
}

sealed interface AddPuzzleStatus {
    class  Idle(val puzzle: Puzzle): AddPuzzleStatus
    class  Building(val puzzle: Puzzle): AddPuzzleStatus
    class  Uploading(val puzzle: Puzzle): AddPuzzleStatus
    object Success: AddPuzzleStatus
    class Error(val error: Throwable): AddPuzzleStatus
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