package com.ajayasija.rexsolutions.di

import com.ajayasija.rexsolutions.data.remote.ApiService
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl("http://rexsolution.co.in/android/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun buildOkHttpClient(): OkHttpClient {
        val httpClientBuilder = OkHttpClient.Builder()
        //set timeout
        httpClientBuilder
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)

        val loggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        httpClientBuilder.addInterceptor(loggingInterceptor)
        httpClientBuilder.addNetworkInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .addHeader("lang", "")
            val request = requestBuilder.build()
            chain.proceed(request)
        }.build()
        httpClientBuilder.addInterceptor(loggingInterceptor)
        return httpClientBuilder.build()
    }


    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create()
    }

 /*   @Provides
    @Singleton
    fun provideUploadImageApi(okHttpClient: OkHttpClient): UploadImageApi {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://omst5afyma.execute-api.ap-south-1.amazonaws.com/production/app/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create()
    }

    @Provides
    @Singleton
    fun provideManageUserUseCase(repository: ManageUserRepository): ManageUserUseCase {
        return ManageUserUseCase(
            logisticBoyListUseCase = LogisticBoyListUseCase(repository),
            employeeStatusChangeUseCase = EmployeeStatusChangeUseCase(repository)
        )
    }*/
}