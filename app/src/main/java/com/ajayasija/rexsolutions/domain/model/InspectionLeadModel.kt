package com.ajayasija.rexsolutions.domain.model

data class InspectionLeadModel(
    val DATA_STATUS: List<DATASTATUS>
) {
    data class DATASTATUS(
        val preinspection: Preinspection
    ) {
        data class Preinspection(
            val fldcCashRec: String,
            val fldcStatus: String,
            val flddCustApDateTime: String,
            val flddIntDateTime: String,
            val flddPPDate: String,
            val fldiCashAmt: String,
            val fldiLeadId: String,
            val fldiLocation: String,
            val fldiMemId: String,
            val fldiTrnsId: String,
            val fldiVhId: String,
            val fldvCallDetails: String,
            val fldvCallMobile: String,
            val fldvCallName: String,
            val fldvFullName: String,
            val fldvInsuredDetails: String,
            val fldvInsuredMobile: String,
            val fldvInsuredName: String,
            val fldvInsurrBranch: String,
            val fldvInsurrComId: String,
            val fldvInsurrComName: String,
            val fldvInsurrDivision: String,
            val fldvLandMark: String,
            val fldvLdSrc: String,
            val fldvLeadNo: String,
            val fldvLocation: String,
            val fldvLocationLine: String,
            val fldvRefNo: String,
            val fldvRemaks: String,
            val fldvRemarks: String,
            val fldvVhFuelType: String,
            val fldvVhModel: String,
            val fldvVhName: String,
            val fldvVhRegNo: String,
            val fldvVhType: String
        )
    }
}