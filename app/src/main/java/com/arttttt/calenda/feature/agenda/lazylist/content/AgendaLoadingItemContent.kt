package com.arttttt.calenda.feature.agenda.lazylist.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AgendaLoadingItemContent(
    modifier: Modifier,
) {
    Box(
        modifier = modifier.padding(vertical = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Preview(showBackground = true)
@Composable
private fun AgendaLoadingItemContentPreview() {
    AgendaLoadingItemContent(
        modifier = Modifier.fillMaxSize(),
    )
}