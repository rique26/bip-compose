package com.ebody.bip.features.wellbeing.domain.usecase

import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import com.ebody.bip.features.wellbeing.domain.repository.MoodRepository
import com.ebody.bip.features.wellbeing.presentation.analytics.AnalyticsTimeFilter
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

class GetAnalyticsMetricsUseCase @Inject constructor(
    private val repository: MoodRepository
) {
    suspend operator fun invoke(filter: AnalyticsTimeFilter): AnalyticsResult {
        val now = LocalDateTime.now()
        val start = when (filter) {
            is AnalyticsTimeFilter.Days -> now.minusDays(filter.amount.toLong())
            is AnalyticsTimeFilter.Custom -> filter.startDate.atStartOfDay()
        }
        val end = if (filter is AnalyticsTimeFilter.Custom) filter.endDate.atTime(LocalTime.MAX) else now

        val entries = repository.getMoodsBetween(start, end)

        val translatedEntries = entries.map { entry ->
            when (entry.level) {
                1 -> "Mal"
                2 -> "Estranho"
                3 -> "Bem"
                4 -> "Ótimo"
                else -> "Desconhecido"
            }
        }

        // Cálculo de frequência e média
        val counts = translatedEntries.groupingBy { it }.eachCount()
        val total = entries.size
        val average = counts.maxByOrNull { it.value }?.key ?: "N/A"

        return AnalyticsResult(
            total = total,
            average = average,
            counts = counts,
            records = entries
        )
    }
}

data class AnalyticsResult(
    val total: Int,
    val average: String,
    val counts: Map<String, Int>,
    val records: List<MoodEntry>
)