package com.appydinos.moviebrowser.extensions

import java.math.BigDecimal
import kotlin.math.roundToInt

fun Int.toHoursAndMinutes(): String {
    val decimalValue: Double = this.toDouble() / 60
    val bigDecimal = BigDecimal(decimalValue)
    val hours = bigDecimal.toInt()
    val minutes = (bigDecimal.subtract(BigDecimal(hours)).toDouble() * 60).roundToInt()

    val hrsText = if (hours > 0) "${hours}h " else ""
    return "$hrsText${minutes}m"
}
