package com.ajayasija.rexsolutions.ui.screens.auth

import android.location.Location
import com.ajayasija.rexsolutions.domain.model.LoginModel
import com.ajayasija.rexsolutions.domain.model.RegisterModel

data class AuthState(
    val isLoading: Boolean = false,
    val gotOtp: Boolean = false,
    val error: String? = null,
    val location: Location? = null,
    var login: LoginModel? = null,
    var register: RegisterModel? = null
)
