package com.github.calo001.nigma.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.calo001.nigma.ui.basic.PuzzleItem
import com.github.calo001.nigma.ui.model.PuzzleView
import com.github.calo001.nigma.ui.states.PuzzleListState
import com.github.calo001.nigma.view.Screen

@ExperimentalFoundationApi
@Composable
fun MainScreen(
    puzzleListState: PuzzleListState,
    username: String,
    onNavigate: (Screen) -> Unit,
    onClickPuzzle: (PuzzleView) -> Unit,
) {
    when(puzzleListState) {
        PuzzleListState.Error -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(text = "Error")
            }
        }
        is PuzzleListState.Loading -> {
            if (puzzleListState.list.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            } else {
                PuzzlesListContent(
                    username = username,
                    puzzleList = puzzleListState.list,
                    onClickPuzzle = onClickPuzzle
                )
            }
        }
        is PuzzleListState.Success -> {
            PuzzlesListContent(
                username = username,
                puzzleList = puzzleListState.list,
                onClickPuzzle = onClickPuzzle
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun PuzzlesListContent(
    username: String,
    puzzleList: List<PuzzleView>,
    onClickPuzzle: (PuzzleView) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
    ) {
        item {
            Greetings(username)
        }
        items(
            count = puzzleList.size,
            key = { puzzleList[it].id }
        ) { index ->
            Column(
                modifier = Modifier
                    .animateItemPlacement()
            ) {
                PuzzleItem(
                    puzzle = puzzleList[index],
                    onClickPuzzle = { onClickPuzzle(puzzleList[index]) },
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
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
            style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold)
        )
    }
}