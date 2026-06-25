package com.ebody.bip.core.di

import android.content.Context
import androidx.room.Room
import com.ebody.bip.core.database.BipDatabase
import com.ebody.bip.core.database.MedicationDatabase
import com.ebody.bip.features.emergency.data.local.ContactDao
import com.ebody.bip.features.schedule.data.local.MedicationDao
import com.ebody.bip.features.schedule.data.local.ReminderDao
import com.ebody.bip.features.wellbeing.data.datasource.local.MoodDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    @Named("BipDb")
    fun provideBipDatabase(@ApplicationContext context: Context, ): BipDatabase {
        return Room.databaseBuilder(context, BipDatabase::class.java, "bip.db")
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    @Singleton
    @Named("MedicationDb")
    fun provideMedicationDatabase(@ApplicationContext context: Context): MedicationDatabase {
        return Room.databaseBuilder(context, MedicationDatabase::class.java, "medications.db")
            .createFromAsset("medications.db")
            .build()
    }

//    @Provides
//    @Singleton
//    fun provideUserDao(database: BipDatabase): UserDao {
//        return database.userDao()
//    }
//
    @Provides
    @Singleton
    fun provideMedicationDao(@Named("MedicationDb") database: MedicationDatabase): MedicationDao {
        return database.medicationDao()
    }

    @Provides
    @Singleton
    fun provideReminderDao(@Named("BipDb") database: BipDatabase): ReminderDao {
        return database.reminderDao()
    }

    @Provides
    @Singleton
    fun provideContactDao(@Named("BipDb") database: BipDatabase): ContactDao {
        return database.contactDao()
    }

    @Provides
    @Singleton
    fun provideMoodDao(@Named("BipDb") database: BipDatabase): MoodDao {
        return database.moodDao()
    }
}