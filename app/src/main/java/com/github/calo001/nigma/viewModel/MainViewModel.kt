package com.github.calo001.nigma.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.calo001.nigma.interactor.*
import com.github.calo001.nigma.ui.model.PuzzleView
import com.github.calo001.nigma.ui.signup.Email
import com.github.calo001.nigma.ui.signup.Password
import com.github.calo001.nigma.ui.states.PuzzleListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val createUserInteractor: CreateUserInteractor,
    private val signInUserInteractor: SignInUserInteractor,
    private val currentSessionInteractor: CurrentSessionInteractor,
    private val logoutInteractor: LogoutInteractor,
    private val uploadPuzzleInteractor: UploadPuzzleInteractor,
): ViewModel() {
    private val _puzzleListState = MutableStateFlow<PuzzleListState>(PuzzleListState.Loading)
    val puzzleListState: StateFlow<PuzzleListState> get() = _puzzleListState

    private val _puzzleSelected = MutableStateFlow<PuzzleView?>(null)
    val puzzleSelected: StateFlow<PuzzleView?> get() = _puzzleSelected

    private val _signupStatus = MutableStateFlow<SignUpStatus>(SignUpStatus.Idle)
    val signupStatus: StateFlow<SignUpStatus> get() = _signupStatus

    private val _sessionStatus = MutableStateFlow<SessionStatus>(SessionStatus.Idle)
    val sessionStatus: StateFlow<SessionStatus> get() = _sessionStatus

    private val _puzzleCreator = MutableStateFlow<AddPuzzleStatus>(AddPuzzleStatus.Building(Puzzle.default))
    val puzzleCreator: StateFlow<AddPuzzleStatus> get() = _puzzleCreator

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

    fun login(email: Email, password: Password) = viewModelScope.launch(Dispatchers.IO) {
        _sessionStatus.tryEmit(SessionStatus.Loading)
        val response = signInUserInteractor.login(email, password)
        when {
            response.isFailure -> { _sessionStatus.tryEmit(SessionStatus.Error) }
            response.isSuccess -> { _sessionStatus.tryEmit(SessionStatus.SignInSuccess) }
        }
    }

    fun getCurrentSession() = viewModelScope.launch(Dispatchers.IO) {
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

    fun logout() = viewModelScope.launch(Dispatchers.IO) {
        val result = logoutInteractor.logout()
        when {
            result.isSuccess -> { _sessionStatus.tryEmit(SessionStatus.Idle) }
            result.isFailure -> Unit
        }
    }

    fun updatePuzzleCreator(puzzle: Puzzle) {
        _puzzleCreator.tryEmit(AddPuzzleStatus.Building(puzzle))
    }

    fun uploadPuzzle() = viewModelScope.launch(Dispatchers.IO) {
        _sessionStatus
            .combine(_puzzleCreator) { session, puzzle ->
            Pair(session, puzzle)
        }.firstOrNull()?.let { combination ->
                val session = combination.first
                val puzzle = combination.second

                val nameFile = when (session) {
                    SessionStatus.Error,
                    SessionStatus.Idle,
                    SessionStatus.Loading,
                    SessionStatus.LoggedOut,
                    SessionStatus.SignInSuccess -> "${System.currentTimeMillis()}.png"
                    is SessionStatus.SessionStarted -> "${session.user.id}.png"
                }

                if (puzzle is AddPuzzleStatus.Building) {
                    _puzzleCreator.tryEmit(AddPuzzleStatus.Uploading(puzzle.puzzle))
                    val fileUpload = uploadPuzzleInteractor.uploadPuzzleImage(
                        puzzle.puzzle.copy(fileName = puzzle.puzzle.fileName + nameFile)
                    )

                    when {
                        fileUpload.isSuccess -> {
                            fileUpload.getOrNull()?.let { uploadedFile ->
                                val resultUpload = uploadPuzzleInteractor.savePuzzle(puzzle.puzzle.copy(fileId = uploadedFile.id))
                                when {
                                    resultUpload.isSuccess -> {
                                        _puzzleCreator.tryEmit(AddPuzzleStatus.Success)
                                    }
                                    resultUpload.isFailure -> { _puzzleCreator.tryEmit(AddPuzzleStatus.Error) }
                                    else -> {}
                                }
                            }
                        }
                        fileUpload.isFailure -> {
                            _puzzleCreator.tryEmit(AddPuzzleStatus.Error)
                        }
                    }
                }
        }
    }

    fun resetPuzzleCreator() {
        _puzzleCreator.tryEmit(AddPuzzleStatus.Building(Puzzle.default))
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