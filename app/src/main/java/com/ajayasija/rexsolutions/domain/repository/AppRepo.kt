package com.ajayasija.rexsolutions.domain.repository

import com.ajayasija.rexsolutions.data.Resource
import com.ajayasija.rexsolutions.domain.model.InspectionLeadModel
import com.ajayasija.rexsolutions.domain.model.LoginModel
import com.ajayasija.rexsolutions.domain.model.RegisterModel
import kotlinx.coroutines.flow.Flow

interface AppRepo {

    suspend fun login(username: String, password: String): Flow<Resource<LoginModel>>
    suspend fun register(
        fullName: String,
        mobile: String,
        email: String,
        dob: String,
        panNo: String,
        panImg: String,
        aadharNo: String,
        aadharImg: String): Flow<Resource<RegisterModel>>


    suspend fun getInspection(memberId: String): Flow<Resource<InspectionLeadModel>>
}