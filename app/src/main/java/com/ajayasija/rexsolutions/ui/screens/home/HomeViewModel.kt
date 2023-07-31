package com.ajayasija.rexsolutions.ui.screens.home

import a2a.logistic.app.presentation.login.AuthEvents
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajayasija.rexsolutions.data.Resource
import com.ajayasija.rexsolutions.data.UserPref
import com.ajayasija.rexsolutions.domain.repository.AppRepo
import com.ajayasija.rexsolutions.ui.screens.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AppRepo,
    val userPref: UserPref
) : ViewModel() {

    var state by mutableStateOf(HomeState())

    fun onEvent(events: HomeEvents) {
        when (events) {
            is HomeEvents.LogOut -> {
                logOut()
            }
        }
    }

    private fun logOut() {
        userPref.logOut()
    }

}