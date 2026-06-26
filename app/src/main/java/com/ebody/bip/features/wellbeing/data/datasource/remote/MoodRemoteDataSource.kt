package com.ebody.bip.features.wellbeing.data.datasource.remote

import com.ebody.bip.features.wellbeing.domain.model.MoodEntry

interface MoodRemoteDataSource {
    suspend fun saveMoodRemote(userId: String, mood: MoodEntry)
    suspend fun fetchAllMoods(userId: String): List<MoodEntry>
}