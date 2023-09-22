package com.ajayasija.rexsolutions.ui.components

import android.Manifest
import android.location.Location
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MediaPicker(
    location: Location,
    launch: Boolean = false,
    pickMultiple: Boolean = false,
    video: Boolean = false,
    showDialog: Boolean = false,
    imageName: String? = null,
    onDismissMediaPicker: () -> Unit,
    onImageSelect: (Uri?) -> Unit,
    onVideoSelect: (String?) -> Unit,
    onMultipleImageSelect: (List<Uri>) -> Unit,
) {
    /*var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }*/
    val context = LocalContext.current

    var showLoading by remember { mutableStateOf(false) }
    if (showLoading) {
        ShowLoading()
    }
    Log.e("Permissions", "Button clicked media")

    Log.e("photo", "Launch : $launch\npick Mutlitple : $pickMultiple\nvideo: $video")

    // launcher
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                showLoading = true
                onImageSelect(addWatermarkToImage(uri, context, location, imageName))
                showLoading = false
            }
        }
    )

    val mediaImg = File.createTempFile("temp_file", ".jpg", context.applicationContext.cacheDir)
    val imageFileUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        mediaImg
    )

    val cameraPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                imageFileUri?.let {
                    val watermarkImage = addWatermarkToImage(
                        it,
                        context = context,
                        location = location,
                        imageName = imageName
                    )
                    if (watermarkImage != null) {
                        onImageSelect(watermarkImage)
                    } else {
                        Toast.makeText(
                            context,
                            "Something went wrong, try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                Log.e("video", "Success to record video")
            }
        }
    )

    //video
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                onVideoSelect(getRealPathFromURI(context, uri))

            }
        }
    )

    val media = File.createTempFile("temp_file", ".mp4", context.applicationContext.cacheDir)
    val fileUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        media
    )
    val videoCaptureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo(),
        onResult = { success ->
            if (success) {
                fileUri?.let {
                    onVideoSelect(media.absolutePath)
                }
                Log.e("video", "Success to record video")
            }
        }
    )

    val multiPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(30),
        onResult = { uriList ->
            showLoading = true
            if (uriList.isNotEmpty()) {
                val watermarkUriList = mutableListOf<Uri>()
                for (i in 0 until (uriList.size)) {
                    addWatermarkToImage(uriList[i], context, location)?.let {
                        watermarkUriList.add(
                            it
                        )
                    }
                    if (i == uriList.size - 1) {
                        showLoading = false
                        onMultipleImageSelect(watermarkUriList)
                    }
                }
            }
        }
    )

    //choose from gallery or camera
    var camera by remember { mutableStateOf(false) }


    if (launch) {
        if (AllPermissionGranted(
                permissions = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )
            )
        ) {
            if (showDialog) {
                ChooseImageOptionsDialog(showDialog = { }) {
                    camera = it
                    if (!video) {
                        if (!pickMultiple && !camera) {
                            singlePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        } else if (pickMultiple && !camera) {
                            showLoading = true
                            multiPhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        } else
                            if (imageFileUri != null) {
                                cameraPickerLauncher.launch(imageFileUri)
                            }
                    } else {
                        if (camera) {
                            videoCaptureLauncher.launch(fileUri)
                        } else {
                            videoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        }
                    }
                }
            }
        }

    }
}




