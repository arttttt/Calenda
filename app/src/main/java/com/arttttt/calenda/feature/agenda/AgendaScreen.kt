package com.arttttt.calenda.feature.agenda

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AgendaScreen() {
    AgendaScreenContent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgendaScreenContent() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Agenda",
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            }
        },
    ) { innerPadding ->
        innerPadding
    }
}

@Preview
@Composable
private fun AgendaScreenContentPreview() {
    AgendaScreenContent()
}