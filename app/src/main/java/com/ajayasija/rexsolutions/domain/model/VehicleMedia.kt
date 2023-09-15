package com.ajayasija.rexsolutions.domain.model

import android.net.Uri

data class VehicleMedia(
    val index: Int = 0,
    val uri: Uri? = null,
    val progress: Int = 0,
    val title: String = "",
    val error: Boolean = false,
)
