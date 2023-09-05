package com.ajayasija.rexsolutions.ui.components

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.io.File
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun getCurrentDateTime(video: Boolean = false): String? {
    val sdf = SimpleDateFormat(
        if (!video) "dd MMM yyyy hh:mm a" else "dd MMM yyyy hh\\:mm a",
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
fun addWatermarkToImage(uri: Uri, context: Context): Uri? {
    val originalBitmap = uriToBitmap(uri, context)

    val desiredWidth = 900
    val desiredHeight = 1000
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
    val location = getCurrentLocation(context)
    val latlng = "Lat: ${
        String.format(
            "%.7f",
            location?.latitude
        )
    }, Lng: ${
        String.format(
            "%.7f",
            location?.longitude
        )
    }"
    val dateTime = getCurrentDateTime()

    val watermarkText = "Rex solution\n$dateTime\n$latlng"
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

/*
@OptIn(DelicateCoroutinesApi::class)
fun addWatermarkToVideo(videoUri: String, context: Context): String {
    // Convert Uri to file path
    val outputUri = getOutputVideoPath(context)
    var location = getCurrentLocation(context, true)
    //currentLocation(onLocationReceived = { lat, lng -> location = "Lat: $lat, Lng: $lng" }, context)
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
}*/

fun getRealPathFromURI(context: Context, uri: Uri): String? {
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