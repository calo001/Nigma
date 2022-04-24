package com.github.calo001.nigma.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.calo001.nigma.R
import com.github.calo001.nigma.ui.add.EditableTextField
import com.github.calo001.nigma.ui.basic.ProfileUserImage
import io.appwrite.models.User

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    sessionInfo: User?,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val profile = painterResource(id = R.drawable.profile)
        ProfileUserImage(
            url = "",
            profile = profile,
            modifier = Modifier
                .padding(vertical = 24.dp)
                .size(180.dp)
        )
        EditableTextField(text = sessionInfo?.name ?: "", onChange = {})
        EditableTextField(text = sessionInfo?.email ?: "", onChange = {})
        LogoutButton(onLogout = onLogout)
    }
}

@Composable
fun LogoutButton(onLogout: () -> Unit) {
    Button(onClick = onLogout) {
        Text(text = "Logout")
    }
}

@Preview(showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(onLogout = {}, sessionInfo = null)
}