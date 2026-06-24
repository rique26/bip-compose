package com.ebody.bip.features.wellbeing.data.datasource.local

import com.ebody.bip.features.wellbeing.data.model.MoodEntity
import kotlinx.coroutines.flow.Flow

interface MoodLocalDataSource {
    suspend fun insertMood(mood: MoodEntity)
    fun getMoodHistory(): Flow<List<MoodEntity>>
}