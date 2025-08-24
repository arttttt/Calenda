package com.arttttt.calenda.feature.agenda

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AgendaScreen() {
    AgendaScreenContent()
}

@Composable
private fun AgendaScreenContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Agenda",
        )
    }
}

@Preview
@Composable
private fun AgendaScreenContentPreview() {
    AgendaScreenContent()
}