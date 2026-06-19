package com.ebody.bip.features.schedule.presentation.home_dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebody.bip.features.schedule.domain.usecase.DeleteReminderUseCase
import com.ebody.bip.features.schedule.domain.usecase.GetRemindersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRemindersUseCase: GetRemindersUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase
) : ViewModel() {

    val uiState: StateFlow<HomeDashboardUiState> = getRemindersUseCase()
        .map { reminders ->
            HomeDashboardUiState(reminders = reminders, isLoading = false)
        }
        .onStart { emit(HomeDashboardUiState(isLoading = true)) }
        .catch { e ->
            emit(HomeDashboardUiState(error = e.message))
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
        }
    }
}