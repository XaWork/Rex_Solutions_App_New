package com.ajayasija.rexsolutions.domain.model


import com.google.gson.annotations.SerializedName

data class ChangePasswordModel(
    @SerializedName("change_password")
    val changePassword: ChangePassword
) {
    data class ChangePassword(
        @SerializedName("error_msg")
        val errorMsg: String,
        @SerializedName("error_sts")
        val errorSts: Int,
        @SerializedName("member_id")
        val memberId: String
    )
}