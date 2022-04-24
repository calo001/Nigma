package com.github.calo001.nigma.router

import androidx.navigation.NavHostController
import com.github.calo001.nigma.view.Screen

class Navigator(
    private val navHostController: NavHostController,
) {
    fun navigate(screen: Screen) {
        navHostController.navigate(screen.route) {
            launchSingleTop = true
        }
    }

    fun navigateHome() {
        navHostController.navigate(Screen.Main.route) {
            popUpTo(Screen.Main.route)
            launchSingleTop = true
        }
    }
}

val Screen.shouldShowNavigator get() =
    when(this) {
        Screen.Main,
        Screen.Profile -> true
        Screen.PuzzleResolver,
        Screen.AddPuzzle,
        Screen.Unknown,
        Screen.SignIn,
        Screen.Signup -> false
    }

val Screen.shouldShowAddFab get() = this == Screen.AddPuzzle