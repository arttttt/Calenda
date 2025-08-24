package com.arttttt.calenda.feature.permissions.domain

import android.Manifest

data object ReadCalendarPermission : Permission {

    override val permission: String = Manifest.permission.READ_CALENDAR
}