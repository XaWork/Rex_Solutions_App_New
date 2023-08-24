package com.ajayasija.rexsolutions.ui.screens.home

import android.content.Context
import android.net.Uri
import java.io.File

sealed class HomeEvents {
    object LogOut : HomeEvents()
    object ChangeAccept : HomeEvents()

    data class SetAllocation(
        val index: Int,
        val status: String
    ):HomeEvents()

    data class UploadVideo(
        val file: File
    ):HomeEvents()
    data class Inspection(
        val context: Context
    ):HomeEvents()
    data class UploadVehMedia(
        val context: Context
    ):HomeEvents()
    data class SaveToLocal(
        var images: List<Uri> = emptyList(),
        var video: Uri? = null,
        var context: Context
    ):HomeEvents()
    data class GetInspectionHistory(
        val context: Context,
        val dateFrom: String,
        val dateTo: String
    ):HomeEvents()
}
