package com.ajayasija.rexsolutions.data.reporitory

import com.ajayasija.rexsolutions.data.Resource
import com.ajayasija.rexsolutions.data.remote.ApiService
import com.ajayasija.rexsolutions.domain.model.InspectionLeadModel
import com.ajayasija.rexsolutions.domain.model.LoginModel
import com.ajayasija.rexsolutions.domain.model.RegisterModel
import com.ajayasija.rexsolutions.domain.repository.AppRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AppRepoImpl @Inject constructor(
    private val api: ApiService
) : AppRepo {

    override suspend fun login(username: String, password: String): Flow<Resource<LoginModel>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val response = api.login(username, password)
                emit(Resource.Success(response))
            } catch (io: IOException) {
                io.printStackTrace()
                emit(Resource.Error(message = "Couldn't load data"))
            } catch (http: HttpException) {
                http.printStackTrace()
                emit(Resource.Error(message = "Couldn't load data"))
            }
        }
    }

    override suspend fun register(
        fullName: String,
        mobile: String,
        email: String,
        dob: String,
        panNo: String,
        panImg: String,
        aadharNo: String,
        aadharImg: String
    ): Flow<Resource<RegisterModel>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val response = api.register(fullName, mobile, email, dob, panNo, panImg, aadharNo, aadharImg)
                emit(Resource.Success(response))
            } catch (io: IOException) {
                io.printStackTrace()
                emit(Resource.Error(message = "Couldn't load data"))
            } catch (http: HttpException) {
                http.printStackTrace()
                emit(Resource.Error(message = "Couldn't load data"))
            }
        }
    }

    override suspend fun getInspection(memberId: String): Flow<Resource<InspectionLeadModel>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val response = api.getInspection(memberId)
                emit(Resource.Success(response))
            } catch (io: IOException) {
                io.printStackTrace()
                emit(Resource.Error(message = "Couldn't load data"))
            } catch (http: HttpException) {
                http.printStackTrace()
                emit(Resource.Error(message = "Couldn't load data"))
            }
        }
    }

}