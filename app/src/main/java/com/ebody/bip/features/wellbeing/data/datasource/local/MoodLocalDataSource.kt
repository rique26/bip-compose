package com.ebody.bip.features.wellbeing.data.datasource.local

import com.ebody.bip.features.wellbeing.data.model.MoodEntity
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import kotlinx.coroutines.flow.Flow

interface MoodLocalDataSource {
    suspend fun insertMood(mood: MoodEntity): Long
    suspend fun insertMoods(moods: List<MoodEntity>)
    fun getMoodHistory(): Flow<List<MoodEntity>>
    suspend fun getMoodsBetween(start: String, end: String): List<MoodEntity>
}