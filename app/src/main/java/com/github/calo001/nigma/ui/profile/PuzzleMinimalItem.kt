package com.github.calo001.nigma.ui.profile

import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.calo001.nigma.R
import com.github.calo001.nigma.ui.model.PuzzleView
import java.nio.ByteBuffer

@Composable
fun PuzzleMinimalItem(list: List<PuzzleView>) {
    LazyColumn(
        modifier = Modifier.height(400.dp)
    ) {
        items(count = list.size) { index ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                PuzzleImageSmall(
                    onClickPuzzle = { },
                    puzzle = list[index],
                    modifier = Modifier.size(120.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = list[index].puzzleName)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PuzzleImageSmall(
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

            bitmap?.let {
                AsyncImage(
                    model = bitmap,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}