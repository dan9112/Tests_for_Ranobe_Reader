package ru.example.work_manager.util

import android.util.Log
import kotlinx.datetime.*

private const val ONE = 1
private const val TAG = "TimeVariables"

fun getDelayInSeconds(hour: Int = 3, minute: Int = 0): Long {
    val currentInstant = Clock.System.now()
    val currentLocalDateTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault())
    val currentLocalDate = currentLocalDateTime.date
    val tomorrow = currentLocalDate + DatePeriod(days = ONE)
    val tomorrowDateTime = tomorrow.atTime(hour = hour, minute = minute)
    val difference = tomorrowDateTime.toInstant(TimeZone.currentSystemDefault()) - currentInstant
    val delay = difference.inWholeSeconds
    Log.v(
        TAG,
        """
            |$currentInstant -> $currentLocalDateTime -> $currentLocalDate
            |$tomorrow -> $tomorrowDateTime
            |$difference -> $delay
        """.trimMargin()
    )
    return delay
}
