package com.ajayasija.rexsolutions.ui.components

import android.Manifest
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import com.amazonaws.mobile.auth.core.internal.util.ViewHelper.showDialog
import com.arthenica.mobileffmpeg.FFmpeg
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
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
    //changeDialogValue: (Boolean) -> Unit,
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

    //take image using camera
    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

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

    val multiPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(30),
        onResult = { uriList ->
            if (uriList.isNotEmpty()) {
                val watermarkUriList = mutableListOf<Uri>()
                uriList.forEach { uri ->
                    addWatermarkToImage(uri, context)?.let { watermarkUriList.add(it) }
                }
                onMultipleImageSelect(watermarkUriList)
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
                        } else if (pickMultiple && !camera)
                            multiPhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        else
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

fun createImageFile(context: Context): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}_", //prefix
        ".jpg", //suffix
        storageDir //directory
    )
}

@Composable
fun ChooseImageOptionsDialog(
    showDialog: (Boolean) -> Unit,
    camera: (Boolean) -> Unit
) {
    Dialog(onDismissRequest = { showDialog(false) }) {
        Surface(shape = RoundedCornerShape(20.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Column(modifier = Modifier.padding(16.dp)) {
                    RexButton(
                        title = "Camera",
                        textAllCaps = true,
                        onClick = {
                            camera(true)
                            showDialog(false)
                        }
                    )
                    VerticalSpace(space = 15.dp)
                    RexButton(
                        title = "Gallery",
                        textAllCaps = true,
                        onClick = {
                            camera(false)
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

}

fun gpsEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    val locationEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

    return if (locationEnabled) {
        true
    } else {
        /*  val intent = Intent(
              Settings.ACTION_LOCATION_SOURCE_SETTINGS,
              Uri.fromParts("package", context.packageName, null)
          )
          context.startActivity(intent)*/
        false
    }

}

fun currentLocation(
    onLocationReceived: (latitude: Double, longitude: Double) -> Unit,
    context: Context
) {
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        if (gpsEnabled(context)) {
            fusedLocationClient.lastLocation.addOnSuccessListener(
                context as ComponentActivity
            ) { location ->
                location?.let {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    onLocationReceived(latitude, longitude)
                }
            }
        }
    }
}

private fun getCurrentDateTime(video: Boolean = false): String? {
    val sdf = SimpleDateFormat(
        if (video) "dd MMM yyyy hh:mm a" else "dd MMM yyyy hh\\:mm a",
        Locale.getDefault()
    )
    return sdf.format(Date())
}

//--------------------- image--------------------

@RequiresApi(Build.VERSION_CODES.P)
fun uriToBitmap(uri: Uri, context: Context): Bitmap {
    val contentResolver: ContentResolver = context.contentResolver

    val source = ImageDecoder.createSource(contentResolver, uri)
    return ImageDecoder.decodeBitmap(source)
}

@RequiresApi(Build.VERSION_CODES.Q)
fun bitmapToUri(bitmap: Bitmap, context: Context): Uri? {
    val displayName = "image_${System.currentTimeMillis()}.png"
    val mimeType = "image/png"
    val contentResolver: ContentResolver = context.contentResolver

    val imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    val imageDetails = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
        put(MediaStore.Images.Media.MIME_TYPE, mimeType)
    }

    val imageUri = contentResolver.insert(imageCollection, imageDetails)
    imageUri?.let { uri ->
        val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
        outputStream?.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    return imageUri
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun addWatermarkToImage(uri: Uri, context: Context): Uri? {
    val originalBitmap = uriToBitmap(uri, context)

    val desiredWidth = 800
    val desiredHeight = 900
    val matrix = Matrix()
    matrix.postRotate(0F)
    val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, desiredWidth, desiredHeight, true)
    val bitmap = resizedBitmap.copy(Bitmap.Config.ARGB_8888, true)

    val canvas = Canvas(bitmap)
    val paint = Paint()

    paint.color = Color.WHITE
    paint.textSize = 30f
    paint.isAntiAlias = true
    paint.textAlign = Paint.Align.RIGHT

    //get current time and location
    var location = ""
    currentLocation(onLocationReceived = { lat, lng -> location = "Lat: $lat, Lng: $lng" }, context)
    val dateTime = getCurrentDateTime()

    val watermarkText = "Rex solution\n$dateTime\n$location"
    val lines = watermarkText.split("\n")
    val padding = 5
    val lineHeight = (paint.textSize + padding).toInt()
    val startY = bitmap.height - (lines.size * lineHeight)

    for (i in lines.indices) {
        val textY = startY + (i * lineHeight)
        canvas.drawText(lines[i], bitmap.width - padding.toFloat(), textY.toFloat(), paint)
    }

    return bitmapToUri(bitmap, context)
}

//-------------- video ----------------------------------


@OptIn(DelicateCoroutinesApi::class)
fun addWatermarkToVideo(videoUri: String, context: Context): String {
    // Convert Uri to file path
    val outputUri = getOutputVideoPath(context)
    var location = ""
    currentLocation(onLocationReceived = { lat, lng -> location = "Lat: $lat, Lng: $lng" }, context)
    val dateTime = getCurrentDateTime(video = true)

    //if (!location.isEmpty()) {
    val uploading =
        ProgressDialog.show(context, "Processing", "Please wait...", false, false)
    val watermarkText = "          Rex solution\n          $dateTime\n$location"

    Log.e(
        "tag",
        "Video uri path : $videoUri\n Output path : $outputUri\n$watermarkText"
    )

    val fontPath = "/system/fonts/Roboto-Regular.ttf"
    val command = arrayOf(
        "-i", videoUri,
        "-vf",
        "drawtext=text='$watermarkText':fontfile=$fontPath:x=(w-text_w-10):y=(h-text_h-10):fontsize=25:fontcolor=black",
        "-c:v", "vp9",
        "-crf", "20",
        "-preset", "medium",
        "-c:a", "copy",
        outputUri
    )

    GlobalScope.launch(Dispatchers.IO) {
        val rc = executeFFmpegCommand(command)
        withContext(Dispatchers.Main) {
            uploading.dismiss()
            handleFFmpegResult(rc, outputUri, videoUri, context)
        }
    }

    return outputUri!!
}

private fun executeFFmpegCommand(command: Array<String?>): Int {
    return FFmpeg.execute(command)
}

private fun handleFFmpegResult(rc: Int, outputUri: String?, videoUri: String?, context: Context) {
    if (rc == 0) {
        Log.e("Variables.tag", "Command execution completed successfully.")
        outputUri?.let { saveVideoToGallery(it, context) }
        videoUri?.let { performDelete(it) }
    } else if (rc == 255) {
        Log.e("Variables.tag", "Command execution cancelled by user.")
    } else {
        Log.e(
            "Variables.tag",
            String.format(
                "Command execution failed with rc=%d and the output below.$rc",
                rc
            )
        )
    }
}

private fun getRealPathFromURI(context: Context, uri: Uri): String? {
    val projection = arrayOf(MediaStore.Video.Media.DATA)
    val cursor: Cursor = context.contentResolver.query(uri, projection, null, null, null)!!
    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
    cursor.moveToFirst()
    val path = cursor.getString(columnIndex)
    cursor.close()
    return path
}

private fun getOutputVideoPath(context: Context): String? {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val outputFileName = "watermarked_video_$timeStamp.mp4"
    val appFolderPath: String =
        context.getExternalFilesDir(null)!!.absolutePath // Get the app folder path
    return "$appFolderPath/$outputFileName"
}

private fun saveVideoToGallery(videoPath: String, context: Context) {
    val values = ContentValues()
    values.put(MediaStore.Video.Media.DATA, videoPath)
    values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
    context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
    Toast.makeText(context, "Video saved to gallery", Toast.LENGTH_SHORT).show()
}


private fun performDelete(videoUri: String) {
    /* if (videoUri != null) {
            ContentResolver contentResolver = getContentResolver();
            if (contentResolver != null) {
                // Set the content Uri for video files in the MediaStore
                Uri mediaUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

                // Create the selection criteria
                String selection = MediaStore.Video.Media._ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(videoUri))};

                // Delete the video file
                int deletedRows = contentResolver.delete(mediaUri, selection, selectionArgs);

                // Check if the deletion was successful
                if (deletedRows > 0) {
                    Log.e("video", "Video deleted successfully");
                } else {
                    Log.e("video", "Video not deleted.");
                }
            }
        }*/
    val videoFile = File(videoUri)
    if (videoFile.exists()) {
        if (videoFile.canWrite()) {
            if (videoFile.delete()) {
                // Toast.makeText(this, "Video deleted successfully", Toast.LENGTH_SHORT).show()
                Log.e("video delete", "Video deleted successfully")
            } else {
                // Toast.makeText(this, "Video not deleted.", Toast.LENGTH_SHORT).show()
                Log.e("video delete", "Video not deleted.")
            }
        }
    } else {
        //Toast.makeText(this, "Video not found.", Toast.LENGTH_SHORT).show()
        Log.e("video delete", "Video not found.")
    }
}