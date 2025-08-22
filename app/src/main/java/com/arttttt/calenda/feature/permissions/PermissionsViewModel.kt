package com.arttttt.calenda.feature.permissions

import androidx.lifecycle.ViewModel
import com.arttttt.calenda.metro.ViewModelKey
import com.arttttt.calenda.metro.ViewModelScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject

@ViewModelKey(PermissionsViewModel::class)
@ContributesIntoMap(ViewModelScope::class)
@Inject
class PermissionsViewModel : ViewModel()