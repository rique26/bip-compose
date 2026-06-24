package com.ebody.bip.features.emergency.di

import com.ebody.bip.features.emergency.data.external.ExternalMessageSenderImpl
import com.ebody.bip.features.emergency.data.repository.EmergencyRepositoryImpl
import com.ebody.bip.features.emergency.domain.ExternalMessageSender
import com.ebody.bip.features.emergency.domain.repository.EmergencyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EmergencyModule {

    @Binds
    @Singleton
    abstract fun bindEmergencyRepository(
        impl: EmergencyRepositoryImpl
    ): EmergencyRepository

    @Binds
    @Singleton
    abstract fun bindExternalMessageSender(
        impl: ExternalMessageSenderImpl
    ): ExternalMessageSender
}