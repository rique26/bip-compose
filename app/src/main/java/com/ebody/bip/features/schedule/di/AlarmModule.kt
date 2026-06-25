package com.ebody.bip.features.schedule.di

import com.ebody.bip.features.schedule.data.alarm.AlarmSchedulerImpl
import com.ebody.bip.features.schedule.domain.AlarmScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmModule {

    @Binds
    @Singleton
    abstract fun bindAlarmScheduler(impl: AlarmSchedulerImpl): AlarmScheduler
}