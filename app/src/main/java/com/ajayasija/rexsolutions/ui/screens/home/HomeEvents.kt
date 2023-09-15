package com.ajayasija.rexsolutions.ui.screens.home

import android.content.Context
import android.location.Location
import android.net.Uri
import com.ajayasija.rexsolutions.domain.model.InspectionLeadModel
import java.io.File

sealed class HomeEvents {
    object LogOut : HomeEvents()
    object ChangeAccept : HomeEvents()
    data class SetAllocation(
        val lead: InspectionLeadModel.DATASTATUS.Preinspection,
        val status: String,
        val context: Context
    ):HomeEvents()
    data class AcceptLead(
        val lead: InspectionLeadModel.DATASTATUS.Preinspection,
    ):HomeEvents()
    data class UpdateImageUri(
        val imageUri: Uri,
        val index: Int,
        val context: Context
    ):HomeEvents()
    data class GetLocation(
       val context: Context
    ):HomeEvents()
    data class DeleteImageFromAws(
       val index: Int,
        val context: Context
    ):HomeEvents()

    data class UploadVideo(
        val file: File
    ):HomeEvents()
    data class Inspection(
        val context: Context
    ):HomeEvents()
    data class UploadVehMedia(
        val context: Context,
        val uri: Uri
    ):HomeEvents()
    data class SaveToLocal(
        var location: Location,
        var context: Context
    ):HomeEvents()
    data class GetInspectionHistory(
        val context: Context,
        val dateFrom: String,
        val dateTo: String
    ):HomeEvents()
}
