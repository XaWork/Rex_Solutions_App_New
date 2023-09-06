package com.ajayasija.rexsolutions.ui.screens.auth

import android.content.Context
import com.ajayasija.rexsolutions.ui.screens.home.HomeEvents

sealed class AuthEvents {
    data class Login(val username: String, val password: String) : AuthEvents()
    data class ChangePassword(
        val oldPassword: String,
        val newPassword: String,
        val confirmPassword: String
    ) : AuthEvents()
    data class GetLocation(
        val context: Context
    ): AuthEvents()

    data class Register(
        val context: Context,
        val fullName: String,
        val mobile: String,
        val email: String,
        val dob: String,
        val panNo: String,
        val panImg: String,
        val aadharNO: String,
        val aadharImg: String
    ) : AuthEvents()
}
