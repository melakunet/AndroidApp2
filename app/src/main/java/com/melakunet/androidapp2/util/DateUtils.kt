package com.melakunet.androidapp2.util

import android.content.Context
import com.melakunet.androidapp2.R
import java.text.DateFormat
import java.util.Calendar
import java.util.Date

/**
 * Turns a timestamp into a section heading for the History screen:
 * "Today", "Yesterday", or a formatted date such as "Aug 18, 2026".
 */
fun dayLabel(context: Context, millis: Long): String {
    val target = Calendar.getInstance().apply { timeInMillis = millis }
    val today = Calendar.getInstance()

    if (isSameDay(target, today)) {
        return context.getString(R.string.label_today)
    }

    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    if (isSameDay(target, yesterday)) {
        return context.getString(R.string.label_yesterday)
    }

    return DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(millis))
}

/** True when two calendars land on the same day of the same year. */
private fun isSameDay(a: Calendar, b: Calendar): Boolean =
    a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
            a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
