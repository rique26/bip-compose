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
import com.ebody.bip.features.schedule.data.model.MedicationEntity
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
    fun `doWork when active reminders exist should schedule alarms successfully`() = runBlocking {
        // Arrange
        val medicationId = 10L
        val mockReminder = ReminderEntity(
            id = 1L,
            medicationId = medicationId,
            time = System.currentTimeMillis() + 60000,
            dosage = "3 comprimidos",
            createdAt = System.currentTimeMillis(),
            requestCode = 301
        )

        val mockMedicationEntity = MedicationEntity(
            id = medicationId,
            name = "Paracetamol"
        )

        coEvery { reminderDao.getAllActiveReminders() } returns flowOf(listOf(mockReminder))
        coEvery { medicationDao.getMedicationById(medicationId) } returns mockMedicationEntity

        val worker = TestListenableWorkerBuilder<AlarmWatchdogWorker>(context)
            .setWorkerFactory(workerFactory)
            .build()

        // Act
        val result = worker.doWork()

        // Assert
        assertEquals(ListenableWorker.Result.success(), result)
        coVerify(exactly = 1) { reminderDao.getAllActiveReminders() }
        coVerify(exactly = 1) { medicationDao.getMedicationById(medicationId) }
        coVerify(exactly = 1) { alarmScheduler.schedule(any<MedicationReminder>()) }
    }

    @Test
    fun `doWork when multiple active reminders exist should schedule all alarms successfully`() = runBlocking {
        // Arrange (ordem corrigida: id, medicationId, time, dosage, createdAt, requestCode)
        val reminders = listOf(
            ReminderEntity(1L, 10L, System.currentTimeMillis() + 60000, "1 comp", System.currentTimeMillis(), 301),
            ReminderEntity(2L, 20L, System.currentTimeMillis() + 120000, "2 ml", System.currentTimeMillis(), 302)
        )
        val med1 = MedicationEntity(10L, "Dipirona")
        val med2 = MedicationEntity(20L, "Ibuprofeno")

        coEvery { reminderDao.getAllActiveReminders() } returns flowOf(reminders)
        coEvery { medicationDao.getMedicationById(10L) } returns med1
        coEvery { medicationDao.getMedicationById(20L) } returns med2

        val worker = TestListenableWorkerBuilder<AlarmWatchdogWorker>(context)
            .setWorkerFactory(workerFactory)
            .build()

        // Act
        val result = worker.doWork()

        // Assert
        assertEquals(ListenableWorker.Result.success(), result)
        coVerify(exactly = 1) { alarmScheduler.schedule(match { it.medication.name == "Dipirona" }) }
        coVerify(exactly = 1) { alarmScheduler.schedule(match { it.medication.name == "Ibuprofeno" }) }
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
        coVerify(exactly = 0) { alarmScheduler.schedule(any()) }
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
        coVerify(exactly = 0) { alarmScheduler.schedule(any()) }
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
                createdAt = System.currentTimeMillis(),
                requestCode = 301
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
        coVerify(exactly = 0) { alarmScheduler.schedule(any<MedicationReminder>()) }
    }

    @Test
    fun `doWork when mixed valid and orphan reminders exist should skip orphan and schedule valid`() = runBlocking {
        // Arrange (ordem corrigida: id, medicationId, time, dosage, createdAt, requestCode)
        val validReminder = ReminderEntity(1L, 10L, System.currentTimeMillis() + 60000, "1 comp", System.currentTimeMillis(), 301)
        val orphanReminder = ReminderEntity(2L, 99L, System.currentTimeMillis() + 120000, "1 comp", System.currentTimeMillis(), 302)

        coEvery { reminderDao.getAllActiveReminders() } returns flowOf(listOf(validReminder, orphanReminder))
        coEvery { medicationDao.getMedicationById(10L) } returns MedicationEntity(10L, "Vitamina C")
        coEvery { medicationDao.getMedicationById(99L) } returns null

        val worker = TestListenableWorkerBuilder<AlarmWatchdogWorker>(context)
            .setWorkerFactory(workerFactory)
            .build()

        // Act
        val result = worker.doWork()

        // Assert
        assertEquals(ListenableWorker.Result.success(), result)
        coVerify(exactly = 1) { alarmScheduler.schedule(match { it.medication.name == "Vitamina C" }) }
    }

    @Test
    fun `doWork when reminder has multi pill dosage should schedule alarm exactly once`() = runBlocking {
        // Arrange: O usuário vai tomar 3 comprimidos, mas o alarme deve tocar apenas 1 vez no horário
        val medicationId = 10L
        val mockReminder = ReminderEntity(
            id = 1L,
            medicationId = medicationId,
            time = System.currentTimeMillis() + 60000,
            dosage = "3 comprimidos",
            createdAt = System.currentTimeMillis(),
            requestCode = 301
        )

        val mockMedication = MedicationEntity(
            id = medicationId,
            name = "Antibiótico"
        )

        coEvery { reminderDao.getAllActiveReminders() } returns flowOf(listOf(mockReminder))
        coEvery { medicationDao.getMedicationById(medicationId) } returns mockMedication

        val worker = TestListenableWorkerBuilder<AlarmWatchdogWorker>(context)
            .setWorkerFactory(workerFactory)
            .build()

        // Act
        val result = worker.doWork()

        // Assert
        assertEquals(ListenableWorker.Result.success(), result)

        // A garantia de ouro: O agendador NÃO PODE multiplicar os alarmes por causa da dosagem.
        // Deve ser chamado exatamente 1 única vez.
        coVerify(exactly = 1) { alarmScheduler.schedule(any<MedicationReminder>()) }
    }
}