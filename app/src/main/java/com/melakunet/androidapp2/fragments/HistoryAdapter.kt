package com.melakunet.androidapp2.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.melakunet.androidapp2.R
import com.melakunet.androidapp2.models.CoffeeRun
import java.text.DateFormat
import java.util.Date

/**
 * Possible row types for the history list.
 */
sealed class HistoryRow {
    data class DateHeader(val label: String) : HistoryRow()
    data class OrderRow(val run: CoffeeRun) : HistoryRow()
}

/**
 * Adapter that can show both date headings and individual order cards.
 */
class HistoryAdapter(private val rows: List<HistoryRow>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when (rows[position]) {
            is HistoryRow.DateHeader -> 0
            is HistoryRow.OrderRow -> 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 0) {
            HeaderViewHolder(inflater.inflate(R.layout.item_history_header, parent, false))
        } else {
            OrderViewHolder(inflater.inflate(R.layout.item_history_order, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val row = rows[position]
        if (holder is HeaderViewHolder && row is HistoryRow.DateHeader) {
            holder.label.text = row.label
        } else if (holder is OrderViewHolder && row is HistoryRow.OrderRow) {
            val run = row.run
            val context = holder.itemView.context
            
            holder.itemName.text = run.itemName
            holder.personName.text = context.getString(R.string.ordered_by, run.personName)
            
            val date = Date(run.dateMillis)
            val dateStr = DateFormat.getDateInstance(DateFormat.MEDIUM).format(date)
            val timeStr = DateFormat.getTimeInstance(DateFormat.SHORT).format(date)
            holder.dateTime.text = "$dateStr • $timeStr"
        }
    }

    override fun getItemCount(): Int = rows.size

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.headerLabel)
    }

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.orderItemName)
        val personName: TextView = view.findViewById(R.id.orderPersonName)
        val dateTime: TextView = view.findViewById(R.id.orderDateTime)
    }
}
