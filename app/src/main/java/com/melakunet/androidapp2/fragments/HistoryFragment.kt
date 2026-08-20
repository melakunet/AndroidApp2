package com.melakunet.androidapp2.fragments

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.melakunet.androidapp2.R
import com.melakunet.androidapp2.models.HistoryStore
import com.melakunet.androidapp2.util.dayLabel

/**
 * Screen showing a list of all past orders, grouped by date.
 */
class HistoryFragment : Fragment(R.layout.fragment_history) {

    private lateinit var historyList: RecyclerView
    private lateinit var emptyState: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        historyList = view.findViewById(R.id.historyList)
        emptyState = view.findViewById(R.id.emptyState)
        
        historyList.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onResume() {
        super.onResume()
        // Refresh the list every time the screen comes into focus,
        // just in case an order was added on the Menu tab.
        loadHistory()
    }

    /**
     * Reads all orders from the store and builds a list that includes 
     * date headings between days.
     */
    private fun loadHistory() {
        val allRuns = HistoryStore.allRuns(requireContext()).reversed()
        
        if (allRuns.isEmpty()) {
            historyList.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
            return
        }

        historyList.visibility = View.VISIBLE
        emptyState.visibility = View.GONE

        val rows = mutableListOf<HistoryRow>()
        var lastDateLabel = ""

        allRuns.forEach { run ->
            val currentDateLabel = dayLabel(requireContext(), run.dateMillis)
            
            // If the date changed, we insert a new header before the orders for that day.
            if (currentDateLabel != lastDateLabel) {
                rows.add(HistoryRow.DateHeader(currentDateLabel))
                lastDateLabel = currentDateLabel
            }
            
            rows.add(HistoryRow.OrderRow(run))
        }

        historyList.adapter = HistoryAdapter(rows)
    }
}
