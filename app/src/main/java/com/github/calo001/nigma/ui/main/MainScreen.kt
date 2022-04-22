package com.github.calo001.nigma.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.calo001.nigma.ui.basic.PuzzleItem
import com.github.calo001.nigma.ui.model.PuzzleView
import com.github.calo001.nigma.view.Screen

@Composable
fun MainScreen(
    puzzles: List<PuzzleView>,
    username: String,
    onNavigate: (Screen) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
    ) {
        item { 
            Greetings(username)
        }
        items(puzzles.size) { index ->
            PuzzleItem(
                puzzle = puzzles[index],
                onClickPuzzle = { onNavigate(Screen.PuzzleResolver) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun Greetings(username: String) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Welcome",
            style = MaterialTheme.typography.h6
         )
        Text(
            text = username,
            style = MaterialTheme.typography.h4
        )
    }
}

val listPuzzles = listOf(
    PuzzleView(
        username = "Pepe",
        userImageProfileUrl = "https://images.unsplash.com/photo-1633332755192-727a05c4013d?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8dXNlcnxlbnwwfHwwfHw%3D&w=1000&q=80",
        puzzleImageUrl = "https://pbs.twimg.com/profile_images/1364932022926458886/BxwXy9N8_400x400.jpg",
        gridSize = 3,
        puzzleName = "Name"
    ),
    PuzzleView(
        username = "Pepe",
        userImageProfileUrl = "https://images.unsplash.com/photo-1633332755192-727a05c4013d?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8dXNlcnxlbnwwfHwwfHw%3D&w=1000&q=80",
        puzzleImageUrl = "https://images.unsplash.com/photo-1633332755192-727a05c4013d?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8dXNlcnxlbnwwfHwwfHw%3D&w=1000&q=80",
        gridSize = 3,
        puzzleName = "Name"
    )
)