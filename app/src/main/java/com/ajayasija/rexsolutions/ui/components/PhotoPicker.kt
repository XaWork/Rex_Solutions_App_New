package com.ajayasija.rexsolutions.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun PhotoPicker(
    launch: Boolean = false,
    onImageSelect: (Uri?) -> Unit,
) {
    /*var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }*/

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> onImageSelect(uri) }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { uri ->  }
    )

    if (launch)
        singlePhotoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
}


@Composable
fun ChooseImageOptionsDialog(
    showDialog: (Boolean) -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = { showDialog(false) }) {
        Surface(shape = RoundedCornerShape(20.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Column(modifier = Modifier.padding(16.dp)) {
                    RexButton(
                        title = "Camera",
                        textAllCaps = true,
                        onClick = {
                            onConfirm()
                            showDialog(false)
                        }
                    )
                    VerticalSpace(space = 15.dp)
                    RexButton(
                        title = "Gallery",
                        textAllCaps = true,
                        onClick = {
                            onConfirm()
                            showDialog(false)
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun Previewview() {
    ChooseImageOptionsDialog({}, {})
}

//-------------- PERMISSIONS --------------------------------------

/*@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Permission(
    permission: String = android.Manifest.permission.CAMERA,
    rationale: String = "This permission is important for this app. Please grant the permission.",
    permissionNotAvailableContent: @Composable () -> Unit = { },
    content: @Composable () -> Unit = { }
) {
    val permissionState = rememberPermissionState(permission)

}*/

@Composable
private fun Rationale(
    text: String,
    onRequestPermission: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* Don't */ },
        title = {
            Text(text = "Permission request")
        },
        text = {
            Text(text)
        },
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text("Ok")
            }
        }
    )
}