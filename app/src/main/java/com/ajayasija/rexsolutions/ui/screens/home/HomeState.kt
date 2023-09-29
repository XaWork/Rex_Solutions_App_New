package com.ajayasija.rexsolutions.ui.screens.home

import android.location.Location
import com.ajayasija.rexsolutions.domain.model.AllocationImageAwsModel
import com.ajayasija.rexsolutions.domain.model.AllocationStatusModel
import com.ajayasija.rexsolutions.domain.model.ImageData
import com.ajayasija.rexsolutions.domain.model.InspectionHistoryModel
import com.ajayasija.rexsolutions.domain.model.InspectionLeadModel
import com.ajayasija.rexsolutions.domain.model.VehicleMedia
import com.ajayasija.rexsolutions.domain.model.VideoUploadModel
import com.ajayasija.rexsolutions.ui.screens.inspection_lead.titleList

data class HomeState(
    val isLoading: Boolean = false,
    val accept: Boolean = false,
    val videoUploadSuccessfully: Boolean = false,
    val error: String? = null,
    val location: Location? = null,
    val lead: InspectionLeadModel? = null,
    val allocationStatus: AllocationStatusModel? = null,
    val allocationImageAws: AllocationImageAwsModel? = null,
    val uploadVideo: VideoUploadModel? = null,
    val inspectionHistory: InspectionHistoryModel? = null,
    val acceptedLead: InspectionLeadModel.DATASTATUS.Preinspection? = null,
    val pendingLeads: Int = 0,
    val homePendingLeads: Int = 0,
    val imageCount: Int = 0,
    val totalLeads: Int = 0,
    val totalImages: Int = 0,
    val remainingCount: Int = 0,
    val uploadProgress: Int = 0,
    val uploadImageName: String = "",
    val uploadImageData: ImageData? = null,
    val allImageData: List<ImageData> = emptyList(),
    val vehicleMediaList: List<VehicleMedia> = titleList.mapIndexed { index, title ->
        VehicleMedia(
            index = index,
            title = title
        )
    }
)
