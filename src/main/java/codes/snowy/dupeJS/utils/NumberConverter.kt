package codes.snowy.dupeJS.utils

import kotlin.math.floor

object NumberConverter {

    fun convertCompact(number: Long): String {
        if (number < 1000) return number.toString()

        val suffixes = arrayOf("", "k", "M", "B", "T", "Q")
        val exp = floor(Math.log10(number.toDouble()) / 3).toInt()
        
        val value = number / Math.pow(1000.0, exp.toDouble())
        return if (value % 1 == 0.0) {
            String.format("%d%s", value.toLong(), suffixes[exp])
        } else {
            String.format("%.2f%s", value, suffixes[exp])
        }
    }
}