package com.github.calo001.nigma.ui.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.calo001.nigma.R
import com.github.calo001.nigma.ui.theme.NigmaTheme
import com.github.calo001.nigma.view.Screen
import com.github.calo001.nigma.viewModel.SignUpStatus

typealias Email = String
typealias Password = String
typealias Username = String

@Composable
fun SingUpScreen(
    modifier: Modifier = Modifier,
    onNavigate: (Screen) -> Unit,
    onSignupRequest: (Email, Password, Username) -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var showEmailError by remember { mutableStateOf(false) }
    var showPasswordError by remember { mutableStateOf(false) }
    var showUsernameError by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Signup",
            style = MaterialTheme.typography.h4
        )
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )
        Column {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = {
                    Text(text = "Username")
                },
                isError = showUsernameError,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(text = "Email")
                },
                isError = showEmailError,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                visualTransformation = PasswordVisualTransformation(),
                label = {
                    Text(text = "Password")
                },
                isError = showPasswordError,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Column {
            Button(
                onClick = {
                    showUsernameError = username.isEmpty()
                    showEmailError = email.isEmpty()
                    showPasswordError = password.isEmpty()

                    if (email.isNotEmpty() and password.isNotEmpty() and username.isNotEmpty()) {
                        onSignupRequest(email.trim(), password.trim(), username.trim())
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Signup")
            }
            TextButton(
                onClick = {
                    onNavigate(Screen.SignIn)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Login")
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun SingupScreenPreview() {
    NigmaTheme {
        SingUpScreen(Modifier, {}, { email, password, username ->

        })
    }
}
