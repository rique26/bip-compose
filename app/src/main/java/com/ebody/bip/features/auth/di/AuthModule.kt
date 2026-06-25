package com.ebody.bip.features.auth.di

import com.ebody.bip.features.auth.data.firebase.FirebaseAuthManager
import com.ebody.bip.features.auth.data.firebase.FirebaseAuthManagerImpl
import com.ebody.bip.features.auth.domain.repository.AuthRepository
import com.ebody.bip.features.auth.data.repository.AuthRepositoryImpl
import com.ebody.bip.features.auth.util.AndroidEmailValidator
import com.ebody.bip.features.auth.util.EmailValidator
import com.ebody.bip.features.auth.data.datasource.remote.FirebaseAuthRemoteDataSource
import com.ebody.bip.features.auth.data.datasource.remote.FirebaseAuthRemoteDataSourceImpl
import com.ebody.bip.features.auth.data.repository.SessionManagerImpl
import com.ebody.bip.features.auth.domain.repository.SessionManager
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
    abstract fun bindSessionManager(
        impl: SessionManagerImpl
    ): SessionManager

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

    companion object {
        @Provides
        @Singleton
        fun provideEmailValidator(): EmailValidator {
            return AndroidEmailValidator()
        }
    }
}