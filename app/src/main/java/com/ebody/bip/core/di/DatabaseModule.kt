package com.ebody.bip.core.di

import android.content.Context
import androidx.room.Room
import com.ebody.bip.core.data.local.database.BipDatabase
import com.ebody.bip.features.schedule.data.local.MedicationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideBipDatabase(
        @ApplicationContext context: Context,
    ): BipDatabase {
        return Room.databaseBuilder(
            context,
            BipDatabase::class.java,
            "bip_database"
        )
            .createFromAsset("medications.db")
            .fallbackToDestructiveMigration()
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
    fun provideMedicationDao(database: BipDatabase): MedicationDao {
        return database.medicationDao()
    }
}