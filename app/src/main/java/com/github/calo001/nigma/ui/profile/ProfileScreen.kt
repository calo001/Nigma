package com.github.calo001.nigma.ui.profile

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import com.github.calo001.nigma.R
import com.github.calo001.nigma.repository.model.UserInfo
import com.github.calo001.nigma.ui.add.EditableTextField
import com.github.calo001.nigma.ui.basic.ProfileUserImage
import okhttp3.internal.EMPTY_BYTE_ARRAY
import java.nio.ByteBuffer

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    sessionInfo: UserInfo,
    onImageCaptured: (Bitmap) -> Unit,
    showLoading: Boolean,
    onUsernameChanged: (String) -> Unit,
) {
    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmapFromGallery = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
            bitmapFromGallery?.let { bitmap ->
                onImageCaptured(bitmap)
            }
        }
    }

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    DisposableEffect(
        key1 = sessionInfo
    ) {
        val imageLoader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(ByteBuffer.wrap(sessionInfo.imageProfile ?: EMPTY_BYTE_ARRAY))
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val profile = painterResource(id = R.drawable.profile)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
        ) {
            ProfileUserImage(
                bitmap = bitmap,
                profile = profile,
                onClick = {
                    galleryLauncher.launch("image/*")
                },
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .size(180.dp)
            )
            if (showLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(180.dp)
                )
            }
        }
        Text(text = sessionInfo.email)

        var userName by remember(key1 = sessionInfo.username) {
            mutableStateOf(sessionInfo.username)
        }
        EditableTextField(
            text = userName,
            onChange = { userName = it },
            onClickAccept = { onUsernameChanged(userName) },
        )
        LogoutButton(onLogout = onLogout)
        Spacer(modifier = Modifier.height(140.dp))
    }
}

@Composable
fun LogoutButton(onLogout: () -> Unit) {
    Button(onClick = onLogout) {
        Text(text = "Logout")
    }
}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Preview(showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        onLogout = {},
        sessionInfo = UserInfo("", "", "", ByteArray(0), ""),
        onImageCaptured = {},
        showLoading = false,
        onUsernameChanged = { username ->

        }
    )
}