package com.ebody.bip.features.wellbeing.presentation.analytics

import java.time.LocalDate

sealed interface AnalyticsTimeFilter {
    data class Days(val amount: Int) : AnalyticsTimeFilter
    data class Custom(val startDate: LocalDate, val endDate: LocalDate) : AnalyticsTimeFilter
}