package codes.snowy.dupeJS.utils

import kotlin.math.floor

object NumberConverter {

    fun convertCompact(number: Long): String {
        if (number < 1000) return number.toString()

        val suffixes = arrayOf("", "k", "M", "B", "T", "Q")
        val exp = floor(Math.log10(number.toDouble()) / 3).toInt()

        val formattedNumber = number / Math.pow(1000.0, exp.toDouble()).toLong()
        return String.format("%.1f%s", formattedNumber.toDouble(), suffixes[exp])
    }
}