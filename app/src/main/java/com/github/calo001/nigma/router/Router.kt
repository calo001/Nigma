package com.github.calo001.nigma.router

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.calo001.nigma.ui.add.AddScreen
import com.github.calo001.nigma.ui.main.MainScreen
import com.github.calo001.nigma.ui.main.listPuzzles
import com.github.calo001.nigma.ui.model.PuzzleView
import com.github.calo001.nigma.ui.profile.ProfileScreen
import com.github.calo001.nigma.ui.resolver.PuzzleResolver
import com.github.calo001.nigma.ui.signing.SingInScreen
import com.github.calo001.nigma.ui.signup.SingUpScreen
import com.github.calo001.nigma.view.Screen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Router(
    navController: NavHostController,
    startDestination: String,
    onNavigate: (Screen) -> Unit,
) {
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
            Column(modifier = Modifier.fillMaxSize()) {
                MainScreen(
                    puzzles = listPuzzles,
                    username = "Toño",
                    onNavigate = onNavigate,
                )
            }
        }

        composable(
            route = Screen.Signing.route,
            enterTransition = defaultEnter,
            exitTransition = defaultExit,
            popEnterTransition = defaultPopEnter,
            popExitTransition = defaultPopExit,
        ) {
            SingInScreen(
                onNavigate = onNavigate,
                modifier = Modifier.padding(16.dp)
            )
        }

        composable(
            route = Screen.Signup.route,
            enterTransition = defaultEnter,
            exitTransition = defaultExit,
            popEnterTransition = defaultPopEnter,
            popExitTransition = defaultPopExit,
        ) {
            SingUpScreen(
                onNavigate = onNavigate,
                modifier = Modifier.padding(16.dp)
            )
        }

        composable(
            route = Screen.AddPuzzle.route,
            enterTransition = defaultEnter,
            exitTransition = defaultExit,
            popEnterTransition = defaultPopEnter,
            popExitTransition = defaultPopExit,
        ) {
            AddScreen(
                modifier = Modifier.padding(16.dp),
                onNavigate = onNavigate,
            )
        }

        composable(
            route = Screen.Profile.route,
            enterTransition = defaultEnter,
            exitTransition = defaultExit,
            popEnterTransition = defaultPopEnter,
            popExitTransition = defaultPopExit,
        ) {
            ProfileScreen()
        }

        composable(
            route = Screen.PuzzleResolver.route,
            enterTransition = defaultEnter,
            exitTransition = defaultExit,
            popEnterTransition = defaultPopEnter,
            popExitTransition = defaultPopExit,
        ) {
            PuzzleResolver(
                puzzleView = PuzzleView(
                    userImageProfileUrl = "",
                    gridSize = 2,
                    puzzleName = "",
                    puzzleImageUrl = "https://media-cdn.tripadvisor.com/media/photo-s/18/1c/30/b4/photo1jpg.jpg",
                    username = "",
                ),
                onNavigate = onNavigate
            )
        }
    }
}