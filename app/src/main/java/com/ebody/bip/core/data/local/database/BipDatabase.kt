package com.ebody.bip.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ebody.bip.features.schedule.data.local.MedicationDao
import com.ebody.bip.features.schedule.data.local.MedicationEntity
import com.ebody.bip.features.schedule.data.local.ReminderDao
import com.ebody.bip.features.schedule.data.local.ReminderEntity

@Database(entities = [ReminderEntity::class], version = 1)
abstract class BipDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
}

@Database(entities = [MedicationEntity::class], version = 1)
abstract class MedicationDatabase : RoomDatabase() {
    abstract fun medicationDao(): MedicationDao
}