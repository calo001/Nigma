package com.github.calo001.nigma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.github.calo001.nigma.router.Navigator
import com.github.calo001.nigma.router.Router
import com.github.calo001.nigma.router.shouldShowNavigator
import com.github.calo001.nigma.ui.basic.BottomBar
import com.github.calo001.nigma.ui.theme.NigmaTheme
import com.github.calo001.nigma.view.Screen
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.github.calo001.nigma.router.shouldShowAddFab
import com.github.calo001.nigma.ui.basic.ScaffoldOver

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberAnimatedNavController()
            val navigator = remember { Navigator(navController) }

            NigmaTheme {
                ScaffoldOver(
                    bottomBar = {
                        val currentRoute = Screen.fromString(navController.currentDestination?.route)
                        var showBottomBar by rememberSaveable { mutableStateOf(currentRoute.shouldShowNavigator) }
                        var showAddFab by rememberSaveable { mutableStateOf(currentRoute.shouldShowAddFab) }

                        navController.addOnDestinationChangedListener { controller, destination, args ->
                            showBottomBar = Screen.fromString(destination.route).shouldShowNavigator
                            showAddFab = Screen.fromString(destination.route).shouldShowAddFab
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            AnimatedVisibility(
                                visible = showBottomBar,
                                enter = scaleIn(),
                                exit = scaleOut(),
                            ) {
                                BottomBar(
                                    onNavigate = { screen ->  navigator.navigate(screen) }
                                )
                            }
                            AnimatedVisibility(
                                visible = showAddFab,
                                enter = scaleIn(),
                                exit = scaleOut(),
                            ) {
                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                ) {
                                    FloatingActionButton(
                                        onClick = { /*TODO*/ },
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(16.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                        )
                                    }
                                }
                            }
                        }
                    }
                ) {
                    Surface(
                        color = MaterialTheme.colors.background,
                        contentColor = contentColorFor(MaterialTheme.colors.surface),
                        modifier = Modifier
                    ) {
                        Router(
                            navController = navController,
                            startDestination = Screen.Signing.route,
                            onNavigate = { screen ->
                                if (screen is Screen.Main) {
                                    navigator.navigateHome()
                                } else {
                                    navigator.navigate(screen)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}