package com.github.calo001.nigma.ui.add

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.github.calo001.nigma.R
import com.github.calo001.nigma.view.Screen
import com.github.calo001.nigma.view.gridDefaults
import com.github.calo001.nigma.viewModel.AddPuzzleStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddScreen(
    modifier: Modifier = Modifier,
    puzzleStatus: AddPuzzleStatus,
    onNavigate: (Screen) -> Unit,
    onImageCaptured: (Bitmap, String) -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    puzzleName: String,
    puzzleDescription: String,
    bitmap: Bitmap?,
    onReset: () -> Unit,
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch(Dispatchers.IO) {
                val bitmapFromGallery = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                }
                bitmapFromGallery?.let { bitmap ->
                    val compressed = ThumbnailUtils.extractThumbnail(bitmap.copy(Bitmap.Config.RGB_565, false), 600, 600)
                    onImageCaptured(compressed, uri.lastPathSegment ?: uri.lastPathSegment ?: "puzzle.png")
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AddPuzzleContent(
            modifier = Modifier.fillMaxSize(),
            onNavigate = onNavigate,
            puzzleName = puzzleName,
            onNameChange = onNameChange,
            bitmap = bitmap,
            puzzleDescription = puzzleDescription,
            onDescriptionChange = onDescriptionChange,
            onGalleryLaunch = {
                galleryLauncher.launch("image/*")
            },
            onClose = onClose,
        )

        AnimatedVisibility(
            visible = puzzleStatus is AddPuzzleStatus.Uploading,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            UploadingContent()
        }

        AnimatedVisibility(
            visible = puzzleStatus is AddPuzzleStatus.Success,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            SuccessCreated(onClick = {
                onNavigate(Screen.Main)
                onReset()
            })
        }
    }
}

@Composable
fun SuccessCreated(
    onClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(MaterialTheme.colors.background.copy(0.95f))
            .fillMaxSize()
            .clickable { onClick() }
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success))
        LottieAnimation(
            composition = composition,
            contentScale = ContentScale.FillBounds,
            iterations = Int.MAX_VALUE,
            modifier = Modifier
                .size(300.dp)
        )
        Text(
            text = "Puzzle created successfully",
            modifier = Modifier.padding(bottom = 16.dp))
        Button(onClick = onClick) {
            Text(text = "OK")
        }
    }
}

@Composable
fun UploadingContent() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(MaterialTheme.colors.background.copy(0.95f))
            .fillMaxSize()
            .clickable { }
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.upload_files))
        LottieAnimation(
            composition = composition,
            contentScale = ContentScale.Fit,
            iterations = Int.MAX_VALUE,
            modifier = Modifier
                .size(300.dp)
        )
        Text(
            text = "Creating puzzle ...",
            modifier = Modifier
        )
    }
}

@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AddPuzzleContent(
    modifier: Modifier,
    onNavigate: (Screen) -> Unit,
    puzzleName: String,
    onNameChange: (String) -> Unit,
    bitmap: Bitmap?,
    puzzleDescription: String,
    onDescriptionChange: (String) -> Unit,
    onGalleryLaunch: () -> Unit,
    onClose: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        CloseButton(
            modifier = Modifier.align(Alignment.End),
            onClick = onClose
        )
        EditableTextField(
            text = puzzleName,
            onChange = onNameChange,
        )
        SquarePuzzle(
            modifier = Modifier,
            bitmap = bitmap,
            onClick = {
                scope.launch {
                    onGalleryLaunch()
                }
            }
        )
        DescriptionPuzzle(
            modifier = Modifier,
            puzzleDescription = puzzleDescription,
            onDescriptionChange = onDescriptionChange,
        )
    }
}

@Composable
fun DescriptionPuzzle(
    modifier: Modifier = Modifier,
    onDescriptionChange: (String) -> Unit,
    puzzleDescription: String
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp)
        .background(MaterialTheme.colors.surface, MaterialTheme.shapes.medium)
    ) {
        BasicTextField(
            value = puzzleDescription,
            onValueChange = onDescriptionChange,
            textStyle = TextStyle.Default.copy(color = MaterialTheme.colors.primary),
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(min = 200.dp, max = 400.dp)
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun NumberCard(
    modifier: Modifier = Modifier,
    number: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val background = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.background
    val content = if (isSelected) MaterialTheme.colors.background else MaterialTheme.colors.primary
    val border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colors.primary)
    Card(
        backgroundColor = background,
        border = border,
        shape = CircleShape,
        onClick = onClick,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize()
        ) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.caption,
                color = content,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun SquarePuzzle(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    bitmap: Bitmap?
) {
    val size = LocalConfiguration.current.screenWidthDp - 16 - 16

    Card(
        backgroundColor = MaterialTheme.colors.primary,
        onClick = onClick,
        modifier = modifier
            .size(size.dp)
    ) {
        if (bitmap != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(bitmap)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                filterQuality = FilterQuality.Medium,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Button(onClick = onClick) {
                Icon(imageVector = Icons.Filled.PhotoAlbum, contentDescription = null)
                Text(text = "ADD IMAGE")
            }
        }
    }
}

@Composable
fun CloseButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(30.dp)
    ) {
        Icon(imageVector = Icons.Filled.Close, contentDescription = null)
    }
}

@ExperimentalComposeUiApi
@Composable
fun EditableTextField(
    text: String,
    onChange: (String) -> Unit,
    onClickAccept: () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var editable by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    if (editable) {
        Box {
            OutlinedTextField(
                value = text,
                textStyle = MaterialTheme.typography.h5,
                onValueChange = onChange,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
            LaunchedEffect(key1 = text) {
                focusRequester.requestFocus()
            }
            OutlinedButton(
                onClick = {
                    editable = false
                    onClickAccept()
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            ) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = null)
            }
        }
    } else {
        keyboardController?.hide()
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        editable = true
                    }
                    .padding(16.dp)
            )
            OutlinedButton(onClick = { editable = true }) {
                Text(text = "Edit")
            }
        }
    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun AddScreenPreview() {
    Column {
        EditableTextField(
            text = "texto",
            onChange = {},
        )
    }
}