package com.ajayasija.rexsolutions.data.remote

import com.ajayasija.rexsolutions.domain.model.AllocationImageAwsModel
import com.ajayasija.rexsolutions.domain.model.AllocationStatusModel
import com.ajayasija.rexsolutions.domain.model.InspectionHistoryModel
import com.ajayasija.rexsolutions.domain.model.InspectionLeadModel
import com.ajayasija.rexsolutions.domain.model.LoginModel
import com.ajayasija.rexsolutions.domain.model.RegisterModel
import com.ajayasija.rexsolutions.domain.model.VideoUploadModel
import okhttp3.MultipartBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiService {

    @GET("login_handler.php")
    suspend fun login(
        @Query("user_name") userName: String,
        @Query("user_pass") userPass: String
    ): LoginModel

    @FormUrlEncoded
    @POST("register_executive.php")
    suspend fun register(
        @Field("full_name") fullName: String,
        @Field("mobile_no") mobile: String,
        @Field("email_id") email: String,
        @Field("user_dob") dob: String,
        @Field("pan_no") panNo: String,
        @Field("pan_no_file") panImg: String,
        @Field("aadhar_no") aadharNo: String,
        @Field("aadhar_no_file") aadharImg: String
    ): RegisterModel


    @GET("allocation_executive.php")
    suspend fun getInspection(
        @Query("member_id") userName: String,
    ): InspectionLeadModel

    @GET("allocation_status.php")
    suspend fun setAllocationStatus(
        @QueryMap map: HashMap<String, String>,
    ): AllocationStatusModel

    @GET("allocation_image_aws.php")
    suspend fun allocationImageAws(
        @QueryMap map: HashMap<String, String>,
    ): AllocationImageAwsModel

    @GET("inspection_history.php")
    suspend fun inspectionHistory(
        @QueryMap map: HashMap<String, String>,
    ): InspectionHistoryModel

    @Multipart
    @POST("allocation_video_aws.php")
    suspend fun uploadVideo(
        @Query("fldiLeadId") leadId: String,
        @Part videoPart: MultipartBody.Part
    ): VideoUploadModel

}