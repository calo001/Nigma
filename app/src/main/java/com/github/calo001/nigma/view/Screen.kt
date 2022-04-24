package com.github.calo001.nigma.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector) {
    object Main: Screen(route = "main", icon = Icons.Filled.ViewModule)
    object AddPuzzle: Screen(route = "add", icon = Icons.Filled.Add)
    object Profile: Screen(route = "profile", icon = Icons.Filled.AccountCircle)
    object PuzzleResolver: Screen(route = "puzzle", icon = Icons.Filled.SquareFoot)
    object SignIn: Screen(route = "signIn", icon = Icons.Filled.AccountCircle)
    object Signup: Screen(route = "signup", icon = Icons.Filled.AccountCircle)
    object Unknown: Screen(route = "unknown", icon = Icons.Filled.Circle)

    companion object {
        fun fromString(route: String?): Screen {
            return when(route) {
                Main.route -> Main
                AddPuzzle.route -> AddPuzzle
                Profile.route -> Profile
                PuzzleResolver.route -> PuzzleResolver
                SignIn.route -> SignIn
                Signup.route -> Signup
                else -> Unknown
            }
        }
    }
}