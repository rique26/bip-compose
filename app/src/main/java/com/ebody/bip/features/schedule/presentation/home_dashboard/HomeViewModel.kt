package com.ebody.bip.features.schedule.presentation.home_dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebody.bip.core.domain.intelligence.usecase.EvaluateBipIntelligenceUseCase
import com.ebody.bip.features.schedule.domain.usecase.DeleteReminderUseCase
import com.ebody.bip.features.schedule.domain.usecase.GetRemindersUseCase
import com.ebody.bip.features.schedule.domain.usecase.SyncRemindersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRemindersUseCase: GetRemindersUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase,
    private val syncRemindersUseCase: SyncRemindersUseCase,
    private val evaluateBipIntelligenceUseCase: EvaluateBipIntelligenceUseCase
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _aiState = MutableStateFlow(HomeDashboardUiState())

    init {
        evaluateIntelligence()
    }

    val uiState: StateFlow<HomeDashboardUiState> = getRemindersUseCase()
        .map { reminders ->
            HomeDashboardUiState(reminders = reminders, isLoading = false)
        }
        .onStart { emit(HomeDashboardUiState(isLoading = true)) }
        .catch { e ->
            emit(HomeDashboardUiState(error = e.message))
        }
        .combine(_aiState) { dbState, aiDecisions ->
            dbState.copy(
                riskLevel = aiDecisions.riskLevel,
                bipMessage = aiDecisions.bipMessage,
                mascotExpression = aiDecisions.mascotExpression,
                isLoading = dbState.isLoading,
                error = dbState.error
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeDashboardUiState(isLoading = true)
        )


    fun onEvent(event: HomeDashboardEvent) {
        when (event) {
            is HomeDashboardEvent.DeleteReminder -> {
                viewModelScope.launch {
                    deleteReminderUseCase(event.reminder)
                }
            }
            is HomeDashboardEvent.Refresh -> {
                viewModelScope.launch {
                    _isRefreshing.update { true }
                    syncRemindersUseCase()
                    _isRefreshing.update { false }
                }
            }
            is HomeDashboardEvent.Logout -> {
                viewModelScope.launch {
                }
            }

            is HomeDashboardEvent.MedicationTaken -> {
                evaluateIntelligence()
            }
        }
    }

    private fun evaluateIntelligence() {
        viewModelScope.launch {
            val analysis = evaluateBipIntelligenceUseCase()
            _aiState.update {
                it.copy(
                    riskLevel = analysis.riskLevel,
                    bipMessage = analysis.message,
                    mascotExpression = analysis.mascotExpression
                )
            }
        }
    }
}