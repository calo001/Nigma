package com.github.calo001.nigma.ui.states

import com.github.calo001.nigma.ui.model.PuzzleView

sealed interface PuzzleListState {
    class Loading(val list: List<PuzzleView>): PuzzleListState
    class Success(val list: List<PuzzleView>): PuzzleListState
    object Error: PuzzleListState
}