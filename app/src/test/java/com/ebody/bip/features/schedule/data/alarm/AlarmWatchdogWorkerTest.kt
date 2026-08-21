package com.ebody.bip.features.schedule.data.alarm

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.ebody.bip.core.di.DatabaseModule
import com.ebody.bip.features.emergency.data.local.ContactDao
import com.ebody.bip.features.schedule.data.local.MedicationDao
import com.ebody.bip.features.schedule.data.local.ReminderDao
import com.ebody.bip.features.schedule.data.model.ReminderEntity
import com.ebody.bip.features.schedule.di.AlarmModule
import com.ebody.bip.features.schedule.domain.AlarmScheduler
import com.ebody.bip.features.schedule.domain.model.MedicationReminder
import com.ebody.bip.features.wellbeing.data.datasource.local.MoodDao
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(DatabaseModule::class, AlarmModule::class)
@Config(sdk = [34], application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
class AlarmWatchdogWorkerTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var context: Context

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    @BindValue
    @JvmField
    val reminderDao: ReminderDao = mockk(relaxed = true)

    @BindValue
    @JvmField
    val medicationDao: MedicationDao = mockk(relaxed = true)

    @BindValue
    @JvmField
    val moodDao: MoodDao = mockk(relaxed = true)

    @BindValue
    @JvmField
    val contactDao: ContactDao = mockk(relaxed = true)

    @BindValue
    @JvmField
    val alarmScheduler: AlarmScheduler = mockk(relaxed = true)

    @Before
    fun setUp() {
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun doWork_whenActiveRemindersExist_shouldScanAndRevalidateSuccessfully() = runBlocking {
        // Arrange
        val mockReminders = listOf(
            ReminderEntity(
                id = 1L,
                medicationId = 10L,
                time = System.currentTimeMillis() + 60000,
                dosage = "1 comprimido",
                requestCode = 301,
                createdAt = System.currentTimeMillis()
            )
        )
        coEvery { reminderDao.getAllActiveReminders() } returns flowOf(mockReminders)

        // Passa o workerFactory para que o Hilt consiga instanciar o Worker com as dependências
        val worker = TestListenableWorkerBuilder<AlarmWatchdogWorker>(context)
            .setWorkerFactory(workerFactory)
            .build()

        // Act
        val result = worker.doWork()

        // Assert
        assertEquals(ListenableWorker.Result.success(), result)
        coVerify(exactly = 1) { reminderDao.getAllActiveReminders() }
    }

    @Test
    fun doWork_whenDatabaseIsEmpty_shouldReturnSuccessGracefully() = runBlocking {
        // Arrange
        coEvery { reminderDao.getAllActiveReminders() } returns flowOf(emptyList())

        val worker = TestListenableWorkerBuilder<AlarmWatchdogWorker>(context)
            .setWorkerFactory(workerFactory)
            .build()

        // Act
        val result = worker.doWork()

        // Assert
        assertEquals(ListenableWorker.Result.success(), result)
        coVerify(exactly = 1) { reminderDao.getAllActiveReminders() }
    }

    @Test
    fun doWork_whenExceptionOccurs_shouldHandleAndReturnRetryOrFailure() = runBlocking {
        // Arrange
        coEvery { reminderDao.getAllActiveReminders() } throws RuntimeException("Database error")

        val worker = TestListenableWorkerBuilder<AlarmWatchdogWorker>(context)
            .setWorkerFactory(workerFactory)
            .build()

        // Act
        val result = worker.doWork()

        // Assert
        assertEquals(ListenableWorker.Result.retry(), result)
    }

    @Test
    fun doWork_whenMedicationNotFoundForReminder_shouldSkipAndReturnSuccess() = runBlocking {
        // Arrange
        val mockReminders = listOf(
            ReminderEntity(
                id = 1L,
                medicationId = 99L,
                time = System.currentTimeMillis() + 60000,
                dosage = "1 comprimido",
                requestCode = 301,
                createdAt = System.currentTimeMillis()
            )
        )
        coEvery { reminderDao.getAllActiveReminders() } returns flowOf(mockReminders)
        coEvery { medicationDao.getMedicationById(99L) } returns null

        val worker = TestListenableWorkerBuilder<AlarmWatchdogWorker>(context)
            .setWorkerFactory(workerFactory)
            .build()

        // Act
        val result = worker.doWork()

        // Assert
        assertEquals(ListenableWorker.Result.success(), result)

        // Ajustado com o tipo explícito para o compilador resolver a inferência
        coVerify(exactly = 0) { alarmScheduler.schedule(any<MedicationReminder>()) }
    }
}