package com.arttttt.calenda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.arttttt.calenda.di.UIGraph
import com.arttttt.calenda.metro.appGraph
import com.arttttt.calenda.uikit.theme.CalendaTheme
import dev.zacsweers.metro.asContribution

class MainActivity : ComponentActivity() {

    val uiGraph: UIGraph by lazy {
        appGraph.asContribution<UIGraph.Factory>().create(this)
    }

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
        get() {
            return uiGraph.metroViewModelFactory
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            CalendaTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    RootContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    )
                }
            }
        }
    }
}