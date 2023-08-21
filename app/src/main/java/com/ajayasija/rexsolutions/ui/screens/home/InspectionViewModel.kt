package com.ajayasija.rexsolutions.ui.screens.home

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajayasija.rexsolutions.data.DBHandler
import com.ajayasija.rexsolutions.data.Resource
import com.ajayasija.rexsolutions.data.UserPref
import com.ajayasija.rexsolutions.domain.model.ImageData
import com.ajayasija.rexsolutions.domain.model.InspectionLeadModel
import com.ajayasija.rexsolutions.domain.repository.AppRepo
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
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

    fun onEvent(events: HomeEvents) {
        when (events) {
            is HomeEvents.LogOut -> {
                logOut()
            }

            is HomeEvents.UploadVehMedia -> {
                uploadVehMedia(events.context)
            }

            is HomeEvents.SaveToLocal -> {
                saveToLocal(events.images, context = events.context, video = events.video)
            }

            is HomeEvents.ChangeAccept -> {
                state = state.copy(accept = false)
            }

            is HomeEvents.Inspection -> {
                getDataFromDB(events.context)
            }

            is HomeEvents.SetAllocation -> {
                var lead = state.lead!!.DATA_STATUS[events.index].preinspection
                setAllocation(lead, events.status)
            }
        }
    }

    private fun uploadVehMedia(context: Context) {
        if (state.imageCount > 0) {
            //  state = state.copy(isLoading = true)
            val dbHandler = DBHandler(context)

            val alImageData: ArrayList<ImageData> = dbHandler.imageUploadDetails
            state = state.copy(allImageData = alImageData)
            if (alImageData.size > 0) {
                for (i in alImageData.indices) {
                    val imageData: ImageData = alImageData[i]
                    val imageFile = File(imageData.ImagePath)
                    if (imageFile.exists()) {
                        state = state.copy(uploadImageData = imageData)
                        uploadDataToAws(context, imageData, dbHandler)
                    }
                }
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
            "fldvRgBkImg" to "${imageData.ImageName}${imageData.LeadId}${imageData.ImageName}",
        )
        viewModelScope.launch {
            repository.allocationImageAws(map)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }

                        is Resource.Success -> {
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
                                pendingLeads = result.data?.DATA_STATUS?.size!!,
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

    private fun setAllocation(lead: InspectionLeadModel.DATASTATUS.Preinspection, status: String) {

        var map = hashMapOf(
            "fldiLeadId" to lead.fldiLeadId,
            "fldiTrnsId" to lead.fldiTrnsId,
            "fldcStatus" to status,
            "fldvExeRemark" to "",
            "flddPPDate" to lead.flddPPDate,
            "FldiVhId" to lead.fldiVhId,
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
                                acceptedLead = if (status == "Y") lead else null
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

    private fun inspectionHistory(startDate: String, endDate: String) {

        var map = hashMapOf(
            "member_id" to userPref.getUser()!!.DATA_STATUS!!.member_id,
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
                            state = state.copy(
                                isLoading = false,
                                lead = null,
                                inspectionHistory = result.data,
                                error = null,
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

    private fun saveToLocal(
        images: List<Uri>,
        video: Uri? = null,
        context: Context
    ) {
        state = state.copy(isLoading = true, error = null)
        var dbHandler = DBHandler(context)
        val veh = state.acceptedLead!!
        try {
            if (video != null) {
                val myDir: File =
                    if (Build.VERSION_CODES.R > VERSION.SDK_INT) {
                        File(
                            Environment.getExternalStorageDirectory().path
                                    + "//Rex"
                        )
                    } else {
                        File(
                            (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path
                                    + "//Rex")
                        )
                    }
                if (!myDir.exists())
                    myDir.mkdir()

                val timeStampFormat = SimpleDateFormat("ddMMyyyy", Locale.ENGLISH)
                val myDate = Date()
                val strDate = timeStampFormat.format(myDate)
                val videoName: String = veh.fldiVhId + "_" + strDate + "_" + video + ".mp4"
                val destFile = File(myDir, videoName)
                val out: OutputStream = FileOutputStream(destFile)
                val uri: Uri = video/*
                val srcFile =
                    File(getPath(uri, context.contentResolver, args = MediaStore.Video.Media.DATA))*/
                // Log.e("videoName", srcFile.toString())
                /*val `in`: InputStream? = context.contentResolver.openInputStream(video)

                if (`in` != null) {
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
                        videoName,
                        destFile.absolutePath,
                        "N",
                        "N"
                    )
                    //  copyFile(destFile, srcFile);
                  //  Log.d("Src", ":" + srcFile.absolutePath)
                    Log.d("Dest", ":" + destFile.absolutePath)
                }else{
                    Log.d("input", "INput stream is null")
                }*/

            }

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
                val imageName: String = veh.fldiVhId + "_" + strDate + "_" + i + ".jpg"
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
                    "N",
                    "N"
                )
                //  copyFile(destFile, srcFile);
                Log.d("Src", ":" + srcFile.absolutePath)
                Log.d("Dest", ":" + destFile.absolutePath)
                if (i == images.size - 1)
                    state = state.copy(isLoading = false, acceptedLead = null, error = null)
            }
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

}