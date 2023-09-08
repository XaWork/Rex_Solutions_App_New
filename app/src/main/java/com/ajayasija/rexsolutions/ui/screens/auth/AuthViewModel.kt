package com.ajayasija.rexsolutions.ui.screens.auth

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajayasija.rexsolutions.data.DBHandler
import com.ajayasija.rexsolutions.data.Resource
import com.ajayasija.rexsolutions.data.UserPref
import com.ajayasija.rexsolutions.domain.model.ImageData
import com.ajayasija.rexsolutions.domain.model.RegisterFormData
import com.ajayasija.rexsolutions.domain.repository.AppRepo
import com.ajayasija.rexsolutions.ui.components.gpsEnabled
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AppRepo,
    val userPref: UserPref
) : ViewModel() {

    var state by mutableStateOf(AuthState())

    fun onEvent(events: AuthEvents) {
        when (events) {
            is AuthEvents.Login -> {
                login(events.username, events.password)
            }

            is AuthEvents.GetLocation -> {
                currentLocation(events.context)
            }

            is AuthEvents.ChangePassword -> {
                changePassword(
                    userPref.getUser()?.DATA_STATUS!!.member_id,
                    events.oldPassword,
                    events.newPassword,
                    events.confirmPassword
                )
            }

            is AuthEvents.Register -> {
                validate(
                    events.context,
                    RegisterFormData(
                        events.fullName, events.mobile, events.email, events.dob, events.panNo,
                        events.panImg, events.aadharNO, events.aadharImg
                    )
                )
            }
        }
    }


    private fun currentLocation(
        context: Context
    ) {
        Log.e("TAG", "currentLocation method called")
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
                fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,
                    object : CancellationToken() {
                        override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                            CancellationTokenSource().token

                        override fun isCancellationRequested() = false
                    })
                    .addOnSuccessListener {
                        Log.e("location", "success to get location $it")
                        if (it != null) {
                            state = state.copy(isLoading = false, location = it)
                        }else{
                            state = state.copy(
                                isLoading = false,
                                error = "Failed to get location"
                            )
                        }
                    }
                    .addOnFailureListener {
                        Log.e("location", "Failed to get location $it")
                        state = state.copy(isLoading = false, location = null)
                    }
            } else {
                state = state.copy(isLoading = false, location = null)
            }
        } else {
            state = state.copy(isLoading = false, location = null)
            ActivityCompat.requestPermissions(
                context as Activity,  // Make sure context is an Activity
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                0  // You need to define a unique request code
            )
        }
    }

    private fun login(username: String, password: String) {
        viewModelScope.launch {
            repository.login(username, password)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }

                        is Resource.Success -> {
                            state = if (result.data?.DATA_STATUS?.error_sts == "1")
                                state.copy(isLoading = false, login = result.data, error = null)
                            else
                                state.copy(
                                    isLoading = false,
                                    login = null,
                                    error = result.data?.DATA_STATUS?.error_msg
                                )

                        }

                        is Resource.Error -> {
                            state = state.copy(
                                isLoading = false,
                                error = result.message,
                                login = null
                            )
                        }
                    }
                }
        }
    }

    private fun changePassword(
        memberId: String,
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            repository.changePassword(memberId, oldPassword, newPassword, confirmPassword)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }

                        is Resource.Success -> {
                            if (result.data?.changePassword?.errorSts == 0) {
                                state = state.copy(error = "Password change successful")
                            }
                        }

                        is Resource.Error -> {
                            state = state.copy(
                                isLoading = false,
                                error = result.message,
                                login = null
                            )
                        }
                    }
                }
        }
    }

    private fun validate(
        context: Context,
        registerFormData: RegisterFormData
    ) {
        registerFormData.run {
            if (fullName.isBlank() ||
                mobile.isBlank() ||
                email.isBlank() ||
                dob.isBlank() ||
                panNo.isBlank() ||
                aadharNo.isBlank()
            ) {
                state = state.copy(error = "Fill all required fields")
            } else if (mobile.length < 10)
                state = state.copy(error = "Enter valid mobile number")
            else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                state = state.copy(error = "Enter valid email address")
            else if (panNo.length < 10)
                state = state.copy(error = "Enter valid PAN no.")
            else if (aadharNo.length < 12)
                state = state.copy(error = "Enter valid Aadhar no.")
            else
                uploadDataToAws(context, registerFormData)
        }


    }

    private fun register(
        registerFormData: RegisterFormData
    ) {
        viewModelScope.launch {
            registerFormData.run {
                repository.register(
                    fullName,
                    mobile,
                    email,
                    dob,
                    panNo,
                    File(panImg).name,
                    aadharNo,
                    File(aadharImg).name
                )
                    .collect { result ->
                        when (result) {
                            is Resource.Loading -> {
                                state = state.copy(isLoading = result.isLoading)
                            }

                            is Resource.Success -> {
                                state = if (result.data?.actualinspection?.error_sts == 0)
                                    state.copy(
                                        isLoading = false,
                                        login = null,
                                        register = result.data,
                                        error = result.data.actualinspection.error_msg
                                    )
                                else
                                    state.copy(
                                        isLoading = false,
                                        login = null,
                                        error = result.data?.actualinspection?.error_msg
                                    )

                            }

                            is Resource.Error -> {
                                state = state.copy(
                                    isLoading = false,
                                    error = result.message,
                                    login = null
                                )
                            }
                        }
                    }
            }
        }
    }


    private fun uploadDataToAws(
        context: Context,
        registerFormData: RegisterFormData,
    ) {
        state = state.copy(isLoading = true)
        val registerImageList = listOf(registerFormData.aadharImg, registerFormData.panImg)
        for (img in registerImageList) {
            var uploadObserver: TransferObserver?
            try {
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
                val image = File(img)
                uploadObserver = transferUtility.upload(image.name, image)
                uploadObserver.setTransferListener(object : TransferListener {
                    override fun onStateChanged(id: Int, transformState: TransferState) {
                        if (TransferState.COMPLETED == transformState) {
                            Log.e("Transfer", "Successfully completed")
                        } else if (TransferState.FAILED == transformState) {
                            state =
                                state.copy(isLoading = false, error = "Failed to upload, Try again")
                            Log.e("Transfer", "failed to complete")
                        }
                    }

                    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                        val percentDonef = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                        val percentDone = percentDonef.toInt()
                        Log.e("Transfer", "Percent completed $percentDone")

                        //   tvFileName.setText("ID:" + id + "|bytesCurrent: " + bytesCurrent + "|bytesTotal: " + bytesTotal + "|" + percentDone + "%");
                    }

                    override fun onError(id: Int, ex: java.lang.Exception) {
                        state = state.copy(isLoading = false, error = "Error : $ex")
                        ex.printStackTrace()
                        Log.d("Error upload", ":$ex")
                    }
                })
            } catch (e: java.lang.Exception) {
                state = state.copy(isLoading = false, error = "Error : $e")
                Log.d("AWS", "error:$e")
            }
        }
        state = state.copy(isLoading = false)
        register(registerFormData)
    }


}