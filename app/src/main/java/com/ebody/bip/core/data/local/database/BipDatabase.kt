package com.ebody.bip.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ebody.bip.features.emergency.data.local.ContactDao
import com.ebody.bip.features.emergency.data.local.ContactEntity
import com.ebody.bip.features.schedule.data.local.MedicationDao
import com.ebody.bip.features.schedule.data.model.MedicationEntity
import com.ebody.bip.features.schedule.data.local.ReminderDao
import com.ebody.bip.features.schedule.data.model.ReminderEntity
import com.ebody.bip.features.wellbeing.data.datasource.local.MoodDao
import com.ebody.bip.features.wellbeing.data.model.MoodEntity

@Database(
    entities = [
        ReminderEntity::class,
        ContactEntity::class,
        MoodEntity::class,
    ],
    version = 2, exportSchema = false
)
abstract class BipDatabase : RoomDatabase() {
    abstract fun moodDao(): MoodDao
    abstract fun reminderDao(): ReminderDao
    abstract fun contactDao(): ContactDao
}

@Database(entities = [MedicationEntity::class], version = 1)
abstract class MedicationDatabase : RoomDatabase() {
    abstract fun medicationDao(): MedicationDao
}