package com.ebody.bip.core.presentation.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val clean = text.text.filter { it.isDigit() }.take(8)

        val out = StringBuilder()
        for (i in clean.indices) {
            out.append(clean[i])
            if (i == 1 || i == 3) {
                if (i < clean.lastIndex) {
                    out.append("/")
                }
            }
        }

        return TransformedText(AnnotatedString(out.toString()), DateOffsetMapping(clean))
    }
}

private class DateOffsetMapping(private val cleanText: String) : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int {
        if (offset <= 2) return offset
        if (offset <= 4) return offset + 1
        if (offset <= 8) return offset + 2
        return cleanText.length + 2
    }

    override fun transformedToOriginal(offset: Int): Int {
        if (offset <= 2) return offset
        if (offset <= 5) return offset - 1
        if (offset <= 10) return offset - 2
        return cleanText.length
    }
}

class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val clean = text.text.filter { it.isDigit() }.take(11)

        val out = StringBuilder()
        for (i in clean.indices) {
            when (i) {
                0 -> out.append("(").append(clean[i])
                2 -> out.append(") ").append(clean[i])
                7 -> out.append("-").append(clean[i])
                else -> out.append(clean[i])
            }
        }

        return TransformedText(AnnotatedString(out.toString()), PhoneOffsetMapping(clean))
    }
}

private class PhoneOffsetMapping(private val cleanText: String) : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int {
        if (offset <= 0) return offset
        if (offset <= 2) return offset + 1 // Adiciona '('
        if (offset <= 7) return offset + 3 // Adiciona ') '
        if (offset <= 11) return offset + 4 // Adiciona '-'
        return cleanText.length + 4
    }

    override fun transformedToOriginal(offset: Int): Int {
        if (offset <= 1) return 0
        if (offset <= 4) return offset - 1
        if (offset <= 9) return offset - 3
        if (offset <= 15) return offset - 4
        return cleanText.length
    }
}