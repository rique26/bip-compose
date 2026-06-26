package com.ebody.bip.features.schedule.di

import com.ebody.bip.core.domain.intelligence.repository.MedicationIntelligenceRepository
import com.ebody.bip.features.schedule.data.repository.MedicationIntelligenceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ScheduleIntelligenceModule {

    @Binds
    @Singleton
    abstract fun bindMedicationIntelligenceRepository(
        impl: MedicationIntelligenceRepositoryImpl
    ): MedicationIntelligenceRepository
}