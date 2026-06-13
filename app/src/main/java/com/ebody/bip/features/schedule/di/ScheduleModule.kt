package com.ebody.bip.features.schedule.di

import com.ebody.bip.core.data.local.database.BipDatabase
import com.ebody.bip.features.schedule.data.local.MedicationDao
import com.ebody.bip.features.schedule.data.repository.MedicationRepositoryImpl
import com.ebody.bip.features.schedule.domain.MedicationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScheduleModule {


    @Provides
    @Singleton
    fun provideMedicationRepository(
        medicationDao: MedicationDao
    ): MedicationRepository {
        return MedicationRepositoryImpl(medicationDao)
    }
}