package com.ebody.bip.core.di

import com.ebody.bip.core.domain.intelligence.heuristics.BipHeuristic
import com.ebody.bip.features.schedule.domain.heuristics.AdherenceCorrelationHeuristic
import com.ebody.bip.features.wellbeing.domain.heuristics.SymptomPatternHeuristic
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class IntelligenceEngineModule {

    @Binds
    @IntoSet
    abstract fun bindSymptomPatternHeuristic(
        heuristic: SymptomPatternHeuristic
    ): BipHeuristic

    @Binds
    @IntoSet
    abstract fun bindAdherenceCorrelationHeuristic(
        heuristic: AdherenceCorrelationHeuristic
    ): BipHeuristic
}