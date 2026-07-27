package com.ebody.bip.core.di

import com.ebody.bip.core.data.intelligence.repository.ClinicalIntelligenceRepositoryImpl
import com.ebody.bip.core.data.intelligence.repository.LlmInferenceEngineImpl
import com.ebody.bip.core.domain.intelligence.repository.LlmInferenceEngine
import com.ebody.bip.features.intelligence.domain.repository.ClinicalIntelligenceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class IntelligenceModule {

    @Binds
    @Singleton
    abstract fun bindClinicalIntelligenceRepository(
        impl: ClinicalIntelligenceRepositoryImpl
    ): ClinicalIntelligenceRepository

    @Binds
    @Singleton
    abstract fun bindLlmInferenceEngine(
        impl: LlmInferenceEngineImpl
    ): LlmInferenceEngine
}