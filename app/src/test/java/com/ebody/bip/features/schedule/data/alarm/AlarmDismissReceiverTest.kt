package com.ebody.bip.features.schedule.data.alarm

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AlarmDismissReceiverTest {

    @Test
    fun onReceive_whenTriggered_shouldStartAlarmServiceWithDismissAction() {
        // Arrange
        val context = ApplicationProvider.getApplicationContext<Context>()
        val receiver = AlarmDismissReceiver()
        val intent = Intent("com.ebody.bip.ACTION_DISMISS_NOTIFICATION")

        // Act
        receiver.onReceive(context, intent)

        // Assert
        val shadowApp = shadowOf(context as Application)
        val startedIntent = shadowApp.nextStartedService

        assertNotNull("O AlarmService deveria ter sido iniciado", startedIntent)
        assertEquals(AlarmService::class.java.name, startedIntent?.component?.className)
        assertEquals(AlarmService.ACTION_DISMISS, startedIntent?.action)
    }

    @Test
    fun onReceive_whenMultipleBroadcastsTriggered_shouldStartServiceForEveryEvent() {
        // Arrange
        val context = ApplicationProvider.getApplicationContext<Context>()
        val receiver = AlarmDismissReceiver()
        val intent = Intent()

        // Act: Simula cliques múltiplos e rápidos do usuário na notificação de alarme
        receiver.onReceive(context, intent)
        receiver.onReceive(context, intent)

        // Assert
        val shadowApp = shadowOf(context as Application)

        // Valida o primeiro evento disparado
        val firstIntent = shadowApp.nextStartedService
        assertNotNull(firstIntent)
        assertEquals(AlarmService.ACTION_DISMISS, firstIntent?.action)

        // Valida o segundo evento disparado em sequência
        val secondIntent = shadowApp.nextStartedService
        assertNotNull(secondIntent)
        assertEquals(AlarmService.ACTION_DISMISS, secondIntent?.action)
    }

    @Test
    fun onReceive_whenIncomingIntentHasCustomAction_shouldForceCorrectDismissAction() {
        // Arrange
        val context = ApplicationProvider.getApplicationContext<Context>()
        val receiver = AlarmDismissReceiver()

        // Simula um intent vindo de uma origem com action e payloads customizados
        val intent = Intent("SOME_RANDOM_ACTION").apply {
            putExtra("EXTRA_REMINDER_ID", 999L)
        }

        // Act
        receiver.onReceive(context, intent)

        // Assert
        val shadowApp = shadowOf(context as Application)
        val startedIntent = shadowApp.nextStartedService

        assertNotNull(startedIntent)
        assertEquals(AlarmService::class.java.name, startedIntent?.component?.className)

        // Garante que o receiver blinda o comportamento e sobrescreve/assegura a action correta para o serviço
        assertEquals(AlarmService.ACTION_DISMISS, startedIntent?.action)
    }
}