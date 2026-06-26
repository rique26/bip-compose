package com.ebody.bip.features.wellbeing.di

import com.ebody.bip.core.domain.intelligence.heuristics.BipHeuristic
import com.ebody.bip.features.wellbeing.domain.heuristics.SymptomPatternHeuristic
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class WellbeingIntelligenceModule {

    @Binds
    @IntoSet
    abstract fun bindSymptomPatternHeuristic(
        heuristic: SymptomPatternHeuristic
    ): BipHeuristic
}