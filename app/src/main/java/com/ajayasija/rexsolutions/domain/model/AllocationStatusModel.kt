package com.ajayasija.rexsolutions.domain.model

data class AllocationStatusModel(
    val preinspection: Preinspection
) {
    data class Preinspection(
        val error_msg: String,
        val error_sts: String,
        val fldcStatus: String,
        val flddPPDate: String,
        val fldiLeadId: String,
        val fldiTrnsId: String,
        val fldvExeRemark: String
    )
}