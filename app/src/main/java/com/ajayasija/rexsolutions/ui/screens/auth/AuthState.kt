package com.ajayasija.rexsolutions.ui.screens.auth

import com.ajayasija.rexsolutions.domain.model.LoginModel

data class AuthState(
    val isLoading: Boolean = false,
    val gotOtp: Boolean = false,
    val error: String? = null,
    var login: LoginModel? = null
)
