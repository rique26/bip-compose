package com.ebody.bip.features.wellbeing.domain.usecase

import com.ebody.bip.features.wellbeing.domain.repository.MoodRepository
import javax.inject.Inject

class SyncMoodsUseCase @Inject constructor(
    private val repository: MoodRepository
) {
    suspend operator fun invoke() {
        repository.syncWithRemote()
    }
}