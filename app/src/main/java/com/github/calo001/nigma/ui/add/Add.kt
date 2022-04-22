package com.github.calo001.nigma.ui.add

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.calo001.nigma.view.Screen
import com.github.calo001.nigma.view.gridDefaults

@Composable
fun AddScreen(
    modifier: Modifier = Modifier,
    onNavigate: (Screen) -> Unit,
) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        CloseButton(
            modifier = Modifier.align(Alignment.End),
            onClick = {
                onNavigate(Screen.Main)
            }
        )
        EditableTextField(
            text = "Carlos Lopez",
            onChange = {},
        )
        SquarePuzzle(
            modifier = Modifier
        )
        SizesPuzzle(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )
        DescriptionPuzzle(
            modifier = Modifier
        )
    }
}

@Composable
fun DescriptionPuzzle(modifier: Modifier = Modifier) {
    BasicTextField(
        value = "Descripcion",
        onValueChange = {},
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp, max = 400.dp)
    )
}

@Composable
fun SizesPuzzle(modifier: Modifier = Modifier) {
    val elementSize = (LocalConfiguration.current.screenWidthDp - 16 - 16 - 18) / 3
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
    ) {
        gridDefaults.forEach {
            NumberCard(
                number = it.number,
                isSelected = false,
                modifier = Modifier.width(elementSize.dp)
            )
        }
    }

}

@Composable
fun NumberCard(
    modifier: Modifier = Modifier,
    number: Int,
    isSelected: Boolean,
) {
    val background = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.background
    val content = if (isSelected) MaterialTheme.colors.background else MaterialTheme.colors.primary
    val border = if (isSelected) null else BorderStroke(3.dp, MaterialTheme.colors.primary)
    Card(
        backgroundColor = background,
        border = border,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = number.toString(),
            style = MaterialTheme.typography.h3,
            color = content,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )
    }
}

@Composable
fun SquarePuzzle(modifier: Modifier = Modifier) {
    val size = LocalConfiguration.current.screenWidthDp - 16 - 16

    Card(
        backgroundColor = MaterialTheme.colors.primary,
        modifier = modifier
            .size(size.dp)
    ) {
        Button(onClick = { /*TODO*/ }) {
            Icon(imageVector = Icons.Filled.PhotoAlbum, contentDescription = null)
            Text(text = "ADD IMAGE")
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

@Composable
fun EditableTextField(text: String, onChange: (String) -> Unit) {
    var editable by rememberSaveable { mutableStateOf(false) }
    if (editable) {
        Box() {
            OutlinedTextField(
                value = text,
                textStyle = MaterialTheme.typography.h5,
                onValueChange = onChange,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
            IconButton(
                onClick = {
                    editable = false
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            ) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = null)
            }
        }
    } else {
        Text(
            text = text,
            style = MaterialTheme.typography.h5,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { editable = true }
                .padding(16.dp)
        )
    }
}

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