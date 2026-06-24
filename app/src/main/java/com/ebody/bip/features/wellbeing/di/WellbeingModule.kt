package com.ebody.bip.features.wellbeing.di

import com.ebody.bip.features.wellbeing.data.datasource.local.MoodLocalDataSource
import com.ebody.bip.features.wellbeing.data.datasource.local.MoodLocalDataSourceImpl
import com.ebody.bip.features.wellbeing.data.datasource.remote.MoodRemoteDataSource
import com.ebody.bip.features.wellbeing.data.datasource.remote.MoodRemoteDataSourceImpl
import com.ebody.bip.features.wellbeing.data.repository.MoodRepositoryImpl
import com.ebody.bip.features.wellbeing.domain.repository.MoodRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WellbeingModule {

    @Binds
    @Singleton
    abstract fun bindMoodRepository(
        moodRepositoryImpl: MoodRepositoryImpl
    ): MoodRepository

    @Binds
    @Singleton
    abstract fun bindMoodLocalDataSource(
        impl: MoodLocalDataSourceImpl
    ): MoodLocalDataSource

    @Binds
    @Singleton
    abstract fun bindMoodRemoteDataSource(
        impl: MoodRemoteDataSourceImpl
    ): MoodRemoteDataSource
}