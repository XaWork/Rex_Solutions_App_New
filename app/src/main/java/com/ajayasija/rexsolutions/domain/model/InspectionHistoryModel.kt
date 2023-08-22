package com.ajayasija.rexsolutions.domain.model


import com.google.gson.annotations.SerializedName

data class InspectionHistoryModel(
    @SerializedName("DATA")
    val dATA: List<DATA>
) {
    data class DATA(
        @SerializedName("inspection_data")
        val inspectionData: InspectionData
    ) {
        data class InspectionData(
            @SerializedName("comp_name")
            val compName: String,
            @SerializedName("comp_ref_no")
            val compRefNo: Any,
            @SerializedName("customer_address")
            val customerAddress: String,
            @SerializedName("customer_date_time")
            val customerDateTime: String,
            @SerializedName("customer_name")
            val customerName: String,
            @SerializedName("fldiMemId")
            val fldiMemId: String,
            @SerializedName("fldvFullName")
            val fldvFullName: String,
            @SerializedName("vehicle_no")
            val vehicleNo: String
        )
    }
}