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
import org.junit.Assert.assertTrue
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

    @Test
    fun onReceive_whenAlarmTriggeredMultipleTimesConsecutively_reschedulesWithoutDuplicating() {
        val baseTime = System.currentTimeMillis() + 10000
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "${context.packageName}.ALARM_TRIGGER"
            putExtra("ALARM_LABEL", "Paracetamol")
            putExtra("ALARM_DOSAGE", "3 comprimidos")
            putExtra("ALARM_TIME", baseTime)
            putExtra("REQUEST_CODE", 301)
        }

        // 1. Disparo do Dia 1 -> Deve agendar para o Dia 2
        receiver.onReceive(context, intent)

        var scheduledAlarms = shadowAlarmManager.scheduledAlarms
        assertEquals("Deveria haver exatamente 1 alarme agendado", 1, scheduledAlarms.size.toLong())
        val firstRescheduledTime = scheduledAlarms[0].triggerAtTime

        // Pega o intent atualizado que o próprio receptor modificou para o próximo ciclo
        val nextCycleIntent = shadowAlarmManager.nextScheduledAlarm?.operation?.let {
            // Extrai o intent do PendingIntent salvo para simular o disparo do dia seguinte
            shadowOf(it).savedIntent
        } ?: intent

        // 2. Disparo do Dia 2 -> Deve avançar para o Dia 3
        receiver.onReceive(context, nextCycleIntent)

        scheduledAlarms = shadowAlarmManager.scheduledAlarms
        assertEquals("Os alarmes não devem se multiplicar; deve continuar havendo apenas 1", 1, scheduledAlarms.size.toLong())

        val secondRescheduledTime = scheduledAlarms[0].triggerAtTime
        assertTrue("O tempo do alarme do Dia 3 deve ser posterior ao do Dia 2", secondRescheduledTime > firstRescheduledTime)
    }

    @Test
    fun `alarmTriggerSequence across multiple consecutive days reschedules cleanly without duplication`() {
        // Arrange: Define o número de dias que queremos simular em cadeia (ex: 7 dias seguidos)
        val totalDaysToSimulate = 7

        var currentIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "${context.packageName}.ALARM_TRIGGER"
            putExtra("ALARM_LABEL", "Paracetamol")
            putExtra("ALARM_DOSAGE", "3 comprimidos")
            putExtra("ALARM_TIME", System.currentTimeMillis() + 5000L)
            putExtra("REQUEST_CODE", 888)
        }

        var previousTriggerTime = 0L

        // Act & Assert por Indução: Loop simulando a passagem sequencial dos dias
        for (day in 1..totalDaysToSimulate) {
            // Dispara o alarme referente ao dia atual da simulação
            receiver.onReceive(context, currentIntent)

            // 1. Validação de Integridade: O AlarmManager NUNCA pode acumular mais de 1 alarme
            val scheduledAlarms = shadowAlarmManager.scheduledAlarms
            assertEquals(
                "No Dia $day, o sistema deve manter exatamente 1 alarme ativo (sem duplicação ou triplicação)",
                1,
                scheduledAlarms.size.toLong()
            )

            val currentAlarm = scheduledAlarms[0]
            val currentTriggerTime = currentAlarm.triggerAtTime

            // 2. Validação Temporal: O tempo do alarme atual deve ser sempre maior que o anterior
            if (day > 1) {
                assertTrue(
                    "O tempo do alarme do Dia $day deve ser estritamente posterior ao dia anterior",
                    currentTriggerTime > previousTriggerTime
                )
            }

            previousTriggerTime = currentTriggerTime

            // 3. Captura o Intent programado para o próximo dia para alimentar a próxima iteração do loop
            val pendingIntent = currentAlarm.operation
            val shadowPendingIntent = shadowOf(pendingIntent)
            currentIntent = shadowPendingIntent.savedIntent ?: break
        }
    }
}