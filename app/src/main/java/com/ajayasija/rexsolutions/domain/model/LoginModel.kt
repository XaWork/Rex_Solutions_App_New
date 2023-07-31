package com.ajayasija.rexsolutions.domain.model

data class LoginModel(
    val DATA_STATUS: DATASTATUS
) {
    data class DATASTATUS(
        val date_join: String,
        val error_msg: String,
        val error_sts: String,
        val full_name: String,
        val member_id: String,
        val user_name: String
    )
}