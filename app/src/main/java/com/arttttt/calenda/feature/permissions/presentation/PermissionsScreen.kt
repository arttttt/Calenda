package com.arttttt.calenda.feature.permissions.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arttttt.calenda.metro.metroViewModel
import com.arttttt.calenda.uikit.theme.CalendaTheme

@Composable
fun PermissionsScreen(
    openMainScreen: () -> Unit,
) {
    val viewModel = metroViewModel<PermissionsViewModel>()

    PermissionsScreenContent(
        onRequestPermission = viewModel::requestPermission,
    )

    LaunchedEffect(viewModel) {
        viewModel
            .commands
            .collect { command ->
                when (command) {
                    is PermissionsViewModel.Command.OpenMainScreen -> openMainScreen()
                }
            }
    }
}

@Composable
private fun PermissionsScreenContent(
    onRequestPermission: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        Text(
            text = "Calendar permissions are not granted",
        )

        Button(
            onClick = onRequestPermission,
        ) {
            Text(
                text = "Grant permissions",
            )
        }
    }
}

@Preview
@Composable
private fun PermissionsScreenPreview() {
    CalendaTheme {
        PermissionsScreenContent(
            onRequestPermission = {},
        )
    }
}