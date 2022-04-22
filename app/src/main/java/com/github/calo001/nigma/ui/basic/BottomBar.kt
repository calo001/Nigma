package com.github.calo001.nigma.ui.basic

import androidx.compose.foundation.layout.*
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
) {
    Box(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            contentColor = MaterialTheme.colors.primary
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FloatingActionButton(
                    onClick = { onNavigate(Screen.AddPuzzle) },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null
                    )
                }
                IconButton(onClick = { onNavigate(Screen.Main) }) {
                    Icon(
                        imageVector = Screen.Main.icon,
                        contentDescription = null
                    )
                }
                IconButton(onClick = { onNavigate(Screen.Profile) }) {
                    Icon(
                        imageVector = Screen.Profile.icon,
                        contentDescription = null
                    )
                }
            }
        }
    }
}



@Preview
@Composable
fun BottomBarPreview() {

}