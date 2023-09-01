package com.ajayasija.rexsolutions.domain.repository

import com.ajayasija.rexsolutions.data.Resource
import com.ajayasija.rexsolutions.domain.model.AllocationImageAwsModel
import com.ajayasija.rexsolutions.domain.model.AllocationStatusModel
import com.ajayasija.rexsolutions.domain.model.ChangePasswordModel
import com.ajayasija.rexsolutions.domain.model.InspectionHistoryModel
import com.ajayasija.rexsolutions.domain.model.InspectionLeadModel
import com.ajayasija.rexsolutions.domain.model.LoginModel
import com.ajayasija.rexsolutions.domain.model.RegisterModel
import com.ajayasija.rexsolutions.domain.model.VideoUploadModel
import kotlinx.coroutines.flow.Flow
import java.io.File

interface AppRepo {

    suspend fun login(username: String, password: String): Flow<Resource<LoginModel>>
    suspend fun changePassword(
        memberId: String,
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Flow<Resource<ChangePasswordModel>>

    suspend fun register(
        fullName: String,
        mobile: String,
        email: String,
        dob: String,
        panNo: String,
        panImg: String,
        aadharNo: String,
        aadharImg: String
    ): Flow<Resource<RegisterModel>>


    suspend fun getInspection(memberId: String): Flow<Resource<InspectionLeadModel>>
    suspend fun setAllocationStatus(map: HashMap<String, String>): Flow<Resource<AllocationStatusModel>>
    suspend fun allocationImageAws(map: HashMap<String, String>): Flow<Resource<AllocationImageAwsModel>>
    suspend fun inspectionHistory(map: HashMap<String, String>): Flow<Resource<InspectionHistoryModel>>
    suspend fun uploadVideo(leadId: String, videoFile: File): Flow<Resource<VideoUploadModel>>
}