package com.ebody.bip.features.emergency.domain

interface ExternalMessageSender {
    fun sendWhatsApp(phone: String, message: String)
    fun sendSms(phone: String, message: String)
}