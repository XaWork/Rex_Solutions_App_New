package com.ajayasija.rexsolutions.ui.screens.auth

import a2a.logistic.app.presentation.login.AuthEvents
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
}