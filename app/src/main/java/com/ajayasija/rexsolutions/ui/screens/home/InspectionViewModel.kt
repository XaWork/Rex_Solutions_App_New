package com.ajayasija.rexsolutions.ui.screens.home

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

            is HomeEvents.Inspection -> {
                getPreInspection()
            }
        }
    }

    private fun logOut() {
        userPref.logOut()
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
                                error = null
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

}