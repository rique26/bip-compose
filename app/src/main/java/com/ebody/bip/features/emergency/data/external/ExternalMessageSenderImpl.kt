package com.ebody.bip.features.emergency.data.external

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager
import android.util.Log
import com.ebody.bip.features.emergency.domain.ExternalMessageSender
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ExternalMessageSenderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ExternalMessageSender {

    // Tag para filtrar no Logcat do Android Studio
    private val TAG = "EmergencySender"

    override fun sendWhatsApp(phone: String, message: String) {
        try {
            val formattedNumber = if (phone.startsWith("+")) phone else "+55$phone"
            val url = "https://api.whatsapp.com/send?phone=$formattedNumber&text=${Uri.encode(message)}"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)

            // Log de sucesso
            Log.d(TAG, "Sucesso: Intent do WhatsApp disparada para $formattedNumber")

        } catch (e: Exception) {
            // Log de erro
            Log.e(TAG, "Erro ao tentar enviar WhatsApp para $phone", e)
        }
    }

    override fun sendSms(phone: String, message: String) {
        try {
            val formattedNumber = formatPhoneNumber(phone)
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(formattedNumber, null, message, null, null)
            Log.d(TAG, "Sucesso: SMS enviado para $formattedNumber")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao tentar enviar SMS para $phone", e)
        }
    }

    private fun formatPhoneNumber(rawNumber: String): String {
        return if (rawNumber.startsWith("+")) {
            rawNumber
        } else {
            "+55$rawNumber"
        }
    }
}