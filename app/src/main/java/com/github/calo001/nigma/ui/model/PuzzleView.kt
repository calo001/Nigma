package com.github.calo001.nigma.ui.model

data class PuzzleView(
    val id: String,
    val username: String,
    val description: String,
    val userImageProfileUrl: String,
    val puzzleImage: ByteArray,
    val gridSize: Int,
    val puzzleName: String,
    val resolvedBy: List<String>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PuzzleView

        if (id != other.id) return false
        if (username != other.username) return false
        if (userImageProfileUrl != other.userImageProfileUrl) return false
        if (!puzzleImage.contentEquals(other.puzzleImage)) return false
        if (gridSize != other.gridSize) return false
        if (puzzleName != other.puzzleName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + userImageProfileUrl.hashCode()
        result = 31 * result + puzzleImage.contentHashCode()
        result = 31 * result + gridSize
        result = 31 * result + puzzleName.hashCode()
        return result
    }
}