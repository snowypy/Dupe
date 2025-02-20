package codes.snowy.dupeJS.utils

import java.text.DecimalFormat

object NumberFormatter {

    fun convertShort(number: Double): String {
        val formatter = DecimalFormat("#,###.##")
        return formatter.format(number)
    }

    fun convertInt(number: Double): String {
        val formatter = DecimalFormat("#,###")
        return formatter.format(number)
    }
}