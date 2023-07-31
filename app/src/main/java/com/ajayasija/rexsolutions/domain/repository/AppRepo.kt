package com.ajayasija.rexsolutions.domain.repository

import com.ajayasija.rexsolutions.data.Resource
import com.ajayasija.rexsolutions.domain.model.LoginModel
import kotlinx.coroutines.flow.Flow

interface AppRepo {

    suspend fun login(username: String, password: String): Flow<Resource<LoginModel>>
}