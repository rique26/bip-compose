package com.ebody.bip.features.wellbeing.domain.repository

import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface MoodRepository {
    suspend fun saveMood(mood: MoodEntry)
    fun getMoodHistory(): Flow<List<MoodEntry>>
    suspend fun syncWithRemote()
    suspend fun getMoodsBetween(start: LocalDateTime, end: LocalDateTime): List<MoodEntry>

    /**
     * Retorna os últimos registros ordenados de forma decrescente (mais recente primeiro).
     */
    suspend fun getLastSymptoms(limit: Int): List<MoodEntry>
}