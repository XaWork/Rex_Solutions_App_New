package com.ajayasija.rexsolutions.di

import com.ajayasija.rexsolutions.data.reporitory.AppRepoImpl
import com.ajayasija.rexsolutions.domain.repository.AppRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAppRepo(
        appReposImpl: AppRepoImpl
    ): AppRepo
}