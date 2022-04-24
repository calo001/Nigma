package com.github.calo001.nigma.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.calo001.nigma.interactor.CreateUserInteractor
import com.github.calo001.nigma.interactor.CurrentSessionInteractor
import com.github.calo001.nigma.interactor.LogoutInteractor
import com.github.calo001.nigma.interactor.SignInUserInteractor
import com.github.calo001.nigma.ui.model.PuzzleView
import com.github.calo001.nigma.ui.signup.Email
import com.github.calo001.nigma.ui.signup.Password
import com.github.calo001.nigma.ui.states.PuzzleListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val createUserInteractor: CreateUserInteractor,
    private val signInUserInteractor: SignInUserInteractor,
    private val currentSessionInteractor: CurrentSessionInteractor,
    private val logoutInteractor: LogoutInteractor,
): ViewModel() {
    private val _puzzleListState = MutableStateFlow<PuzzleListState>(PuzzleListState.Loading)
    val puzzleListState: StateFlow<PuzzleListState> get() = _puzzleListState

    private val _puzzleSelected = MutableStateFlow<PuzzleView?>(null)
    val puzzleSelected: StateFlow<PuzzleView?> get() = _puzzleSelected

    private val _signupStatus = MutableStateFlow<SignUpStatus>(SignUpStatus.Idle)
    val signupStatus: StateFlow<SignUpStatus> get() = _signupStatus

    private val _sessionStatus = MutableStateFlow<SessionStatus>(SessionStatus.Idle)
    val sessionStatus: StateFlow<SessionStatus> get() = _sessionStatus

    init {
        _puzzleListState.tryEmit(PuzzleListState.Success(listPuzzles))
    }

    fun getPuzzleById(puzzle: PuzzleView) {
        _puzzleSelected.tryEmit(puzzle)
    }

    fun createUser(username: String, email: String, password: String) = viewModelScope.launch {
        _signupStatus.tryEmit(SignUpStatus.Loading)
        val response = createUserInteractor.createUser(email, password, username)
        when {
            response.isFailure -> { _signupStatus.tryEmit(SignUpStatus.Error) }
            response.isSuccess -> { _signupStatus.tryEmit(SignUpStatus.Success) }
        }
    }

    fun login(email: Email, password: Password) = viewModelScope.launch {
        _sessionStatus.tryEmit(SessionStatus.Loading)
        val response = signInUserInteractor.login(email, password)
        when {
            response.isFailure -> { _sessionStatus.tryEmit(SessionStatus.Error) }
            response.isSuccess -> { _sessionStatus.tryEmit(SessionStatus.SignInSuccess) }
        }
    }

    fun getCurrentSession() = viewModelScope.launch {
        val response = currentSessionInteractor.currentSession()
        when {
            response.isSuccess -> {
                response.getOrNull()?.let { user ->
                    _sessionStatus.tryEmit(SessionStatus.SessionStarted(user))
                } ?: run {
                    _sessionStatus.tryEmit(SessionStatus.Error)
                }
            }
            response.isFailure -> { _sessionStatus.tryEmit(SessionStatus.Error) }
        }
    }

    fun logout() = viewModelScope.launch {
        val result = logoutInteractor.logout()
        when {
            result.isSuccess -> { _sessionStatus.tryEmit(SessionStatus.Idle) }
            result.isFailure -> Unit
        }
    }

    fun successLogin() {
        _sessionStatus.tryEmit(SessionStatus.Idle)
    }
}

val listPuzzles = listOf(
    PuzzleView(
        id = "001",
        username = "Pepe",
        userImageProfileUrl = "https://images.unsplash.com/photo-1633332755192-727a05c4013d?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8dXNlcnxlbnwwfHwwfHw%3D&w=1000&q=80",
        puzzleImageUrl = "https://pbs.twimg.com/profile_images/1364932022926458886/BxwXy9N8_400x400.jpg",
        gridSize = 3,
        puzzleName = "Name 01"
    ),
    PuzzleView(
        id = "002",
        username = "Pepe",
        userImageProfileUrl = "https://images.unsplash.com/photo-1633332755192-727a05c4013d?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8dXNlcnxlbnwwfHwwfHw%3D&w=1000&q=80",
        puzzleImageUrl = "https://images.unsplash.com/photo-1633332755192-727a05c4013d?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8dXNlcnxlbnwwfHwwfHw%3D&w=1000&q=80",
        gridSize = 3,
        puzzleName = "Name 02"
    )
)