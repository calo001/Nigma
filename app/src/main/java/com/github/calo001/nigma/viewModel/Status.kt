package com.github.calo001.nigma.viewModel

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

