package com.ajayasija.rexsolutions.domain.model

data class AllocationImageAwsModel(
    val actualinspection: Actualinspection
) {
    data class Actualinspection(
        val error_msg: String,
        val error_sts: String,
        val fldiLeadId: String,
        val fldiMemId: String
    )
}