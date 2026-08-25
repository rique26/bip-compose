package com.ebody.bip.features.schedule.data.alarm

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowMediaPlayer
import org.robolectric.shadows.util.DataSource

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AlarmServiceTest {

    private lateinit var context: Context
    private lateinit var notificationManager: NotificationManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Registra a URI de áudio no ShadowMediaPlayer do Robolectric para evitar falhas no setDataSource
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        if (alarmUri != null) {
            ShadowMediaPlayer.addMediaInfo(
                DataSource.toDataSource(context, alarmUri),
                ShadowMediaPlayer.MediaInfo(1000, 0)
            )
        }
    }

    @After
    fun tearDown() {
        notificationManager.cancelAll()
        ShadowMediaPlayer.resetStaticState()
    }

    @Test
    fun onStartCommand_withNewAlarm_shouldStartForegroundAndCreateChannel() {
        // Arrange
        val serviceController = Robolectric.buildService(AlarmService::class.java)
        val intent = Intent(context, AlarmService::class.java).apply {
            action = AlarmService.ACTION_NEW_ALARM
            putExtra("ALARM_LABEL", "Dipirona")
            putExtra("ALARM_DOSAGE", "500mg")
        }

        // Act
        val service = serviceController.create().get()
        service.onStartCommand(intent, 0, 1)

        // Assert
        val shadowService = shadowOf(service)
        assertTrue("O serviço deveria entrar em modo foreground", shadowService.getLastForegroundNotificationId() != 0)
        assertEquals(AlarmService.NOTIFICATION_ID, shadowService.getLastForegroundNotificationId())

        val channel = notificationManager.getNotificationChannel(AlarmService.CHANNEL_ID)
        assertNotNull("O canal de notificação deve ser criado", channel)
        assertEquals("Alarmes de Medicamento", channel?.name)

        serviceController.destroy()
    }

    @Test
    fun onStartCommand_withLegacyIntent_shouldStartForegroundSuccessfully() {
        // Arrange
        val serviceController = Robolectric.buildService(AlarmService::class.java)
        val intent = Intent(context, AlarmService::class.java).apply {
            putExtra("ALARM_LABEL", "Paracetamol")
            putExtra("ALARM_DOSAGE", "750mg")
        }

        // Act
        val service = serviceController.create().get()
        service.onStartCommand(intent, 0, 1)

        // Assert
        val shadowService = shadowOf(service)
        assertTrue("O serviço via fallback/legacy deveria entrar em foreground", shadowService.getLastForegroundNotificationId() != 0)

        serviceController.destroy()
    }

    @Test
    fun onStartCommand_withMultipleAlarms_shouldEnqueueAndNotify() {
        // Arrange
        val serviceController = Robolectric.buildService(AlarmService::class.java)
        val service = serviceController.create().get()

        val firstIntent = Intent(context, AlarmService::class.java).apply {
            action = AlarmService.ACTION_NEW_ALARM
            putExtra("ALARM_LABEL", "Vitamina C")
            putExtra("ALARM_DOSAGE", "1 comprimido")
        }

        val secondIntent = Intent(context, AlarmService::class.java).apply {
            action = AlarmService.ACTION_NEW_ALARM
            putExtra("ALARM_LABEL", "Omeprazol")
            putExtra("ALARM_DOSAGE", "20mg")
        }

        // Act
        service.onStartCommand(firstIntent, 0, 1)
        service.onStartCommand(secondIntent, 0, 2)

        // Assert
        val shadowService = shadowOf(service)
        assertTrue(shadowService.getLastForegroundNotificationId() != 0)

        val notification = notificationManager.activeNotifications.find { it.id == AlarmService.NOTIFICATION_ID }
        assertNotNull(notification)
        val contentText = notification?.notification?.extras?.getCharSequence("android.text")?.toString()
        assertTrue(contentText?.contains("Omeprazol") == true)

        serviceController.destroy()
    }

    @Test
    fun onStartCommand_withDismissAction_whenNoPendingAlarms_shouldStopService() {
        // Arrange
        val serviceController = Robolectric.buildService(AlarmService::class.java)
        val service = serviceController.create().get()

        val startIntent = Intent(context, AlarmService::class.java).apply {
            action = AlarmService.ACTION_NEW_ALARM
            putExtra("ALARM_LABEL", "Ibuprofeno")
            putExtra("ALARM_DOSAGE", "400mg")
        }
        service.onStartCommand(startIntent, 0, 1)

        val dismissIntent = Intent(context, AlarmService::class.java).apply {
            action = AlarmService.ACTION_DISMISS
        }

        // Act
        service.onStartCommand(dismissIntent, 0, 2)

        // Assert
        assertTrue("O serviço deve invocar stopSelf() quando a fila estiver vazia", shadowOf(service).isStoppedBySelf)

        serviceController.destroy()
    }

    @Test
    fun onStartCommand_withDismissAction_whenPendingAlarmsExist_shouldPlayNextAlarm() {
        // Arrange
        val serviceController = Robolectric.buildService(AlarmService::class.java)
        val service = serviceController.create().get()

        service.onStartCommand(Intent(context, AlarmService::class.java).apply {
            action = AlarmService.ACTION_NEW_ALARM
            putExtra("ALARM_LABEL", "Alarme A")
            putExtra("ALARM_DOSAGE", "10mg")
        }, 0, 1)

        service.onStartCommand(Intent(context, AlarmService::class.java).apply {
            action = AlarmService.ACTION_NEW_ALARM
            putExtra("ALARM_LABEL", "Alarme B")
            putExtra("ALARM_DOSAGE", "20mg")
        }, 0, 2)

        // Act
        val dismissIntent = Intent(context, AlarmService::class.java).apply {
            action = AlarmService.ACTION_DISMISS
        }
        service.onStartCommand(dismissIntent, 0, 3)

        // Assert
        assertFalse("O serviço NÃO deve parar (stopSelf), pois há o Alarme B na fila", shadowOf(service).isStoppedBySelf)
        val notification = notificationManager.activeNotifications.find { it.id == AlarmService.NOTIFICATION_ID }
        assertNotNull("Uma nova notificação com o próximo alarme deve estar ativa", notification)

        serviceController.destroy()
    }

    @Test
    fun onStartCommand_withNullIntent_shouldHandleGracefullyWithoutCrashing() {
        // Arrange
        val serviceController = Robolectric.buildService(AlarmService::class.java)
        val service = serviceController.create().get()

        // Act & Assert
        val result = service.onStartCommand(null, 0, 1)
        assertEquals(Service.START_NOT_STICKY, result)

        serviceController.destroy()
    }

    @Test
    fun onDestroy_shouldReleaseResourcesAndCleanState() {
        // Arrange
        val serviceController = Robolectric.buildService(AlarmService::class.java)
        val service = serviceController.create().get()

        service.onStartCommand(Intent(context, AlarmService::class.java).apply {
            action = AlarmService.ACTION_NEW_ALARM
            putExtra("ALARM_LABEL", "Teste Limpeza")
            putExtra("ALARM_DOSAGE", "1cp")
        }, 0, 1)

        // Act
        serviceController.destroy()

        // Assert
        val notification = notificationManager.activeNotifications.find { it.id == AlarmService.NOTIFICATION_ID }
        assertNull("O ciclo de vida do destroy deve remover a notificação de foreground", notification)
    }
}