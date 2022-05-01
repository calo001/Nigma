package com.github.calo001.nigma.ui.basic

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.calo001.nigma.R
import com.github.calo001.nigma.ui.add.NumberCard
import com.github.calo001.nigma.ui.model.PuzzleView
import com.github.calo001.nigma.ui.theme.NigmaTheme
import com.github.calo001.nigma.view.gridDefaults
import java.nio.ByteBuffer

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PuzzleItem(
    modifier: Modifier = Modifier,
    puzzle: PuzzleView,
    onClickPuzzle: (PuzzleView) -> Unit,
) {
    var puzzleSize by rememberSaveable(
        key = puzzle.id
    ) { mutableStateOf(puzzle.gridSize) }
    Row (
        modifier = modifier
    ){
        SizesPuzzle(
            selected = puzzleSize,
            onClick = {
                puzzleSize = it
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        PuzzleImage(
            onClickPuzzle = {
                onClickPuzzle(puzzle.copy(gridSize = puzzleSize))
            },
            puzzle = puzzle,
            modifier = Modifier.weight(1f)
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun SizesPuzzle(
    modifier: Modifier = Modifier,
    selected: Int,
    onClick: (Int) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        gridDefaults.forEach {
            Spacer(modifier = Modifier.height(6.dp))
            NumberCard(
                number = it.number,
                isSelected = it.number == selected,
                onClick = {
                    onClick(it.number)
                },
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PuzzleImage(
    modifier: Modifier = Modifier,
    onClickPuzzle: () -> Unit,
    puzzle: PuzzleView,
) {
    Card(
        onClick = onClickPuzzle,
        modifier = modifier
    ) {
        val context = LocalContext.current
        Box {
            var bitmap by remember { mutableStateOf<Bitmap?>(null) }
            DisposableEffect(
                key1 = puzzle.puzzleImage
            ) {
                val imageLoader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(ByteBuffer.wrap(puzzle.puzzleImage.clone()))
                    .crossfade(true)
                    .error(R.drawable.ic_logo)
                    .target { drawable ->
                        bitmap = drawable.toBitmap()
                    }
                    .allowConversionToBitmap(true)
                    .build()
                imageLoader.enqueue(request)

                onDispose {
                    imageLoader.shutdown()
                }
            }

            AsyncImage(
                model = bitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.height(300.dp)
            )
            Card(
                backgroundColor = MaterialTheme.colors.secondaryVariant,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Text(
                    text = puzzle.puzzleName,
                    style = MaterialTheme.typography.button,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun UserImageProfile(
    modifier: Modifier = Modifier,
    puzzle: PuzzleView,
    bitmap: Bitmap?,
    onClick: () -> Unit,
) {
    val profile = painterResource(id = R.drawable.profile)
    ProfileUserImage(
        bitmap = bitmap,
        profile = profile,
        onClick = onClick,
        modifier = modifier
    )
}

@ExperimentalMaterialApi
@Composable
fun ProfileUserImage(
    modifier: Modifier,
    bitmap: Bitmap?,
    profile: Painter,
    onClick: () -> Unit,
) {
    Card(
        shape = CircleShape,
        elevation = 0.dp,
        onClick = onClick,
        border = BorderStroke(2.dp, MaterialTheme.colors.surface),
        modifier = modifier,
    ) {
        AsyncImage(
            model = bitmap,
            placeholder = profile,
            error = profile,
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Preview
@Composable
fun PuzzleItemPreview() {
    NigmaTheme {
        PuzzleItem(
            puzzle = PuzzleView(
                id = "01",
                username = "Pepe",
                userImageProfileUrl = "https://images.unsplash.com/photo-1633332755192-727a05c4013d?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8dXNlcnxlbnwwfHwwfHw%3D&w=1000&q=80",
                puzzleImage = ByteArray(255),
                puzzleName = "Name",
                gridSize = 3,
                resolvedBy = emptyList(),
                description = "",
            ),
            onClickPuzzle = {}
        )
    }
}