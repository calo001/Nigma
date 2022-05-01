package com.github.calo001.nigma.viewModel

import android.graphics.Bitmap
import androidx.compose.runtime.LaunchedEffect
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
import kotlin.NullPointerException

@HiltViewModel
class MainViewModel @Inject constructor(
    private val createUserInteractor: CreateUserInteractor,
    private val signInUserInteractor: SignInUserInteractor,
    private val currentSessionInteractor: CurrentSessionInteractor,
    private val logoutInteractor: LogoutInteractor,
    private val uploadPuzzleInteractor: UploadPuzzleInteractor,
    private val realtimePuzzlesInteractor: RealtimePuzzlesInteractor,
    private val puzzleResolvedInteractor: PuzzleResolvedInteractor,
    private val updateUserProfileInteractor: UpdateUserProfileInteractor,
): ViewModel() {
    private val _puzzleListState = MutableStateFlow<PuzzleListState>(PuzzleListState.Loading(emptyList()))
    val puzzleListState: StateFlow<PuzzleListState> get() = _puzzleListState

    private val _puzzleSelected = MutableStateFlow<PuzzleView?>(null)
    val puzzleSelected: StateFlow<PuzzleView?> get() = _puzzleSelected

    private val _signupStatus = MutableStateFlow<SignUpStatus>(SignUpStatus.Idle)
    val signupStatus: StateFlow<SignUpStatus> get() = _signupStatus

    private val _sessionStatus = MutableStateFlow<SessionStatus>(SessionStatus.Idle)
    val sessionStatus: StateFlow<SessionStatus> get() = _sessionStatus

    private val _completedPuzzles = MutableStateFlow<List<PuzzleView>>(emptyList())
    val completedPuzzles: StateFlow<List<PuzzleView>> get() = _completedPuzzles

    private val _puzzleCreator = MutableStateFlow<AddPuzzleStatus>(AddPuzzleStatus.Idle(Puzzle.default))
    val puzzleCreator: StateFlow<AddPuzzleStatus> get() = _puzzleCreator

    private val _snackErrorMessage = MutableStateFlow<String?>(null)
    val snackErrorMessage: StateFlow<String?> get() = _snackErrorMessage

    fun getPuzzleById(puzzle: PuzzleView) {
        _puzzleSelected.tryEmit(puzzle)
    }

    init {
        viewModelScope.launch {
            sessionStatus.collect {
                if (it is SessionStatus.SessionStarted) {
                    initDataSource()
                }
            }
        }
    }

    fun initDataSource() = viewModelScope.launch(Dispatchers.IO) {
        sessionStatus.collect { session ->
            if (session is SessionStatus.SessionStarted) {
                fetchPuzzleList()
                initRealtime()
            }
        }
    }

    private suspend fun initRealtime() {
        realtimePuzzlesInteractor.subscribeRealtime()
            .collect {
                fetchPuzzleList()
            }
    }

    private suspend fun fetchPuzzleList() {
        _puzzleListState.tryEmit(PuzzleListState.Loading(
            when (_puzzleListState.value) {
                PuzzleListState.Error -> emptyList()
                is PuzzleListState.Loading -> {
                    (_puzzleListState.value as PuzzleListState.Loading).list
                }
                is PuzzleListState.Success -> {
                    (_puzzleListState.value as PuzzleListState.Success).list
                }
            }
        ))
        val result = realtimePuzzlesInteractor.getPuzzleList()
        when {
            result.isSuccess -> {
                val newList = PuzzleListState.Success(result.getOrNull() ?: emptyList())
                val userId = getUserId()
                val (userPuzzles, mainPuzzlesList) = newList.list
                    .partition { it.resolvedBy.contains(userId) }
                _puzzleListState.tryEmit(PuzzleListState.Success(mainPuzzlesList))

                userId?.let {
                    _completedPuzzles.tryEmit(userPuzzles)
                }
            }
            result.isFailure -> {
                _puzzleListState.tryEmit(PuzzleListState.Error)
            }
        }
    }

    private suspend fun getUserId(): String? {
        return sessionStatus.firstOrNull()?.let { sessionStatus ->
            when (sessionStatus) {
                is SessionStatus.Error -> null
                SessionStatus.Idle -> null
                SessionStatus.Loading -> null
                SessionStatus.LoggedOut -> null
                SessionStatus.SignInSuccess -> null
                is SessionStatus.SessionStarted -> sessionStatus.user.id
                is SessionStatus.UpdatingSession -> sessionStatus.user?.id
            }
        }
    }

    fun createUser(username: String, email: String, password: String) = viewModelScope.launch {
        _signupStatus.tryEmit(SignUpStatus.Loading)
        val response = createUserInteractor.createUser(
            email = email,
            password = password,
            username = username
        )
        when {
            response.isFailure -> {
                _signupStatus.tryEmit(SignUpStatus.Error(response.exceptionOrNull() ?: NullPointerException()))
                _snackErrorMessage.tryEmit(
                    response.exceptionOrNull()?.message ?: "Error"
                )
            }
            response.isSuccess -> { _signupStatus.tryEmit(SignUpStatus.Success) }
        }
    }

    fun login(email: Email, password: Password) = viewModelScope.launch(Dispatchers.IO) {
        _sessionStatus.tryEmit(SessionStatus.Loading)
        val response = signInUserInteractor.login(email, password)
        when {
            response.isFailure -> {
                _sessionStatus.tryEmit(SessionStatus.Error(
                    email = email,
                    password = password,
                    error = response.exceptionOrNull() ?: NullPointerException()
                    )
                )
                _snackErrorMessage.tryEmit(
                    response.exceptionOrNull()?.message ?: "Error"
                )
            }
            response.isSuccess -> { _sessionStatus.tryEmit(SessionStatus.SignInSuccess) }
        }
    }

    fun getCurrentSession() = viewModelScope.launch(Dispatchers.IO) {
        loadUserInfo(false)
    }

    private suspend fun loadUserInfo(showMessageError: Boolean = true) {
        val response = currentSessionInteractor.currentSession()
        when {
            response.isSuccess -> {
                response.getOrNull()?.let { user ->
                    _sessionStatus.tryEmit(SessionStatus.SessionStarted(user))
                } ?: run {
                    _sessionStatus.tryEmit(SessionStatus.Error(
                        email = "",
                        password = "",
                        error = response.exceptionOrNull() ?: NullPointerException()
                    ))
                    if(showMessageError) {
                        _snackErrorMessage.tryEmit(
                            response.exceptionOrNull()?.message ?: "Error"
                        )
                    }
                }
            }
            response.isFailure -> {
                _sessionStatus.tryEmit(SessionStatus.Error(
                    email = "",
                    password = "",
                    error = response.exceptionOrNull() ?: NullPointerException()
                ))
                if(showMessageError) {
                    _snackErrorMessage.tryEmit(
                        response.exceptionOrNull()?.message ?: "Error"
                    )
                }
            }
        }
    }

    fun logout() = viewModelScope.launch(Dispatchers.IO) {
        val result = logoutInteractor.logout()
        when {
            result.isSuccess -> {
                cleanStates()
            }
            result.isFailure -> {
                _snackErrorMessage.tryEmit("Error, try again.")
            }
        }
    }

    private fun cleanStates() {
        _sessionStatus.tryEmit(SessionStatus.Idle)
        _puzzleCreator.tryEmit(AddPuzzleStatus.Building(Puzzle.default))
        _completedPuzzles.tryEmit(emptyList())
        _signupStatus.tryEmit(SignUpStatus.Idle)
        _puzzleSelected.tryEmit(null)
        _puzzleListState.tryEmit(PuzzleListState.Loading(emptyList()))
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
                    is SessionStatus.Error,
                    SessionStatus.Idle,
                    SessionStatus.Loading,
                    SessionStatus.LoggedOut,
                    SessionStatus.SignInSuccess -> "${System.currentTimeMillis()}.png"
                    is SessionStatus.SessionStarted -> "${session.user.id}.png"
                    is SessionStatus.UpdatingSession -> "${session.user?.id}.png"
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
                                    resultUpload.isFailure -> {
                                        _puzzleCreator.tryEmit(AddPuzzleStatus.Error(
                                            resultUpload.exceptionOrNull() ?: NullPointerException()
                                        ))
                                        _snackErrorMessage.tryEmit(
                                            resultUpload.exceptionOrNull()?.message ?: "Error"
                                        )
                                    }
                                    else -> {}
                                }
                            }
                        }
                        fileUpload.isFailure -> {
                            _puzzleCreator.tryEmit(AddPuzzleStatus.Error(
                                fileUpload.exceptionOrNull() ?: NullPointerException()
                            ))
                            _snackErrorMessage.tryEmit(
                                fileUpload.exceptionOrNull()?.message ?: "Error"
                            )
                        }
                    }
                }
        }
    }

    fun resetPuzzleCreator() {
        _puzzleCreator.tryEmit(AddPuzzleStatus.Idle(Puzzle.default))
    }

    fun onPuzzleResolved(puzzleView: PuzzleView) = viewModelScope.launch(Dispatchers.IO) {
        _sessionStatus.firstOrNull()?.let { session ->
            when (session) {
                is SessionStatus.Error,
                SessionStatus.Idle,
                SessionStatus.Loading,
                SessionStatus.LoggedOut,
                SessionStatus.SignInSuccess -> Unit
                is SessionStatus.UpdatingSession -> Unit
                is SessionStatus.SessionStarted -> {
                    puzzleResolvedInteractor.updatePuzzleItem(
                        puzzleView = puzzleView,
                        userId = session.user.id,
                    )
                }
            }
        }
    }

    fun updateProfileName(name: String) = viewModelScope.launch(Dispatchers.IO) {
        sessionStatus.firstOrNull()?.let { session ->
            if(session is SessionStatus.SessionStarted) {
                _sessionStatus.tryEmit(
                    SessionStatus.UpdatingSession(
                        when (sessionStatus.value) {
                            is SessionStatus.Error,
                            SessionStatus.Idle,
                            SessionStatus.Loading,
                            SessionStatus.LoggedOut,
                            SessionStatus.SignInSuccess -> null
                            is SessionStatus.UpdatingSession -> (sessionStatus.value as SessionStatus.UpdatingSession).user
                            is SessionStatus.SessionStarted -> (sessionStatus.value as SessionStatus.SessionStarted).user
                        }
                    )
                )

                val resultUpdate = updateUserProfileInteractor.updateProfileInfo(
                    session.user.copy(username = name)
                )
                when {
                    resultUpdate.isSuccess -> {
                        loadUserInfo()
                    }
                    resultUpdate.isFailure -> {}
                }
            }
        }
    }

    fun updateProfileImage(bitmap: Bitmap) = viewModelScope.launch(Dispatchers.IO) {
        sessionStatus.firstOrNull()?.let { session ->
            if(session is SessionStatus.SessionStarted) {
                _sessionStatus.tryEmit(SessionStatus.UpdatingSession(
                    when (sessionStatus.value) {
                        is SessionStatus.Error,
                        SessionStatus.Idle,
                        SessionStatus.Loading,
                        SessionStatus.LoggedOut,
                        SessionStatus.SignInSuccess -> null
                        is SessionStatus.UpdatingSession -> (sessionStatus.value as SessionStatus.UpdatingSession).user
                        is SessionStatus.SessionStarted -> (sessionStatus.value as SessionStatus.SessionStarted).user
                    }
                ))

                val result = updateUserProfileInteractor.uploadImageProfile(
                    bitmap = bitmap,
                    userInfo = session.user
                )

                when {
                    result.isSuccess && result.getOrNull() != null -> {
                        val resultUpdate = updateUserProfileInteractor.updateProfileInfo(
                            session.user.copy(imageProfileFileId = result.getOrNull()?.id ?: "")
                        )
                        when {
                            resultUpdate.isSuccess -> {
                                loadUserInfo()
                            }
                            resultUpdate.isFailure -> {}
                        }
                    }
                    result.isFailure -> Unit
                }
            }
        }
    }

    fun resetSignupStatus() {
        _signupStatus.tryEmit(SignUpStatus.Idle)
    }

    fun cleanErrorMessage() {
        _snackErrorMessage.tryEmit(null)
    }
}