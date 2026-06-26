package com.ebody.bip.features.schedule.di

import com.ebody.bip.features.schedule.data.datasource.local.MedicationLocalDataSource
import com.ebody.bip.features.schedule.data.datasource.local.MedicationLocalDataSourceImpl
import com.ebody.bip.features.schedule.data.datasource.remote.MedicationFirebaseDataSource
import com.ebody.bip.features.schedule.data.datasource.remote.MedicationFirebaseDataSourceImpl
import com.ebody.bip.features.schedule.data.repository.MedicationRepositoryImpl
import com.ebody.bip.features.schedule.data.repository.ReminderRepositoryImpl
import com.ebody.bip.features.schedule.domain.repository.MedicationRepository
import com.ebody.bip.features.schedule.domain.repository.ReminderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ScheduleModule {

    @Binds
    @Singleton
    abstract fun provideMedicationRepository(
        medicationRepositoryImpl: MedicationRepositoryImpl
    ): MedicationRepository

    @Binds
    @Singleton
    abstract fun bindReminderRepository(
        reminderRepositoryImpl: ReminderRepositoryImpl
    ): ReminderRepository

    @Binds
    @Singleton
    abstract fun bindMedicationLocalDataSource(
        medicationLocalDataSourceImpl: MedicationLocalDataSourceImpl
    ): MedicationLocalDataSource

    @Binds
    @Singleton
    abstract fun bindMedicationRemoteDataSource(
        medicationFirebaseDataSourceImpl: MedicationFirebaseDataSourceImpl
    ): MedicationFirebaseDataSource
}