package com.github.calo001.nigma.ui.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ScaffoldOver(
    bottomBar: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit,
    topSnack: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        content()
        Box(modifier = Modifier
            .fillMaxSize()
            .align(Alignment.BottomCenter)
        ) {
            bottomBar()
            topSnack()
        }
    }
}