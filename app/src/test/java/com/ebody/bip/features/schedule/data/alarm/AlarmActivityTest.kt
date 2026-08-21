package com.ebody.bip.features.schedule.data.alarm

import android.content.Context
import android.content.Intent
import android.view.WindowManager
import androidx.compose.ui.test.junit4.v2.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AlarmActivityTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    @Test
    fun onCreate_shouldConfigureWindowFlagsForLockScreen() {
        // Arrange
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, AlarmActivity::class.java)

        // Act & Assert
        ActivityScenario.launch<AlarmActivity>(intent).use { scenario: ActivityScenario<AlarmActivity> ->
            scenario.onActivity { activity ->
                val flags = activity.window.attributes.flags
                assertTrue(
                    "A flag FLAG_KEEP_SCREEN_ON deve estar ativa para manter a tela acesa",
                    (flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) != 0
                )
            }
        }
    }

    @Test
    fun onCreate_withCustomIntentExtras_shouldDisplayCustomLabelAndDosage() {
        // Arrange
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, AlarmActivity::class.java).apply {
            putExtra("ALARM_LABEL", "Dipirona Sódica")
            putExtra("ALARM_DOSAGE", "500mg")
        }

        // Act & Assert
        ActivityScenario.launch<AlarmActivity>(intent).use { _: ActivityScenario<AlarmActivity> ->
            composeTestRule.onNodeWithText("Dipirona Sódica").assertExists()
            composeTestRule.onNodeWithText("500mg").assertExists()
            composeTestRule.onNodeWithText("✓  Dispensar").assertExists()
        }
    }

    @Test
    fun onCreate_withMissingIntentExtras_shouldDisplayDefaultFallbackValues() {
        // Arrange (Cenário negativo/ausência de dados)
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, AlarmActivity::class.java)

        // Act & Assert
        ActivityScenario.launch<AlarmActivity>(intent).use { _: ActivityScenario<AlarmActivity> ->
            composeTestRule.onNodeWithText("Medicamento").assertExists()
            composeTestRule.onNodeWithText("✓  Dispensar").assertExists()
        }
    }

    @Test
    fun onBackPressed_shouldBeInterceptedAndNotFinishActivity() {
        // Arrange (Cenário de segurança/bloqueio de navegação)
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, AlarmActivity::class.java)

        ActivityScenario.launch<AlarmActivity>(intent).use { scenario: ActivityScenario<AlarmActivity> ->
            // Act
            scenario.onActivity { activity ->
                activity.onBackPressedDispatcher.onBackPressed()
            }

            // Assert
            scenario.onActivity { activity ->
                assertFalse(
                    "O botão voltar deve ser bloqueado e a activity NÃO deve ser finalizada",
                    activity.isFinishing
                )
            }
        }
    }

    @Test
    fun onDismissButtonClick_shouldFinishActivity() {
        // Arrange
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, AlarmActivity::class.java)

        ActivityScenario.launch<AlarmActivity>(intent).use { scenario: ActivityScenario<AlarmActivity> ->
            // Act
            composeTestRule.onNodeWithText("✓  Dispensar").performClick()

            // Assert
            scenario.onActivity { activity ->
                assertTrue(
                    "Clicar no botão Dispensar deve finalizar a activity do alarme",
                    activity.isFinishing
                )
            }
        }
    }

    @Test
    fun onReceiveDismissBroadcast_shouldFinishActivity() {
        // Arrange
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, AlarmActivity::class.java)

        ActivityScenario.launch<AlarmActivity>(intent).use { scenario: ActivityScenario<AlarmActivity> ->
            // Act: Dispara o Broadcast externo simulando clique na notificação
            val dismissIntent = Intent(AlarmActivity.ACTION_DISMISS)
            context.sendBroadcast(dismissIntent)

            // Assert
            scenario.onActivity { activity ->
                assertTrue(
                    "Receber o broadcast de ACTION_DISMISS deve finalizar a activity",
                    activity.isFinishing
                )
            }
        }
    }
}