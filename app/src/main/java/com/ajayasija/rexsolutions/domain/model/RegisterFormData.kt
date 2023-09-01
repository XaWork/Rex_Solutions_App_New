package com.ajayasija.rexsolutions.domain.model

data class RegisterFormData(
    val fullName: String,
    val mobile: String,
    val email: String,
    val dob: String,
    val panNo: String,
    val panImg: String,
    val aadharNo: String,
    val aadharImg: String,
)
