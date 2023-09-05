package com.ajayasija.rexsolutions.ui.components

import android.Manifest
import android.net.Uri
import android.os.Build
import android.util.Log
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
    launch: Boolean = false,
    pickMultiple: Boolean = false,
    video: Boolean = false,
    showDialog: Boolean = false,
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
                onImageSelect(addWatermarkToImage(uri, context))
                showLoading = false
            }
        }
    )

    val cameraPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            if (bitmap != null) {
                showLoading = true
                val uri = bitmapToUri(bitmap, context)
                val watermarkImage = uri?.let { it1 -> addWatermarkToImage(it1, context) }
                /*if (pickMultiple) {
                    onMultipleImageSelect(listOf(watermarkImage!!))
                } else {*/
                showLoading = false
                onImageSelect(watermarkImage)

                // }
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
                    addWatermarkToImage(uriList[i], context)?.let { watermarkUriList.add(it) }
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
                            cameraPickerLauncher.launch()
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




