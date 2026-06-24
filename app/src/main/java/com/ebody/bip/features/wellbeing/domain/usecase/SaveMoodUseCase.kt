package com.ebody.bip.features.wellbeing.domain.usecase

import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import com.ebody.bip.features.wellbeing.domain.repository.MoodRepository
import javax.inject.Inject

class SaveMoodUseCase @Inject constructor(
    private val repository: MoodRepository
) {
    suspend operator fun invoke(mood: MoodEntry) {
        require(mood.level in 1..5) { "Nível de humor inválido." }
        repository.saveMood(mood)
    }
}