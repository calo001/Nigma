package com.github.calo001.nigma.router

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
val defaultEnter get(): AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition? = {
    slideIntoContainer(
        AnimatedContentScope.SlideDirection.Left,
        animationSpec = tween(400)
    )
}

@OptIn(ExperimentalAnimationApi::class)
val defaultPopEnter get(): AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition? = {
    slideIntoContainer(
        AnimatedContentScope.SlideDirection.Right,
        animationSpec = tween(400)
    )
}

@OptIn(ExperimentalAnimationApi::class)
val defaultExit get(): AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition? = {
    slideOutOfContainer(
        AnimatedContentScope.SlideDirection.Left,
        animationSpec = tween(400)
    )
}

@OptIn(ExperimentalAnimationApi::class)
val defaultPopExit get(): AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition? = {
    slideOutOfContainer(
        AnimatedContentScope.SlideDirection.Right,
        animationSpec = tween(400)
    )
}