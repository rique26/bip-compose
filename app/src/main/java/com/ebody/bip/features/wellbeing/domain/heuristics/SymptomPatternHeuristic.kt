package com.ebody.bip.features.wellbeing.domain.heuristics

import com.ebody.bip.core.domain.intelligence.heuristics.BipHeuristic
import com.ebody.bip.core.domain.intelligence.model.HeuristicResult
import com.ebody.bip.features.wellbeing.domain.repository.MoodRepository
import javax.inject.Inject

class SymptomPatternHeuristic @Inject constructor(
    private val moodRepository: MoodRepository
) : BipHeuristic {

    override suspend fun evaluate(): HeuristicResult? {
        val recentSymptoms = moodRepository.getLastSymptoms(limit = 3)
        if (recentSymptoms.size < 3) return null

        val isConsecutiveBadDays = recentSymptoms.all { it.isMoodBad }
        val isConsecutiveStrangeDays = recentSymptoms.all { it.isMoodStrange }

        return when {
            isConsecutiveBadDays -> HeuristicResult(0.8f, "Humor consistentemente ruim nos últimos dias.")
            isConsecutiveStrangeDays -> HeuristicResult(0.4f, "Variações de humor detectadas.")
            else -> null
        }
    }
}