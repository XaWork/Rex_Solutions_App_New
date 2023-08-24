package com.ajayasija.rexsolutions.domain.model


import com.google.gson.annotations.SerializedName

data class VideoUploadModel(
    @SerializedName("actualinspection")
    val actualinspection: Actualinspection
) {
    data class Actualinspection(
        @SerializedName("error_msg")
        val errorMsg: String,
        @SerializedName("error_sts")
        val errorSts: String,
        @SerializedName("fldiLeadId")
        val fldiLeadId: String,
        @SerializedName("fldvFileName")
        val fldvFileName: String
    )
}