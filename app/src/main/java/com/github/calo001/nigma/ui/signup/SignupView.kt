package com.github.calo001.nigma.ui.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.calo001.nigma.R
import com.github.calo001.nigma.ui.theme.NigmaTheme
import com.github.calo001.nigma.view.Screen

@Composable
fun SingUpScreen(
    modifier: Modifier = Modifier,
    onNavigate: (Screen) -> Unit
) {
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
                value = "",
                onValueChange = {},
                label = {
                    Text(text = "Email")
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = {
                    Text(text = "Password")
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Column {
            Button(
                onClick = { onNavigate(Screen.Main) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Signup")
            }
            TextButton(
                onClick = {
                    onNavigate(Screen.Signing)
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
fun SingingScreenPreview() {
    NigmaTheme {
        SingUpScreen(Modifier, {})
    }
}
