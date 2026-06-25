package com.ebody.bip.core.presentation.util

object InputMasks {
    fun formatBirthDate(input: String): String {
        val clean = input.filter { it.isDigit() }
        if (clean.isEmpty()) return ""

        val builder = StringBuilder()
        for (i in clean.indices) {
            if (i == 2) builder.append('/')
            if (i == 4) builder.append('/')
            if (i < 8) builder.append(clean[i])
        }
        return builder.toString()
    }

    fun formatPhone(input: String): String {
        val clean = input.filter { it.isDigit() }
        return clean.take(11).mapIndexed { i, c ->
            when (i) {
                0 -> "($c"; 2 -> ") $c"; 7 -> "-$c"; else -> c
            }
        }.joinToString("")
    }
}
