package com.github.calo001.nigma.ui.signing

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.github.calo001.nigma.R
import com.github.calo001.nigma.ui.signup.Email
import com.github.calo001.nigma.ui.signup.Password
import com.github.calo001.nigma.ui.theme.NigmaTheme
import com.github.calo001.nigma.view.Screen
import com.github.calo001.nigma.viewModel.SessionStatus

@Composable
fun SingInScreen(
    modifier: Modifier = Modifier,
    onNavigate: (Screen) -> Unit,
    onSignInRequest: (Email, Password) -> Unit,
    status: SessionStatus,
) {
    var email by remember { mutableStateOf("calo_lrc@hotmail.com") }
    var password by remember { mutableStateOf("abcABC123") }
    var showEmailError by remember { mutableStateOf(false) }
    var showPasswordError by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Sign in",
            style = MaterialTheme.typography.h4
        )
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )
        Column {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                isError = showEmailError,
                label = {
                    Text(text = "Email")
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                isError = showPasswordError,
                label = {
                    Text(text = "Password")
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Column {
            Button(
                onClick = {
                    showEmailError = email.isEmpty()
                    showPasswordError = password.isEmpty()

                    if (status is SessionStatus.Idle || status is SessionStatus.Error) {
                        if (email.isNotEmpty() and password.isNotEmpty()) {
                            onSignInRequest(email.trim(), password.trim())
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                when (status) {
                    SessionStatus.Idle,
                    SessionStatus.Loading -> {
                        Row {
                            val composition by rememberLottieComposition(
                                LottieCompositionSpec.RawRes(
                                    R.raw.loading
                                )
                            )
                            LottieAnimation(
                                composition = composition,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "Login")
                        }
                    }
                    is SessionStatus.Error,
                    SessionStatus.LoggedOut,
                    SessionStatus.SignInSuccess,
                    is SessionStatus.SessionStarted -> Text(text = "Login")
                    is SessionStatus.UpdatingSession -> Text(text = "Login")
                }
            }
            TextButton(
                onClick = { onNavigate(Screen.Signup) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Signup")
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun SingingScreenPreview() {
    NigmaTheme {
        SingInScreen(Modifier, { }, { _, _ ->}, SessionStatus.Idle)
    }
}