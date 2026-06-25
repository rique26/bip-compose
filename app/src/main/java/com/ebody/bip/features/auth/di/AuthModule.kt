package com.ebody.bip.features.auth.di

import com.ebody.bip.features.auth.data.datasource.local.AuthDataStore
import com.ebody.bip.features.auth.data.datasource.local.AuthDataStoreImpl
import com.ebody.bip.features.auth.data.firebase.FirebaseAuthManager
import com.ebody.bip.features.auth.data.firebase.FirebaseAuthManagerImpl
import com.ebody.bip.features.auth.domain.repository.AuthRepository
import com.ebody.bip.features.auth.data.repository.AuthRepositoryImpl
import com.ebody.bip.features.auth.util.AndroidEmailValidator
import com.ebody.bip.features.auth.util.EmailValidator
import com.ebody.bip.features.auth.data.datasource.local.AuthLocalDataSource
import com.ebody.bip.features.auth.data.datasource.local.AuthLocalDataSourceImpl
import com.ebody.bip.features.auth.data.datasource.remote.FirebaseAuthRemoteDataSource
import com.ebody.bip.features.auth.data.datasource.remote.FirebaseAuthRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindFirebaseAuthManager(
        impl: FirebaseAuthManagerImpl
    ): FirebaseAuthManager

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindFirebaseAuthRemoteDataSource(
        impl: FirebaseAuthRemoteDataSourceImpl
    ): FirebaseAuthRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindAuthLocalDataSource(
        impl: AuthLocalDataSourceImpl
    ): AuthLocalDataSource

    @Binds
    @Singleton
    abstract fun bindAuthDataStore(
        impl: AuthDataStoreImpl
    ): AuthDataStore

    companion object {
        @Provides
        @Singleton
        fun provideEmailValidator(): EmailValidator {
            return AndroidEmailValidator()
        }
    }
}