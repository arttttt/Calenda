package com.arttttt.calenda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColor
import androidx.lifecycle.lifecycleScope
import com.arttttt.calenda.data.CalendarRepositoryImpl
import com.arttttt.calenda.domain.CalendarInfo
import com.arttttt.calenda.ui.theme.CalendaTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val calendarRepository by lazy { CalendarRepositoryImpl(applicationContext) }

    private var calendars by mutableStateOf(emptyList<CalendarInfo>())

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                lifecycleScope.launch {
                    calendars = calendarRepository.getCalendars().getOrThrow()
                }
            } else {
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            CalendaTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(
                            calendars
                        ) { item ->
                            Text(
                                text = item.displayName,
                                color = Color(item.color),
                            )
                        }
                    }
                }
            }
        }

        requestPermissionLauncher.launch(android.Manifest.permission.READ_CALENDAR)
    }
}