package com.ajayasija.rexsolutions.ui.screens.auth

sealed class AuthEvents {
    data class Login(val username: String, val password: String) : AuthEvents()
    data class Register(
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
