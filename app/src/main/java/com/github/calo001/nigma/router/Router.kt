package com.github.calo001.nigma.router

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.github.calo001.nigma.ui.add.AddScreen
import com.github.calo001.nigma.ui.main.MainScreen
import com.github.calo001.nigma.ui.profile.ProfileScreen
import com.github.calo001.nigma.ui.resolver.PuzzleResolver
import com.github.calo001.nigma.ui.signing.SingInScreen
import com.github.calo001.nigma.ui.signup.SingUpScreen
import com.github.calo001.nigma.view.Screen
import com.github.calo001.nigma.viewModel.AddPuzzleStatus
import com.github.calo001.nigma.viewModel.MainViewModel
import com.github.calo001.nigma.viewModel.Puzzle
import com.github.calo001.nigma.viewModel.SessionStatus
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@ExperimentalFoundationApi
@OptIn(ExperimentalAnimationApi::class, ExperimentalPermissionsApi::class)
@Composable
fun Router(
    navController: NavHostController,
    startDestination: String,
    onNavigate: (Screen) -> Unit,
    viewModel: MainViewModel,
) {
    val state = rememberLazyListState()
    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(
            route = Screen.Main.route,
            enterTransition = defaultEnter,
            exitTransition = defaultExit,
            popEnterTransition = defaultPopEnter,
            popExitTransition = defaultPopExit,
        ) {
            val listPuzzles by viewModel.puzzleListState.collectAsState()
            val sessionStatus by viewModel.sessionStatus.collectAsState()

            when (sessionStatus) {
                SessionStatus.Error -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("error")
                    }
                }
                is SessionStatus.SessionStarted -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        MainScreen(
                            puzzleListState = listPuzzles,
                            username = (sessionStatus as SessionStatus.SessionStarted).user.name,
                            onNavigate = onNavigate,
                            userId = (sessionStatus as SessionStatus.SessionStarted).user.id,
                            state = state,
                            onClickPuzzle = { puzzle ->
                                viewModel.getPuzzleById(puzzle)
                                onNavigate(Screen.PuzzleResolver)
                            }
                        )
                    }
                }
                SessionStatus.Loading,
                SessionStatus.Idle,
                SessionStatus.LoggedOut,
                SessionStatus.SignInSuccess -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        composable(
            route = Screen.SignIn.route,
            enterTransition = defaultEnter,
            exitTransition = defaultExit,
            popEnterTransition = defaultPopEnter,
            popExitTransition = defaultPopExit,
        ) {
            val status by viewModel.sessionStatus.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.getCurrentSession()
            }

            when (status) {
                SessionStatus.Idle,
                SessionStatus.Loading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                }
                SessionStatus.Error,
                SessionStatus.LoggedOut -> {
                    SingInScreen(
                        onNavigate = onNavigate,
                        status = status,
                        modifier = Modifier.padding(16.dp),
                        onSignInRequest = { email, password ->
                            viewModel.login(email, password)
                        }
                    )
                }
                SessionStatus.SignInSuccess -> {
                    LaunchedEffect(status) {
                        viewModel.getCurrentSession()
                    }
                }
                is SessionStatus.SessionStarted -> {
                    onNavigate(Screen.Main)
                }
            }
        }

        composable(
            route = Screen.Signup.route,
            enterTransition = defaultEnter,
            exitTransition = defaultExit,
            popEnterTransition = defaultPopEnter,
            popExitTransition = defaultPopExit,
        ) {
            val status by viewModel.signupStatus.collectAsState()
            SingUpScreen(
                onNavigate = onNavigate,
                status = status,
                onSignupRequest = { email, password, username ->
                    viewModel.createUser(email, password, username)
                },
                modifier = Modifier.padding(16.dp),
            )

            LaunchedEffect(key1 = status) {
                onNavigate(Screen.SignIn)
            }

        }

        composable(
            route = Screen.AddPuzzle.route,
            enterTransition = defaultEnter,
            exitTransition = defaultExit,
            popEnterTransition = defaultPopEnter,
            popExitTransition = defaultPopExit,
        ) {
            val puzzleStatus by viewModel.puzzleCreator.collectAsState()
            val puzzle = when (puzzleStatus) {
                is AddPuzzleStatus.Building -> (puzzleStatus as AddPuzzleStatus.Building).puzzle
                AddPuzzleStatus.Success -> Puzzle.default
                is AddPuzzleStatus.Uploading -> (puzzleStatus as AddPuzzleStatus.Uploading).puzzle
                AddPuzzleStatus.Error -> Puzzle.default
            }

            val externalStoragePermissionState = rememberPermissionState(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

            when (externalStoragePermissionState.status) {
                PermissionStatus.Granted -> AddScreen(
                    puzzleStatus = puzzleStatus,
                    onNavigate = onNavigate,
                    puzzleName = puzzle.name,
                    puzzleDescription = puzzle.description,
                    bitmap = puzzle.imgBitmap,
                    onImageCaptured = { bitmap, fileName ->
                        viewModel.updatePuzzleCreator(puzzle.copy(imgBitmap = bitmap, fileName = fileName))
                    },
                    onNameChange = { name ->
                        viewModel.updatePuzzleCreator(puzzle.copy(name = name))
                    },
                    onDescriptionChange = { description ->
                        viewModel.updatePuzzleCreator(puzzle.copy(description = description))
                    },
                    modifier = Modifier.padding(16.dp),
                    onReset = {
                        viewModel.resetPuzzleCreator()
                    }
                )
                is PermissionStatus.Denied -> {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {

                        val composition by rememberLottieComposition(
                            LottieCompositionSpec.RawRes(
                                com.github.calo001.nigma.R.raw.upload_files
                            )
                        )
                        LottieAnimation(
                            composition = composition,
                            modifier = Modifier.size(300.dp)
                        )

                        Button(onClick = {
                            onNavigate(Screen.Main)
                        }) {
                            Text(text = "Don't allow access files")
                        }

                        Button(onClick = {
                            externalStoragePermissionState.launchPermissionRequest()
                        }) {
                            Text(text = "Allow access files")
                        }
                    }
                }
            }


        }

        composable(
            route = Screen.Profile.route,
            enterTransition = defaultEnter,
            exitTransition = defaultExit,
            popEnterTransition = defaultPopEnter,
            popExitTransition = defaultPopExit,
        ) {
            val sessionStatus by viewModel.sessionStatus.collectAsState()

            LaunchedEffect(key1 = sessionStatus) {
                when (sessionStatus) {
                    is SessionStatus.SessionStarted -> Unit
                    SessionStatus.Loading -> Unit
                    SessionStatus.SignInSuccess -> Unit
                    SessionStatus.Error,
                    SessionStatus.Idle,
                    SessionStatus.LoggedOut -> {
                        onNavigate(Screen.SignIn)
                    }
                }
            }

            if (sessionStatus is SessionStatus.SessionStarted) {
                ProfileScreen(
                    sessionInfo = (sessionStatus as SessionStatus.SessionStarted).user,
                    onLogout = {
                        viewModel.logout()
                    }
                )
            }
        }

        composable(
            route = Screen.PuzzleResolver.route,
            enterTransition = defaultEnter,
            exitTransition = defaultExit,
            popEnterTransition = defaultPopEnter,
            popExitTransition = defaultPopExit,
        ) {
            val puzzle by viewModel.puzzleSelected.collectAsState()
            puzzle?.let { puzzleView ->
                PuzzleResolver(
                    puzzleView = puzzleView,
                    onNavigate = onNavigate,
                    onPuzzleResolved = {
                        viewModel.onPuzzleResolved(puzzleView)
                    }
                )
            }
        }
    }
}