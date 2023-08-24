package com.ajayasija.rexsolutions.ui.components

import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AllPermissionGranted(
    permissions: List<String>,
): Boolean {

    Log.e("Permissions", "Button clicked")

    var granted = false
    val permissionStates = rememberMultiplePermissionsState(
        permissions = permissions
    )
    val lifecycleOwner = LocalLifecycleOwner.current

    //conditional UI
    var showDialog by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    if (showDialog) {
        PermissionDialog(onDismiss = {
            showDialog=it
        })
        //ShowToast(message = "Go to setting ", context = context)
    }

    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    permissionStates.launchMultiplePermissionRequest()
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    })

    if (permissionStates.allPermissionsGranted) {
        granted = true
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            permissionStates.permissions.forEach {
                when (it.permission) {
                    Manifest.permission.ACCESS_COARSE_LOCATION -> {
                        when {
                            it.status.isGranted -> {
                                //showDialog = false
                                granted = true
                            }

                            it.status.shouldShowRationale -> {
                                // showDialog = true
                                granted = false
                            }

                            !it.status.isGranted && !it.status.shouldShowRationale -> {
                                showDialog = true
                                granted = false
                            }
                        }
                    }

                    Manifest.permission.ACCESS_FINE_LOCATION -> {
                        when {
                            it.status.isGranted -> {
                                //showDialog = false
                                granted = true
                            }

                            it.status.shouldShowRationale -> {
                                // showDialog = true
                                granted = false
                            }

                            !it.status.isGranted && !it.status.shouldShowRationale -> {
                                 showDialog = true
                                granted = false
                            }
                        }
                    }

                    Manifest.permission.CAMERA -> {
                        when {
                            it.status.isGranted -> {
                                granted = true
                            }

                            it.status.shouldShowRationale -> {
                                granted = false
                            }

                            !it.status.isGranted && !it.status.shouldShowRationale -> {
                                granted = false
                                showDialog = false

                            }
                        }
                    }

                    Manifest.permission.RECORD_AUDIO -> {
                        when {
                            it.status.isGranted -> {
                                granted = true
                            }

                            it.status.shouldShowRationale -> {
                                granted = false
                            }

                            !it.status.isGranted && !it.status.shouldShowRationale -> {
                                granted = false
                                showDialog = true

                            }
                        }
                    }
                }
            }
        }
    }
    return granted
}


interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

class CameraPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined)
            "It seems you permanently denied camera permission. \n You can go to app settings to grant it."
        else
            "This app needs access to your camera"
    }

}

class RecordAudioPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined)
            "It seems you permanently denied microphone permission. \n You can go to app settings to grant it."
        else
            "This app needs access to your microphone"
    }

}

class LocationPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined)
            "It seems you permanently denied location permission. \n You can go to app settings to grant it."
        else
            "This app needs access to your location"
    }

}