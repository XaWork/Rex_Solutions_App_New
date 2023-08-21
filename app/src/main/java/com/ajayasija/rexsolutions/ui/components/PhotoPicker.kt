package com.ajayasija.rexsolutions.ui.components

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.Paint
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    onImageSelect: (Uri?) -> Unit,
    onMultipleImageSelect: (List<Uri>) -> Unit,
) {
    /*var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }*/
    val context = LocalContext.current

    Log.e("Permissions", "Button clicked media")

    var showDialog by remember { mutableStateOf(false) }

    Log.e("photo", "Launch : $launch\npick Mutlitple : $pickMultiple\nvideo: $video")

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                onImageSelect(addWatermarkToBitmap(uri, context))
            }
        }
    )

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> onImageSelect(uri) }
    )

    val multiPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(30),
        onResult = { uri -> onMultipleImageSelect(uri) }
    )


    if (launch) {
        if (!video) {
            if (AllPermissionGranted(
                    permissions = listOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA
                    )
                )
            ) {
                if (!pickMultiple)
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                else
                    multiPhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
            }
        } else {
            videoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
            )
        }

    }
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

@RequiresApi(Build.VERSION_CODES.P)
fun uriToBitmap(uri: Uri, context: Context): Bitmap {
    val contentResolver: ContentResolver = context.contentResolver

    val source = ImageDecoder.createSource(contentResolver, uri)
    return ImageDecoder.decodeBitmap(source)
}

@RequiresApi(Build.VERSION_CODES.Q)
fun bitmapToUri(bitmap: Bitmap, context: Context): Uri? {
    val displayName = "image.png"
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
private fun addWatermarkToBitmap(uri: Uri, context: Context): Uri? {
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


private fun getCurrentDateTime(): String? {
    val sdf = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault())
    return sdf.format(Date())
}