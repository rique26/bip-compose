package com.ebody.bip.features.schedule.presentation.medication_schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebody.bip.features.schedule.domain.model.Medication
import com.ebody.bip.features.schedule.domain.model.MedicationReminder
import com.ebody.bip.features.schedule.domain.usecase.GetMedicationByIdUseCase
import com.ebody.bip.features.schedule.domain.usecase.SaveReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MedicationScheduleViewModel @Inject constructor(
    private val getMedicationByIdUseCase: GetMedicationByIdUseCase,
    private val saveReminderUseCase: SaveReminderUseCase
) : ViewModel() {

    private val _medications = MutableStateFlow<List<Medication>>(emptyList())
    val medications: StateFlow<List<Medication>> = _medications.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    fun loadMedications(ids: List<Long>) {
        viewModelScope.launch {
            val loadedMedications = ids.mapNotNull { id ->
                getMedicationByIdUseCase(id)
            }
            _medications.value = loadedMedications
        }
    }

    fun saveReminders(
        scheduleTimes: List<Pair<Int, Int>>,
        dosage: String,
        onSuccess: () -> Unit
    ) {
        val meds = _medications.value
        if (meds.isEmpty()) return

        viewModelScope.launch {
            _isSaving.value = true
            try {
                for (med in meds) {
                    val reminders = scheduleTimes.map { (hour, minute) ->
                        MedicationReminder(
                            medication = med,
                            time = LocalTime.of(hour, minute),
                            dosage = dosage,
                            createdAt = System.currentTimeMillis(),
                            requestCode = Random.nextInt(10001)
                        )
                    }

                    reminders.forEach { reminder ->
                        saveReminderUseCase(reminder)
                    }
                }
                onSuccess()
            } finally {
                _isSaving.value = false
            }
        }
    }
}