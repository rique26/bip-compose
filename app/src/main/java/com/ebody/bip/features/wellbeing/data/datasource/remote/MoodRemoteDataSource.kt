package com.ebody.bip.features.wellbeing.data.datasource.remote

import com.ebody.bip.features.wellbeing.domain.model.MoodEntry

interface MoodRemoteDataSource {
    suspend fun saveMoodRemote(mood: MoodEntry)
    // todo remote fun streamMoods(): Flow<List<MoodEntry>>
}