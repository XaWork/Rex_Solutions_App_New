package com.ajayasija.rexsolutions.domain.model

data class RegisterModel(
    val actualinspection: Actualinspection
) {
    data class Actualinspection(
        val error_msg: String,
        val error_sts: Int,
        val member_id: Int
    )
}