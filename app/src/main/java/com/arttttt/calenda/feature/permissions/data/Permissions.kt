package com.arttttt.calenda.feature.permissions.data

import android.content.Context
import android.content.pm.PackageManager
import com.arttttt.calenda.feature.permissions.domain.Permission

context(permission: Permission)
internal fun checkStatusImpl(context: Context): Boolean {
    return context.checkSelfPermission(permission.permission) == PackageManager.PERMISSION_GRANTED
}