package com.melakunet.androidapp2.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.melakunet.androidapp2.R
import com.melakunet.androidapp2.models.HistoryStore
import com.melakunet.androidapp2.models.menuItems

/**
 * Summarizes the coffee run for today. It shows the total count,
 * individual orders, and allows the user to share the summary via text.
 */
class ReportFragment : Fragment(R.layout.fragment_report) {

    override fun onResume() {
        super.onResume()
        // Always refresh when the user switches to this tab so the numbers
        // and lists are completely up-to-date.
        refreshReport()
    }

    private fun refreshReport() {
        val context = requireContext()
        val view = view ?: return
        val todaysRuns = HistoryStore.todaysRuns(context)

        // 1. Update the big count and its label
        val count = todaysRuns.size
        view.findViewById<TextView>(R.id.todayCount).text = count.toString()
        view.findViewById<TextView>(R.id.todayCountLabel).text = if (count == 1) {
            getString(R.string.item_ordered)
        } else {
            getString(R.string.items_ordered)
        }

        // 2. Build the list of today's orders
        val orderCard = view.findViewById<LinearLayout>(R.id.todaysOrdersCard)
        val orderList = view.findViewById<LinearLayout>(R.id.todaysOrdersList)
        orderList.removeAllViews()

        if (todaysRuns.isEmpty()) {
            orderCard.visibility = View.GONE
        } else {
            orderCard.visibility = View.VISIBLE
            todaysRuns.forEach { run ->
                // Manual creation for the specific horizontal style described
                val horizontalRow = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(0, 8, 0, 8) }
                    gravity = android.view.Gravity.CENTER_VERTICAL
                }
                
                val icon = ImageView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        (20 * resources.displayMetrics.density).toInt(),
                        (20 * resources.displayMetrics.density).toInt()
                    )
                    setImageResource(R.drawable.ic_coffee)
                    setColorFilter(androidx.core.content.ContextCompat.getColor(context, R.color.tims_red))
                }
                
                val textGroup = LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                        marginStart = (12 * resources.displayMetrics.density).toInt()
                    }
                }
                
                val itemName = TextView(context).apply {
                    text = run.itemName
                    setTextColor(android.graphics.Color.WHITE)
                    textSize = 14f
                    setShadowLayer(2f, 0f, 1f, android.graphics.Color.BLACK)
                }
                
                val personName = TextView(context).apply {
                    text = run.personName
                    setTextColor(android.graphics.Color.parseColor("#BFFFFFFF"))
                    textSize = 12f
                    setShadowLayer(2f, 0f, 1f, android.graphics.Color.BLACK)
                }
                
                textGroup.addView(itemName)
                textGroup.addView(personName)
                horizontalRow.addView(icon)
                horizontalRow.addView(textGroup)
                orderList.addView(horizontalRow)
            }
        }

        // 3. Find and show the top rated item
        refreshTopRated(view)

        // 4. Setup sharing
        view.findViewById<android.widget.Button>(R.id.shareButton).setOnClickListener {
            shareOrders(todaysRuns)
        }
    }

    private fun refreshTopRated(view: View) {
        val context = requireContext()
        val prefs = context.getSharedPreferences("coffee_run_prefs", Context.MODE_PRIVATE)
        
        // Find which menu item has the highest saved star rating.
        val topItem = menuItems
            .map { it to prefs.getInt(it.ratingKey, 0) }
            .filter { it.second > 0 }
            .maxByOrNull { it.second }

        val topRatedCard = view.findViewById<LinearLayout>(R.id.topRatedCard)
        if (topItem == null) {
            topRatedCard.visibility = View.GONE
        } else {
            topRatedCard.visibility = View.VISIBLE
            val (item, rating) = topItem
            
            view.findViewById<ImageView>(R.id.topRatedIcon).setImageResource(item.iconRes)
            view.findViewById<TextView>(R.id.topRatedName).text = getString(item.nameRes)
            
            val starsContainer = view.findViewById<LinearLayout>(R.id.topRatedStars)
            starsContainer.removeAllViews()
            val starSize = (14 * resources.displayMetrics.density).toInt()
            val starColor = androidx.core.content.ContextCompat.getColor(context, R.color.star_yellow)
            
            for (i in 1..5) {
                val star = ImageView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(starSize, starSize).apply {
                        marginEnd = (2 * resources.displayMetrics.density).toInt()
                    }
                    setImageResource(if (i <= rating) R.drawable.ic_star_filled else R.drawable.ic_star_outline)
                    setColorFilter(starColor)
                }
                starsContainer.addView(star)
            }
        }
    }

    private fun shareOrders(runs: List<com.melakunet.androidapp2.models.CoffeeRun>) {
        val shareText = if (runs.isEmpty()) {
            getString(R.string.share_no_orders)
        } else {
            val sb = StringBuilder()
            sb.append(getString(R.string.share_heading)).append("\n")
            sb.append(getString(R.string.share_count, runs.size)).append("\n\n")
            runs.forEach { run ->
                sb.append("• ${run.personName}: ${run.itemName}\n")
            }
            sb.toString()
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.share_chooser_title)))
    }
}
