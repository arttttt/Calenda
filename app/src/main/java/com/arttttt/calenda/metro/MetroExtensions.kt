package com.arttttt.calenda.metro

import android.content.Context
import com.arttttt.calenda.App
import com.arttttt.calenda.di.AppGraph

val Context.appGraph: AppGraph
    get() {
        return when (this) {
            is App -> appGraph
            else -> applicationContext.appGraph
        }
    }