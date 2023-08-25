package com.ajayasija.rexsolutions.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.Paint
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import com.arthenica.mobileffmpeg.FFmpeg
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    Log.e("Permissions", "Button clicked media")

    Log.e("photo", "Launch : $launch\npick Mutlitple : $pickMultiple\nvideo: $video")

    // launcher
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                onImageSelect(addWatermarkToImage(uri, context))
            }
        }
    )

    val cameraPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            if (bitmap != null) {
                val uri = bitmapToUri(bitmap, context)
                val watermarkImage = uri?.let { it1 -> addWatermarkToImage(it1, context) }
                if (pickMultiple) {
                    onMultipleImageSelect(listOf(watermarkImage!!))
                } else {
                    onImageSelect(watermarkImage)
                }
            }
        }
    )

    //video
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                onVideoSelect(getRealPathFromURI(context, uri)?.let {
                    addWatermarkToVideo(
                        it,
                        context
                    )
                })

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
                    onVideoSelect(addWatermarkToVideo(media.absolutePath, context))
                }
                Log.e("video", "Success to record video")
            }
        }
    )

    var showLoading by remember { mutableStateOf(false) }
    if (showLoading) {
        ShowLoading()
    }
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




