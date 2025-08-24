package com.arttttt.calenda.metro

import android.app.Activity
import android.content.Context
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import com.arttttt.calenda.App
import com.arttttt.calenda.MainActivity
import com.arttttt.calenda.di.AppGraph
import com.arttttt.calenda.di.UIGraph

val Context.appGraph: AppGraph
    get() {
        return when (this) {
            is App -> appGraph
            else -> applicationContext.appGraph
        }
    }

val Activity.uiGraph: UIGraph
    get() {
        return when (this) {
            is MainActivity -> this.uiGraph
            else -> error("Activity must be MainActivity")
        }
    }

@Composable
fun getUIGraph(): UIGraph {
    val activity = LocalActivity.current ?: error("Activity must be provided")

    return activity.uiGraph
}