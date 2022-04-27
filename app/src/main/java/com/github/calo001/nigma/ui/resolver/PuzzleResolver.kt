package com.github.calo001.nigma.ui.resolver

import android.graphics.Bitmap
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.request.ImageRequest
import com.github.calo001.nigma.ui.add.CloseButton
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.github.calo001.nigma.R
import com.github.calo001.nigma.ui.model.GridItem
import com.github.calo001.nigma.ui.model.PuzzleView
import com.github.calo001.nigma.ui.resolver.dragndrop.DragTarget
import com.github.calo001.nigma.ui.resolver.dragndrop.DropTarget
import com.github.calo001.nigma.ui.resolver.dragndrop.LongPressDraggable
import com.github.calo001.nigma.util.split
import com.github.calo001.nigma.view.Screen
import java.nio.ByteBuffer

@Composable
fun PuzzleResolver(
    modifier: Modifier = Modifier,
    puzzleView: PuzzleView,
    onNavigate: (Screen) -> Unit,
    onPuzzleResolved: () -> Unit,
) {
    var showCongratulations by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        PuzzleGroup(
            modifier = modifier,
            puzzleView = puzzleView,
            onPuzzleResolved = {
                showCongratulations = true
                onPuzzleResolved()
            },
            onNavigate = onNavigate,
        )

        if (showCongratulations) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(MaterialTheme.colors.background.copy(alpha = 0.8f))
                    .fillMaxSize()
            ) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.congratulation_success_batch))
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier.padding(16.dp),
                    iterations = Int.MAX_VALUE,
                )
                Button(onClick = {
                    onNavigate(Screen.Main)
                }) {
                    Text(text = "Resolve more puzzles")
                }
            }
        }
    }
}

@Composable
private fun PuzzleGroup(
    modifier: Modifier,
    puzzleView: PuzzleView,
    onPuzzleResolved: () -> Unit,
    onNavigate: (Screen) -> Unit,
) {
    LongPressDraggable(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            CloseButton(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.End),
                onClick = {
                    onNavigate(Screen.Main)
                }
            )
            Text(
                text = "Title",
                style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            PuzzleContent(
                puzzleView = puzzleView,
                onPuzzleResolved = onPuzzleResolved,
            )
        }
    }
}

@Composable
private fun PuzzleContent(
    puzzleView: PuzzleView,
    onPuzzleResolved: () -> Unit,
) {
    var bitmaps by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    val context = LocalContext.current
    DisposableEffect(
        key1 = puzzleView.puzzleImage, key2 = puzzleView.gridSize
    ) {
        val imageLoader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(ByteBuffer.wrap(puzzleView.puzzleImage))
            .crossfade(true)
            .error(R.drawable.ic_logo)
            .target { drawable ->
                val bitmapsSplits =
                    drawable.toBitmap().split(puzzleView.gridSize, puzzleView.gridSize)
                bitmaps = bitmapsSplits.flatten().filterNotNull()
            }
            .allowConversionToBitmap(true)
            .build()
        imageLoader.enqueue(request)

        onDispose {
            imageLoader.shutdown()
        }
    }

    val gridItems by remember(key1 = bitmaps) {
        val list = (0 until puzzleView.gridSize * puzzleView.gridSize)
            .mapNotNull { index ->
                bitmaps.getOrNull(index)?.let { bitmap ->
                    GridItem(
                        originalPosition = index,
                        bitmap = bitmap,
                    )
                }
            }
        mutableStateOf(list)
    }

    var gridItemsShuffled by remember(key1 = gridItems.isNotEmpty(), key2 = gridItems) {
        mutableStateOf(gridItems.map { it.copy() to true }.shuffled())
    }

    var itemsOnGrid by remember {
        mutableStateOf(buildList<GridItem?>{
            repeat(puzzleView.gridSize * puzzleView.gridSize) {
                add(null)
            }
        })
    }

    if (itemsOnGrid.all { it != null }) {
        onPuzzleResolved()
    }

    GridPuzzle(
        size = puzzleView.gridSize,
        items = itemsOnGrid,
        onDragRightDropped = { gridDropped ->
            itemsOnGrid = itemsOnGrid.mapIndexed { indexInGrid, gridItem ->
                if (gridDropped.originalPosition == indexInGrid) gridDropped else gridItem
            }
            gridItemsShuffled = gridItemsShuffled.map { gridItem ->
                if (gridDropped.originalPosition == gridItem.first.originalPosition) {
                    gridItem.first to false
                } else {
                    gridItem
                }

            }
        }
    )

    Pieces(items = gridItemsShuffled)
}

@Composable
fun Pieces(items: List<Pair<GridItem, Boolean>>) {
    LazyRow(
        contentPadding = PaddingValues(16.dp),
    ) {
        items(items) { gridItem ->
            AnimatedVisibility(visible = gridItem.second) {
                Row(
                    modifier = Modifier.alpha(
                        if (gridItem.second) 1f else 0.6f
                    )
                ) {
                    DragTarget(
                        modifier = Modifier,
                        dataToDrop = gridItem.first
                    ) {
                        Card(
                            shape = MaterialTheme.shapes.small
                        ) {
                            Image(
                                bitmap = gridItem.first.bitmap.asImageBitmap(),
                                contentScale = ContentScale.FillBounds,
                                contentDescription = null,
                                modifier = Modifier.size(120.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }
    }
}

@Composable
fun GridPuzzle(
    size: Int,
    items: List<GridItem?>,
    onDragRightDropped: (GridItem) -> Unit,
) {
    val sizeGrid = (LocalConfiguration.current.screenWidthDp - 32).dp
    Card(
        modifier = Modifier
            .padding(16.dp)
            .size(sizeGrid)
    ) {
        Row(
            modifier = Modifier.size(sizeGrid)
        ) {
            items.chunked(size).forEachIndexed { indexColumn, columnElements ->
                Column {
                    columnElements.forEachIndexed { indexItem, gridItem ->
                        val gridItemNumber = (indexColumn * size) + indexItem
                        val shape = ShapeDrawable(RectShape()).apply {
                            paint.color = android.graphics.Color.parseColor("#8F442E")
                        }

                        DropTarget<GridItem>(
                            modifier = Modifier
                        ) { isInBound, gridItemByDrag ->
                            if (isInBound) {
                                if (gridItemByDrag != null) {
                                    if (gridItemByDrag.originalPosition == gridItemNumber) {
                                        onDragRightDropped(gridItemByDrag)
                                    }
                                }
                            }

                            val bitmap = gridItem?.bitmap ?: shape.toBitmap(100, 100, null)
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = null,
                                    contentScale = ContentScale.FillBounds,
                                    alpha = if (isInBound) 0.8f else 1f,
                                    modifier = Modifier
                                        .border(1.dp, MaterialTheme.colors.background)
                                        .size(sizeGrid / size)
                                )
                        }
                    }
                }
            }
        }
    }
}
