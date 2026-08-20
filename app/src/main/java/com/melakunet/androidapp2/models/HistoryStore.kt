package com.melakunet.androidapp2.models

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar
import java.util.UUID

/**
 * Stores every coffee order and saves them between app launches.
 *
 * Orders are converted to a JSON string by Gson and written to SharedPreferences,
 * which mirrors how the iOS version encodes to JSON and writes to UserDefaults.
 *
 * Implemented as an object (a singleton) so every screen reads and writes the
 * same list without needing to pass the store around between fragments.
 */
object HistoryStore {

    private const val PREFS_NAME = "coffee_run_prefs"
    private const val HISTORY_KEY = "coffeeRunHistory"

    private val gson = Gson()

    /** Orders held in memory, oldest first. Loaded from storage on first use. */
    private var runs: MutableList<CoffeeRun> = mutableListOf()
    private var isLoaded = false

    /** Reads saved orders from SharedPreferences. Safe to call more than once. */
    private fun ensureLoaded(context: Context) {
        if (isLoaded) return

        val prefs = context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(HISTORY_KEY, null)

        if (json != null) {
            // Gson needs the full generic type to rebuild a List<CoffeeRun>
            val type = object : TypeToken<MutableList<CoffeeRun>>() {}.type
            runs = gson.fromJson(json, type) ?: mutableListOf()
        }
        isLoaded = true
    }

    /** Writes the current list back to SharedPreferences as JSON. */
    private fun save(context: Context) {
        context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(HISTORY_KEY, gson.toJson(runs))
            .apply()
    }

    /**
     * Records a new order. A blank name falls back to "Team Member" so the
     * history never shows an empty row.
     */
    fun add(context: Context, itemName: String, personName: String) {
        ensureLoaded(context)
        runs.add(
            CoffeeRun(
                id = UUID.randomUUID().toString(),
                dateMillis = System.currentTimeMillis(),
                itemName = itemName,
                personName = personName.ifBlank { "Team Member" }
            )
        )
        save(context)
    }

    /** Every order ever placed, oldest first. */
    fun allRuns(context: Context): List<CoffeeRun> {
        ensureLoaded(context)
        return runs.toList()
    }

    /** Only orders placed today — used by the Report screen. */
    fun todaysRuns(context: Context): List<CoffeeRun> {
        ensureLoaded(context)
        return runs.filter { isToday(it.dateMillis) }
    }

    /** True when the given timestamp falls on the current calendar day. */
    private fun isToday(millis: Long): Boolean {
        val today = Calendar.getInstance()
        val other = Calendar.getInstance().apply { timeInMillis = millis }
        return today.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)
    }
}
