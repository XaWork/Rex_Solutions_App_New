package com.ajayasija.rexsolutions.ui.screens.auth

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajayasija.rexsolutions.data.Resource
import com.ajayasija.rexsolutions.data.UserPref
import com.ajayasija.rexsolutions.domain.repository.AppRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

            is AuthEvents.Register -> {
                validate(
                    events.fullName, events.mobile, events.email, events.dob, events.panNo,
                    events.panImg, events.aadharNO, events.aadharImg
                )
            }
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

    private fun validate(
        fullName: String,
        mobile: String,
        email: String,
        dob: String,
        panNo: String,
        panImg: String,
        aadharNo: String,
        aadharImg: String
    ) {
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
            register(fullName, mobile, email, dob, panNo, panImg, aadharNo, aadharImg)

    }

    private fun register(
        fullName: String,
        mobile: String,
        email: String,
        dob: String,
        panNo: String,
        panImg: String,
        aadharNo: String,
        aadharImg: String
    ) {
        viewModelScope.launch {
            repository.register(fullName, mobile, email, dob, panNo, panImg, aadharNo, aadharImg)
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