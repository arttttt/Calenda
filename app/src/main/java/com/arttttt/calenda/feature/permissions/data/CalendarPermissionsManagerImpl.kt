package com.arttttt.calenda.feature.permissions.data

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.arttttt.calenda.di.UIScope
import com.arttttt.calenda.feature.permissions.domain.CalendarPermissionsManager
import com.arttttt.calenda.feature.permissions.domain.ReadCalendarPermission
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

@ContributesBinding(UIScope::class)
@Inject
class CalendarPermissionsManagerImpl(
    private val activity: ComponentActivity,
) : CalendarPermissionsManager {

    private var permissionDeferred: CompletableDeferred<Boolean>? = null

    val requestPermissionLauncher = activity.activityResultRegistry.register(
        key = "calendar_permission",
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionDeferred?.complete(isGranted)
    }

    override fun canReadCalendar(): Boolean {
        return ReadCalendarPermission.checkStatus(activity)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun requestReadCalendarPermission(): Boolean {
        if (canReadCalendar()) {
            return true
        }

        permissionDeferred?.let { existing ->
            if (!existing.isCompleted) {
                return existing.await()
            }
        }

        return withContext(Dispatchers.Main.immediate) {
            suspendCancellableCoroutine { continuation ->
                permissionDeferred = CompletableDeferred<Boolean>().apply {
                    invokeOnCompletion { exception ->
                        if (exception == null) {
                            continuation.resume(getCompleted())
                        } else {
                            continuation.cancel(exception)
                        }
                        permissionDeferred = null
                    }
                }

                requestPermissionLauncher.launch(ReadCalendarPermission.permission)

                continuation.invokeOnCancellation {
                    permissionDeferred?.cancel()
                    permissionDeferred = null
                }
            }
        }
    }
}