package com.ajayasija.rexsolutions.domain.model

data class InspectionHistoryModel(
    val DATa: List<DATA>
) {
    data class DATA(
        val inspection_data: InspectionData
    ) {
        data class InspectionData(
            val comp_name: String,
            val comp_ref_no: Any,
            val customer_address: String,
            val customer_date_time: String,
            val customer_name: String,
            val fldiMemId: String,
            val fldvFullName: String,
            val vehicle_no: String
        )
    }
}