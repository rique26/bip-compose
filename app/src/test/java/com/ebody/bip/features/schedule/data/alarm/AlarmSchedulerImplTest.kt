package com.ebody.bip.features.schedule.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.ebody.bip.features.schedule.domain.model.Medication
import com.ebody.bip.features.schedule.domain.model.MedicationReminder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAlarmManager
import java.time.LocalTime

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AlarmSchedulerImplTest {

    private lateinit var context: Context
    private lateinit var alarmManager: AlarmManager
    private lateinit var shadowAlarmManager: ShadowAlarmManager
    private lateinit var scheduler: AlarmSchedulerImpl

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        shadowAlarmManager = shadowOf(alarmManager)
        setExactAlarmPermission(true)
        scheduler = AlarmSchedulerImpl(context)
    }

    private fun setExactAlarmPermission(allowed: Boolean) {
        try {
            val field = ShadowAlarmManager::class.java.getDeclaredField("canScheduleExactAlarms")
            field.isAccessible = true
            field.set(null, allowed)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun schedule_whenValidReminder_shouldConfigureAlarmWithCorrectExtras() {
        // Arrange
        val reminder = MedicationReminder(
            medication = Medication(id = 1L, name = "Dipirona"),
            time = LocalTime.of(14, 30),
            dosage = "500mg",
            createdAt = System.currentTimeMillis(),
            requestCode = 1001
        )

        // Act
        scheduler.schedule(reminder)

        // Assert
        val nextScheduledAlarm = shadowAlarmManager.nextScheduledAlarm
        assertNotNull("Alarme deveria ter sido agendado", nextScheduledAlarm)

        // Verifica se o PendingIntent tem os extras corretos
        val shadowPendingIntent = shadowOf(nextScheduledAlarm?.operation)
        val intent = shadowPendingIntent.savedIntent

        assertEquals("Dipirona", intent.getStringExtra("ALARM_LABEL"))
        assertEquals("500mg", intent.getStringExtra("ALARM_DOSAGE"))
        assertEquals(1001, intent.getIntExtra("REQUEST_CODE", 0))
    }

    @Test
    fun schedule_whenPermissionDenied_doesNotScheduleAlarm() {
        // Arrange
        setExactAlarmPermission(false)
        val reminder = MedicationReminder(
            medication = Medication(id = 1L, name = "Dipirona"),
            time = LocalTime.of(14, 30),
            dosage = "500mg",
            createdAt = System.currentTimeMillis(),
            requestCode = 1001
        )

        // Act
        scheduler.schedule(reminder)

        // Assert
        assertEquals("Nenhum alarme deveria ser agendado se a permissão for negada", 0, shadowAlarmManager.scheduledAlarms.size)
    }

    @Test
    fun cancel_whenAlarmExists_shouldRemoveFromManager() {
        // Arrange
        val reminder = MedicationReminder(
            medication = Medication(id = 1L, name = "Dipirona"),
            time = LocalTime.of(14, 30),
            dosage = "500mg",
            createdAt = System.currentTimeMillis(),
            requestCode = 1001
        )
        scheduler.schedule(reminder)
        assertEquals(1, shadowAlarmManager.scheduledAlarms.size)

        // Act
        scheduler.cancel(reminder)

        // Assert
        assertEquals("Alarme deveria ter sido removido", 0, shadowAlarmManager.scheduledAlarms.size)
    }

    @Test
    fun cancel_whenAlarmDoesNotExist_shouldNotThrowException() {
        // Arrange
        val reminder = MedicationReminder(
            medication = Medication(id = 1L, name = "Dipirona"),
            time = LocalTime.of(14, 30),
            dosage = "500mg",
            createdAt = System.currentTimeMillis(),
            requestCode = 9999 // Code inexistente
        )

        // Act & Assert
        try {
            scheduler.cancel(reminder)
        } catch (e: Exception) {
            org.junit.Assert.fail("Cancelamento de alarme inexistente não deveria lançar exceção: ${e.message}")
        }
    }

    @Test
    fun schedule_whenDosageHasMultiplePills_shouldScheduleOnlyOneAlarmInSystem() {
        // Arrange: Dosagem complexa indicando múltiplos comprimidos
        val reminder = MedicationReminder(
            medication = Medication(id = 1L, name = "Paracetamol"),
            time = LocalTime.of(8, 0),
            dosage = "3 comprimidos",
            createdAt = System.currentTimeMillis(),
            requestCode = 1002
        )

        // Act
        scheduler.schedule(reminder)

        // Assert: Garante que o AlarmManager do Android recebeu EXATAMENTE 1 alarme físico,
        // independentemente da quantidade descrita na string de dosagem.
        assertEquals("O AlarmManager deve registrar apenas 1 alarme físico no sistema", 1, shadowAlarmManager.scheduledAlarms.size)

        val nextAlarm = shadowAlarmManager.nextScheduledAlarm
        assertNotNull(nextAlarm)

        // Valida que o extra da dosagem foi repassado corretamente sem gerar duplicidade
        val intent = shadowOf(nextAlarm?.operation).savedIntent
        assertEquals("3 comprimidos", intent.getStringExtra("ALARM_DOSAGE"))
    }
}