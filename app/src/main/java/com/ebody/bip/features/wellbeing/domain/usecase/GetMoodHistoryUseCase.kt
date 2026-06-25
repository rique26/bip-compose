package com.ebody.bip.features.wellbeing.domain.usecase

import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import com.ebody.bip.features.wellbeing.domain.repository.MoodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMoodHistoryUseCase @Inject constructor(
    private val repository: MoodRepository
) {
    operator fun invoke(): Flow<List<MoodEntry>> {
        return repository.getMoodHistory()
    }
}