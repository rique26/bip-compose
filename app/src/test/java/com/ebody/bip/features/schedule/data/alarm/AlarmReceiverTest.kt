package com.ebody.bip.features.schedule.data.alarm

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.ebody.bip.core.di.DatabaseModule
import com.ebody.bip.features.emergency.data.local.ContactDao
import com.ebody.bip.features.schedule.data.local.MedicationDao
import com.ebody.bip.features.schedule.data.local.ReminderDao
import com.ebody.bip.features.schedule.data.model.ReminderEntity
import com.ebody.bip.features.wellbeing.data.datasource.local.MoodDao
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.yield
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAlarmManager

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@UninstallModules(DatabaseModule::class)
@Config(sdk = [34], application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
class AlarmReceiverTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var context: Context
    private lateinit var alarmManager: AlarmManager
    private lateinit var shadowAlarmManager: ShadowAlarmManager
    private lateinit var receiver: AlarmReceiver

    private val testDispatcher = UnconfinedTestDispatcher()

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

    @Before
    fun setUp() {
        AlarmReceiver.dispatcherProvider = testDispatcher
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        shadowAlarmManager = shadowOf(alarmManager)

        try {
            val field = ShadowAlarmManager::class.java.getDeclaredField("canScheduleExactAlarms")
            field.isAccessible = true
            field.set(null, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        receiver = AlarmReceiver()
    }

    @After
    fun tearDown() {
        AlarmReceiver.dispatcherProvider = Dispatchers.IO
    }

    @Test
    fun onReceive_whenAlarmTriggerAction_startsServiceAndReschedulesAlarm() {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "${context.packageName}.ALARM_TRIGGER"
            putExtra("ALARM_LABEL", "Dipirona")
            putExtra("ALARM_DOSAGE", "500mg")
            putExtra("ALARM_TIME", System.currentTimeMillis() + 10000)
            putExtra("REQUEST_CODE", 101)
        }

        receiver.onReceive(context, intent)

        val startedIntent = shadowOf(RuntimeEnvironment.getApplication()).nextStartedService
        assertNotNull("O AlarmService deveria ter sido iniciado", startedIntent)
        assertEquals(AlarmService::class.java.name, startedIntent.component?.className)
        assertEquals(AlarmService.ACTION_NEW_ALARM, startedIntent.action)
        assertEquals("Dipirona", startedIntent.getStringExtra("ALARM_LABEL"))
        assertEquals("500mg", startedIntent.getStringExtra("ALARM_DOSAGE"))

        val nextScheduledAlarm = shadowAlarmManager.nextScheduledAlarm
        assertNotNull("O próximo alarme deveria ter sido agendado", nextScheduledAlarm)
        assertEquals(AlarmManager.RTC_WAKEUP, nextScheduledAlarm?.type)
    }

    @Test
    fun onReceive_whenBootCompletedAction_restoresRemindersFromDatabase() = runBlocking {
        val mockReminders = listOf(
            ReminderEntity(
                id = 1L,
                medicationId = 10L,
                time = System.currentTimeMillis() + 50000,
                dosage = "1 comprimido",
                requestCode = 201,
                createdAt = System.currentTimeMillis()
            )
        )
        coEvery { reminderDao.getAllActiveReminders() } returns flowOf(mockReminders)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = Intent.ACTION_BOOT_COMPLETED
        }

        receiver.onReceive(context, intent)
        yield()

        val scheduledAlarms = shadowAlarmManager.scheduledAlarms
        assertEquals("Deveria haver 1 alarme restaurado", 1, scheduledAlarms.size)
        assertEquals(AlarmManager.RTC_WAKEUP, scheduledAlarms[0].type)
    }

    @Test
    fun onReceive_whenActionIsNull_doesNothing() {
        // Arrange
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = null
        }

        // Act
        receiver.onReceive(context, intent)

        // Assert: Nenhum serviço deve ter sido iniciado e nenhum alarme agendado
        val startedIntent = shadowOf(RuntimeEnvironment.getApplication()).nextStartedService
        assertNull("Nenhum serviço deveria ser iniciado com ação nula", startedIntent)
        assertEquals(0, shadowAlarmManager.scheduledAlarms.size)
    }

    @Test
    fun onReceive_whenUnknownAction_doesNothing() {
        // Arrange
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "${context.packageName}.UNKNOWN_ACTION"
        }

        // Act
        receiver.onReceive(context, intent)

        // Assert
        val startedIntent = shadowOf(RuntimeEnvironment.getApplication()).nextStartedService
        assertNull("Nenhum serviço deveria ser iniciado para ações desconhecidas", startedIntent)
        assertEquals(0, shadowAlarmManager.scheduledAlarms.size)
    }

    @Test
    fun onReceive_whenBootCompletedButDatabaseIsEmpty_doesNotScheduleAlarms() = runBlocking {
        // Arrange
        coEvery { reminderDao.getAllActiveReminders() } returns flowOf(emptyList())

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = Intent.ACTION_BOOT_COMPLETED
        }

        // Act
        receiver.onReceive(context, intent)
        yield()

        // Assert: Nenhum alarme deve ser agendado se a lista estiver vazia
        assertEquals(0, shadowAlarmManager.scheduledAlarms.size)
    }

    @Test
    fun onReceive_whenBootCompletedAndDatabaseThrowsException_handlesGracefully() = runBlocking {
        // Arrange
        coEvery { reminderDao.getAllActiveReminders() } throws RuntimeException("Database error")

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = Intent.ACTION_BOOT_COMPLETED
        }

        // Act & Assert: Garante que não quebra a execução (não lança exception não tratada)
        receiver.onReceive(context, intent)
        yield()

        assertEquals(0, shadowAlarmManager.scheduledAlarms.size)
    }

    @Test
    fun scheduleExact_whenExactAlarmsPermissionDenied_doesNotScheduleAlarm() {
        // Arrange: Força a permissão de alarme exato como falsa via reflexão
        try {
            val field = ShadowAlarmManager::class.java.getDeclaredField("canScheduleExactAlarms")
            field.isAccessible = true
            field.set(null, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "${context.packageName}.ALARM_TRIGGER"
            putExtra("ALARM_TIME", System.currentTimeMillis() + 10000)
            putExtra("REQUEST_CODE", 102)
        }

        // Act
        receiver.onReceive(context, intent)

        // Assert: Como a permissão é falsa na API 31+, o alarme NÃO deve ser agendado
        assertEquals(0, shadowAlarmManager.scheduledAlarms.size)
    }
}