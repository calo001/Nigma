package com.github.calo001.nigma.ui.model

data class PuzzleView(
    val id: String,
    val username: String,
    val userImageProfileUrl: String,
    val puzzleImageUrl: String,
    val gridSize: Int,
    val puzzleName: String,
)