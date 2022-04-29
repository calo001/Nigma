package com.github.calo001.nigma.ui.basic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.calo001.nigma.view.Screen

@Composable
fun BottomBar(
    onNavigate: (Screen) -> Unit,
    selected: Screen,
) {
    Box(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
    ) {
        Card(
            contentColor = MaterialTheme.colors.primary,
            elevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                FloatingActionButton(
                    onClick = { onNavigate(Screen.AddPuzzle) },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.padding(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null
                    )
                }
                IconButton(onClick = { onNavigate(Screen.Main) }) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Screen.Main.icon,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        AnimatedVisibility(visible = selected is Screen.Main) {
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .background(
                                        color = MaterialTheme.colors.primary,
                                        shape = CircleShape
                                    )
                                    .size(6.dp)
                            )
                        }
                    }
                }
                IconButton(onClick = { onNavigate(Screen.Profile) }) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Screen.Profile.icon,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        AnimatedVisibility(visible = selected is Screen.Profile) {
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .background(
                                        color = MaterialTheme.colors.primary,
                                        shape = CircleShape
                                    )
                                    .size(6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}



@Preview
@Composable
fun BottomBarPreview() {

}