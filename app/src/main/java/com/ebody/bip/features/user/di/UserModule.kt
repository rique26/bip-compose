package com.ebody.bip.features.user.di

import com.ebody.bip.features.user.data.datasource.remote.UserRemoteDataSource
import com.ebody.bip.features.user.data.datasource.remote.UserRemoteDataSourceImpl
import com.ebody.bip.features.user.data.repository.UserRepositoryImpl
import com.ebody.bip.features.user.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserModule {

    @Binds
    @Singleton
    abstract fun bindUserRemoteDataSource(
        impl: UserRemoteDataSourceImpl
    ): UserRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository
}