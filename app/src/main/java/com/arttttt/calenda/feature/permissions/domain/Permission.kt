package com.arttttt.calenda.feature.permissions.domain

import android.content.Context
import com.arttttt.calenda.feature.permissions.data.checkStatusImpl

interface Permission {

    val permission: String

    fun checkStatus(context: Context): Boolean {
        return checkStatusImpl(context)
    }
}