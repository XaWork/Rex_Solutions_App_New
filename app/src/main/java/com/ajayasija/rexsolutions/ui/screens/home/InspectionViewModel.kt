package com.ajayasija.rexsolutions.ui.screens.home

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajayasija.rexsolutions.data.DBHandler
import com.ajayasija.rexsolutions.data.Resource
import com.ajayasija.rexsolutions.data.UserPref
import com.ajayasija.rexsolutions.domain.model.AllocationImageAwsModel
import com.ajayasija.rexsolutions.domain.model.ImageData
import com.ajayasija.rexsolutions.domain.model.InspectionHistoryModel
import com.ajayasija.rexsolutions.domain.model.InspectionLeadModel
import com.ajayasija.rexsolutions.domain.repository.AppRepo
import com.ajayasija.rexsolutions.ui.components.currentLocation
import com.ajayasija.rexsolutions.ui.components.gpsEnabled
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class InspectionViewModel @Inject constructor(
    private val repository: AppRepo,
    val userPref: UserPref
) : ViewModel() {

    var state by mutableStateOf(HomeState())
    private var responseList = emptyList<AllocationImageAwsModel>()

    fun onEvent(events: HomeEvents) {
        when (events) {
            is HomeEvents.LogOut -> {
                logOut()
            }

            is HomeEvents.GetLocation -> {
                currentLocation(events.context)
            }

            is HomeEvents.UploadVideo -> {
                //state = state.copy(error = uploadVideo(events.path))
                uploadVideos(events.file)
            }

            is HomeEvents.UploadVehMedia -> {
                uploadVehMedia(events.context)
            }

            is HomeEvents.GetInspectionHistory -> {
                inspectionHistory(events.dateFrom, events.dateTo, events.context)
            }

            is HomeEvents.SaveToLocal -> {
                setAllocation(
                    lead = state.acceptedLead!!,
                    "Y",
                    context = events.context,
                    location = events.location
                )
                saveToLocal(events.images, context = events.context)
            }

            is HomeEvents.ChangeAccept -> {
                state = state.copy(accept = false)
            }

            is HomeEvents.Inspection -> {
                getDataFromDB(events.context)
            }

            is HomeEvents.SetAllocation -> {
                val lead = events.lead
                setAllocation(lead, events.status, events.context)
            }

            is HomeEvents.AcceptLead -> {
                val lead = events.lead
                state = state.copy(acceptedLead = lead, accept = true)
            }
        }
    }

    private fun uploadVehMedia(context: Context, video: Boolean = false) {
        val dbHandler = DBHandler(context)

        if (state.imageCount > 0) {
            //  state = state.copy(isLoading = true)

            val alImageData: ArrayList<ImageData> = dbHandler.imageUploadDetails
            Log.e("image data before", alImageData.toString())
            // alImageData.sortBy { it.ImageName!! }
            //Log.e("image data after", alImageData.toString())
            state = state.copy(isLoading = true, allImageData = alImageData)
            if (alImageData.size > 0) {
                for (i in alImageData.indices) {
                    Log.e("image data one by one", alImageData[i].toString())
                    val imageData: ImageData = alImageData[i]
                    val imageFile = File(imageData.ImagePath)
                    if (imageFile.exists()) {
                        state = state.copy(uploadImageData = imageData)
                        uploadDataToAws(context, imageData, dbHandler)
                    }
                }
                Log.e("Upload", responseList.toString());
            }
        }

    }

    private fun uploadDataToAws(context: Context, imageData: ImageData, dbHandler: DBHandler) {
        var uploadObserver: TransferObserver?
        try {
            var count: Int = state.imageCount
            var increasing = 1
            val transferUtility = TransferUtility.builder()
                .defaultBucket("rexsolution")
                .context(context)
                .s3Client(
                    AmazonS3Client(
                        BasicAWSCredentials(
                            "AKIA6L3OZX46QMLGQD5G",
                            "0OPxnbtQtv1fkqTTTQPEw9ycx9czZSCGPe/4aTJy"
                        )
                    )
                )
                .build()
            count -= 1
            increasing += 1
            val image: File = File(imageData.ImagePath)
            uploadObserver = transferUtility.upload(imageData.ImageName, image)
            uploadObserver.setTransferListener(object : TransferListener {
                override fun onStateChanged(id: Int, transformState: TransferState) {
                    if (TransferState.COMPLETED == transformState) {
                        //Toast.makeText(getApplicationContext(), "Upload Completed!", Toast.LENGTH_SHORT).show();
                        Log.d("Upload", "completed")
                        image.delete()
                        val update: Boolean =
                            dbHandler.updateUploadStatus(Integer.valueOf(imageData.RowId))
                        Log.d("Image", "status:$update")
                        //updateProgress()

                        uploadDataToServer(imageData)
                        //update state
                        val remainingCount = state.remainingCount
                        state = state.copy(
                            remainingCount = remainingCount - 1,
                            pendingLeads = if (remainingCount > 0) remainingCount - 1 else remainingCount
                        )
                    } else if (TransferState.FAILED == transformState) {
                        //  file.delete();
                        Log.d("Upload", "failed")
                    }
                }

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                    val percentDonef = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                    val percentDone = percentDonef.toInt()
                    state = state.copy(uploadProgress = percentDone)
                    Log.d("Upload", ":$percentDone")


                    //   tvFileName.setText("ID:" + id + "|bytesCurrent: " + bytesCurrent + "|bytesTotal: " + bytesTotal + "|" + percentDone + "%");
                }

                override fun onError(id: Int, ex: java.lang.Exception) {
                    ex.printStackTrace()
                    Log.d("Error upload", ":$ex")
                }
            })
        } catch (e: java.lang.Exception) {
            Log.d("AWS", "error:$e")
        }
    }

    private fun logOut() {
        userPref.logOut()
    }

    private fun uploadDataToServer(imageData: ImageData) {

        var map = hashMapOf(
            "fldiLeadId" to imageData.LeadId!!,
            "fldiMemId" to userPref.getUser()!!.DATA_STATUS.member_id,
            "fldvRgBkImg" to "${imageData.ImageName}",
        )
        viewModelScope.launch {
            repository.allocationImageAws(map)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }

                        is Resource.Success -> {
                            Log.e("response home", result.data.toString())
                            //  responseList.add(result.data!!)
                            state = state.copy(
                                isLoading = false,
                                allocationImageAws = result.data,
                                error = null,
                            )
                        }

                        is Resource.Error -> {
                            state = state.copy(
                                isLoading = false,
                                error = result.message,
                                lead = null
                            )
                        }
                    }
                }
        }
    }

    private fun uploadVideos(file: File) {
        viewModelScope.launch {
            repository.uploadVideo(state.acceptedLead!!.fldiLeadId, file)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }

                        is Resource.Success -> {
                            state = state.copy(
                                isLoading = false,
                                uploadVideo = result.data,
                                error = null,
                            )
                        }

                        is Resource.Error -> {
                            state = state.copy(
                                isLoading = false,
                                error = result.message,
                                lead = null
                            )
                        }
                    }
                }
        }
    }


    private fun uploadVideo(file: String): String? {
        var returnValue = ""
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            val UPLOAD_URL =
                "http://rexsolution.co.in/android/allocation_video_aws.php?fldiLeadId=${state.acceptedLead?.fldiLeadId}"

            var serverResponseCode = 0
            //LeadId = fldiLeadId;
            val L = "3474"
            var conn: HttpURLConnection? = null
            var dos: DataOutputStream? = null
            val lineEnd = "\r\n"
            val twoHyphens = "--"
            val boundary = "*****"
            var bytesRead: Int
            var bytesAvailable: Int
            var bufferSize: Int
            val buffer: ByteArray
            val maxBufferSize = 1 * 1024 * 1024 * 1024
            val sourceFile = File(file)
            if (!sourceFile.isFile) {
                Log.e("Huzza", "Source File Does not exist")
                state = state.copy(isLoading = false)
                returnValue = ""
            }
            try {
                val fileInputStream = FileInputStream(sourceFile)
                val url = URL(UPLOAD_URL)
                conn = url.openConnection() as HttpURLConnection
                conn.doInput = true
                conn!!.doOutput = true
                conn.useCaches = false
                conn.requestMethod = "POST"
                conn.setRequestProperty("Connection", "Keep-Alive")
                conn.setRequestProperty("ENCTYPE", "multipart/form-data")
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=$boundary")
                conn.setRequestProperty("fldvVideo", file)
                dos = DataOutputStream(conn.outputStream)
                dos.writeBytes(twoHyphens + boundary + lineEnd)
                dos.writeBytes("Content-Disposition: form-data; name=fldvVideo;filename=$file$lineEnd")
                //dos.writeBytes("Content-Disposition: form-data; name=\"fldvVideo\";filename=\"" + fileName +"fldiLeadId" lineEnd);
                dos.writeBytes(lineEnd)
                bytesAvailable = fileInputStream.available()
                Log.i("Huzza", "Initial .available : $bytesAvailable")
                bufferSize = Math.min(bytesAvailable, maxBufferSize)
                buffer = ByteArray(bufferSize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize)
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize)
                    bytesAvailable = fileInputStream.available()
                    bufferSize = Math.min(bytesAvailable, maxBufferSize)
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize)
                }
                dos.writeBytes(lineEnd)
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)
                serverResponseCode = conn.responseCode
                fileInputStream.close()
                dos.flush()
                dos.close()
            } catch (ex: MalformedURLException) {
                ex.printStackTrace()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            state = state.copy(isLoading = false)
            if (serverResponseCode == 200) {
                val sb = StringBuilder()
                try {
                    val rd = BufferedReader(
                        InputStreamReader(
                            conn
                                ?.inputStream
                        )
                    )
                    var line: String?
                    while (rd.readLine().also { line = it } != null) {
                        sb.append(line)
                    }
                    rd.close()
                } catch (ioex: IOException) {
                    Log.e("video", ioex.toString())
                    ioex.printStackTrace()
                }
                //Toast.makeText(UploadImagesActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
                returnValue = sb.toString()
            } else {
                // Toast.makeText(UploadImagesActivity.this,"Could not upload", Toast.LENGTH_SHORT).show();
                returnValue = "Could not upload"
            }
        }
        return returnValue
    }

    private fun getDataFromDB(
        context: Context
    ) {
        val dbHandler = DBHandler(context)
        val imageDetails = dbHandler.imageUploadDetails
        val totalLeadCount = dbHandler.leadCount
        val totalImageCount = dbHandler.imagesCount

        state = state.copy(
            imageCount = imageDetails.size,
            remainingCount = imageDetails.size,
            totalLeads = totalLeadCount,
            totalImages = totalImageCount,
            allImageData = imageDetails
        )

        Log.e("state", state.toString())

        getPreInspection()
    }

    private fun getPreInspection() {
        viewModelScope.launch {
            repository.getInspection(userPref.getUser()?.DATA_STATUS!!.member_id)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }

                        is Resource.Success -> {
                            state = state.copy(
                                isLoading = false,
                                lead = result.data,
                                homePendingLeads = result.data?.DATA_STATUS?.size!!,
                                error = null,
                            )
                        }

                        is Resource.Error -> {
                            state = state.copy(
                                isLoading = false,
                                error = result.message,
                                lead = null
                            )
                        }
                    }
                }
        }
    }

    private fun setAllocation(
        lead: InspectionLeadModel.DATASTATUS.Preinspection,
        status: String,
        context: Context,
        location: Location? = null
    ) {
        val map = hashMapOf(
            "fldiLeadId" to lead.fldiLeadId,
            "fldiTrnsId" to lead.fldiTrnsId,
            "fldcStatus" to status,
            "fldvExeRemark" to "",
            "flddPPDate" to lead.flddPPDate,
            "FldiVhId" to lead.fldiVhId,
            "fldiLat" to (location?.latitude?.toString() ?: ""),
            "fldiLong" to (location?.longitude?.toString() ?: ""),
        )
        viewModelScope.launch {
            repository.setAllocationStatus(map)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }

                        is Resource.Success -> {
                            state = state.copy(
                                isLoading = false,
                                lead = null,
                                pendingLeads = 0,
                                allocationStatus = result.data,
                                error = null,
                                accept = status == "Y",
                                acceptedLead = null //if (status == "Y") lead else
                            )
                            getPreInspection()
                        }

                        is Resource.Error -> {
                            state = state.copy(
                                isLoading = false,
                                error = result.message,
                                lead = null
                            )
                        }
                    }
                }
        }
    }

    // ------------------------------ Get Location --------------------------------

    private fun currentLocation(
        context: Context
    ) {
        state = state.copy(isLoading = true)
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
                fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    Log.e("location", "success to get location ${task.result}")
                    state = state.copy(isLoading = false, location = task.result)
                }
                    .addOnSuccessListener {
                        Log.e("location", "success to get location $it")
                        state = state.copy(isLoading = false, location = it)

                    }
                    .addOnFailureListener {
                        Log.e("location", "Failed to get location $it")
                        state = state.copy(isLoading = false, location = null)
                    }
            }
        }
    }

    // ------------------------------ Get History --------------------------------
    private fun inspectionHistory(startDate: String, endDate: String, context: Context) {

        val map = hashMapOf(
            "member_id" to userPref.getUser()!!.DATA_STATUS.member_id,
            "from_date" to startDate,
            "end_date" to endDate,
        )
        viewModelScope.launch {
            repository.inspectionHistory(map)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }

                        is Resource.Success -> {
                            Log.e("Sheet", "${result.data?.dATA}")
                            state = state.copy(
                                isLoading = false,
                                lead = null,
                                inspectionHistory = result.data,
                                error = null,
                            )
                            if (result.data?.dATA?.isNotEmpty() == true) {
                                val filePath =
                                    state.inspectionHistory?.dATA?.let {
                                        generateExcel(
                                            context,
                                            it
                                        )
                                    }
                                if (filePath != null) {
                                    provideDownload(context, filePath)
                                }
                            }
                        }

                        is Resource.Error -> {
                            state = state.copy(
                                isLoading = false,
                                error = result.message,
                                lead = null
                            )
                        }
                    }
                }
        }
    }

    private fun generateExcel(context: Context, data: List<InspectionHistoryModel.DATA>): String? {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Inspection Data")

        // Create headers
        val headerRow = sheet.createRow(0)
        val headers = arrayOf(
            "Full Name",
            "Member ID",
            "Vehicle No",
            "Company Name",
            "Customer Name",
            "Customer Address",
            "Date Time"
        )
        for ((index, header) in headers.withIndex()) {
            headerRow.createCell(index).setCellValue(header)
        }

        // Populate data
        var rowIndex = 1
        for (ite in data) {
            val row = sheet.createRow(rowIndex++)
            val item = ite.inspectionData
            row.createCell(0).setCellValue(item.fldvFullName)
            row.createCell(1).setCellValue(item.fldiMemId)
            row.createCell(2).setCellValue(item.vehicleNo)
            row.createCell(3).setCellValue(item.compName)
            row.createCell(4).setCellValue(item.customerName)
            row.createCell(5).setCellValue(item.customerAddress)
            row.createCell(6).setCellValue(item.customerDateTime)
            Log.e("Sheet", "row is : $row")
        }
        Log.e("Sheet", "Sheet is $sheet")


        // Save the workbook to a file
        val filePath =
            context.getExternalFilesDir(null)?.absolutePath + File.separator + "InspectionData.xlsx"
        val file = File(filePath)
        try {
            val outputStream = FileOutputStream(file)
            workbook.write(outputStream)
            outputStream.close()
            return filePath
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    private fun provideDownload(context: Context, filePath: String) {
        val file = File(filePath)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        context.grantUriPermission(context.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(
            uri,
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        context.startActivity(Intent.createChooser(intent, "Download Excel File"))
    }

    //---------------------- Save to local --------------------
    private fun saveToLocal(
        images: List<Uri>,
        context: Context
    ) {
        try {
            var dbHandler = DBHandler(context)
            val veh = state.acceptedLead!!
            state = state.copy(isLoading = true, error = null, uploadVideo = null)
            //upload images
            for (i in images.indices) {
                val myDir: File
                if (Build.VERSION_CODES.R > VERSION.SDK_INT) {
                    myDir = File(
                        Environment.getExternalStorageDirectory().path
                                + "//Rex"
                    )
                } else {
                    myDir = File(
                        (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path
                                + "//Rex")
                    )
                }
                if (!myDir.exists()) myDir.mkdir()
                // String root = Environment.getExternalStorageDirectory().toString();
                //  File myDir = new File(root + "/rexsolution");
                val timeStampFormat = SimpleDateFormat("ddMMyyyy", Locale.ENGLISH)
                val myDate = Date()
                val strDate = timeStampFormat.format(myDate)
                val imageName: String = veh.fldiVhId.uppercase() + "_" + strDate + "_" + i + ".jpg"
                val destFile = File(myDir, imageName)
                val uri: Uri = images[i]
                val srcFile: File = File(getPath(uri, context.contentResolver))
                Log.e("images", srcFile.toString())
                val `in`: InputStream = FileInputStream(srcFile)
                val out: OutputStream = FileOutputStream(destFile)


                // Copy the bits from instream to outstream
                val buf = ByteArray(1024)
                var len: Int
                while ((`in`.read(buf).also { len = it }) > 0) {
                    out.write(buf, 0, len)
                }
                `in`.close()
                out.close()
                Log.d("Out", "File:" + destFile.path)
                dbHandler.addInspection(
                    veh.fldiLeadId,
                    veh.fldiVhId,
                    imageName,
                    destFile.absolutePath,
                    upload_status = "N",
                    api_status = "N"
                )
                //  copyFile(destFile, srcFile);
                Log.d("Src", ":" + srcFile.absolutePath)
                Log.d("Dest", ":" + destFile.absolutePath)
                if (i == images.size - 1)
                    state = state.copy(isLoading = false, acceptedLead = null, error = null)
            }
            state = state.copy(isLoading = false, acceptedLead = null, error = null)
            /* int count=dbHandler.getLeadDetails();
            binding.tvSelectedImages.setText("Images Saved: "+count);*/
        } catch (e: Exception) {
            e.printStackTrace()
            state = state.copy(isLoading = false, error = e.message)
            Log.d("storing image", "error:$e")
        }
    }


    private fun getPath(
        uri: Uri,
        contentResolver: ContentResolver,
        args: String = MediaStore.Images.Media.DATA
    ): String? {
        var filePath: String? = null

        // Get the file path using the MediaStore API
        val projection = arrayOf(args)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                filePath = it.getString(columnIndex)
            }
        }

        Log.e("filepath", "file path of media is : $filePath")
        return filePath
    }

    private fun getVideoPath(
        uri: Uri,
        contentResolver: ContentResolver,
        args: String = MediaStore.Video.Media.DATA
    ): String? {
        var filePath: String? = null

        // Get the file path using the MediaStore API
        val projection = arrayOf(args)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(args)
                filePath = it.getString(columnIndex)
            }
        }

        Log.e("filepath", "file path of media is : $filePath")
        return filePath
    }

}

