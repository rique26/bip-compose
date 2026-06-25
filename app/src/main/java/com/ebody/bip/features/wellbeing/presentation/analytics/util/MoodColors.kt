package com.ebody.bip.features.wellbeing.presentation.analytics.util

object MoodColors {
    fun getColorForMood(mood: String): String = when (mood) {
        "Ótimo" -> "#1DB954"    // Verde
        "Bem" -> "#FFD13B"      // Amarelo
        "Estranho" -> "#9C27B0" // Roxo
        "Mal" -> "#E53935"      // Vermelho
        else -> "#9E9E9E"       // Cinza (Fallbacks/Padrão)
    }
}