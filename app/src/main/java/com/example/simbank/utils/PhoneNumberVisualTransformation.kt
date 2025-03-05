package com.example.simbank.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val formattedNumber = formatPhoneNumber(text.text)
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // Correct: Handle empty text and other cases
                if (text.text.isEmpty()) return 0
                return when {
                    offset <= 0 -> 0
                    offset <= 3 -> offset + 1
                    offset <= 6 -> offset + 3
                    offset <= 10 -> offset + 4
                    else -> formattedNumber.length
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (text.text.isEmpty()) return 0
                return when {
                    offset <= 1 -> 0
                    offset <= 5 -> offset - 1
                    offset <= 9 -> offset - 3
                    offset <= 14 -> offset - 4
                    else -> 10
                }
            }
        }

        return TransformedText(AnnotatedString(formattedNumber), offsetMapping)
    }

    private fun formatPhoneNumber(number: String): String {
        if (number.isEmpty()) return ""
        val digits = number.filter { it.isDigit() }
        val formatted = StringBuilder()
        if (digits.isNotEmpty()) {
            formatted.append("(")
            formatted.append(digits.substring(0, minOf(3, digits.length)))
            if (digits.length > 3) {
                formatted.append(") ")
                formatted.append(digits.substring(3, minOf(6, digits.length)))
                if (digits.length > 6) {
                    formatted.append("-")
                    formatted.append(digits.substring(6, minOf(10, digits.length)))
                }
            }
        }
        return formatted.toString()
    }
}