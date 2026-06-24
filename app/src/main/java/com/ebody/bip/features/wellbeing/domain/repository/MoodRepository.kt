package com.ebody.bip.features.wellbeing.domain.repository

import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import kotlinx.coroutines.flow.Flow

interface MoodRepository {
    suspend fun saveMood(mood: MoodEntry)
    fun getMoodHistory(): Flow<List<MoodEntry>>
    // todo remote suspend fun syncRemoteToLocal()
}