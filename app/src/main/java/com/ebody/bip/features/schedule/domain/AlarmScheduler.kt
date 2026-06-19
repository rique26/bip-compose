package com.ebody.bip.features.schedule.domain

import com.ebody.bip.features.schedule.domain.model.MedicationReminder

interface AlarmScheduler {
    fun schedule(reminder: MedicationReminder)
    fun cancel(reminder: MedicationReminder)
}