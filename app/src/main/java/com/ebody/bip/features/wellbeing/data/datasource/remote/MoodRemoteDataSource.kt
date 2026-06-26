package com.ebody.bip.features.wellbeing.data.datasource.remote

import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import com.ebody.bip.features.wellbeing.domain.model.WellbeingError

interface MoodRemoteDataSource {
    suspend fun syncMood(userId: String, mood: MoodEntry): Result<Unit, WellbeingError>
    suspend fun fetchAllMoods(userId: String): Result<List<MoodEntry>, WellbeingError>
}