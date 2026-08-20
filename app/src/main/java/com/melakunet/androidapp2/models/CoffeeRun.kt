package com.melakunet.androidapp2.models

/**
 * One coffee order saved to history.
 *
 * The date is stored as a Long (milliseconds since epoch) rather than a Date
 * object because it survives Gson serialisation cleanly and is easy to compare
 * when grouping orders by day.
 *
 * @param id unique identifier so the RecyclerView can tell rows apart
 * @param dateMillis when the order was placed
 * @param itemName name of the menu item ordered
 * @param personName who the order is for
 */
data class CoffeeRun(
    val id: String,
    val dateMillis: Long,
    val itemName: String,
    val personName: String
)
