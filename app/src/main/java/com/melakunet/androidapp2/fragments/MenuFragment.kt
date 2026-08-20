package com.melakunet.androidapp2.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.melakunet.androidapp2.R
import com.melakunet.androidapp2.models.menuItems

/**
 * The main Menu screen. It hosts a ViewPager2 to swipe through coffee items,
 * maintains the person's name across all pages, and manages the dot indicators.
 */
class MenuFragment : Fragment(R.layout.fragment_menu) {

    /**
     * Shared across all MenuItemPageFragments so the user doesn't lose the name
     * they typed when they swipe between drinks.
     */
    var currentPersonName: String = ""

    private lateinit var menuPager: ViewPager2
    private lateinit var dotContainer: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        menuPager = view.findViewById(R.id.menuPager)
        dotContainer = view.findViewById(R.id.dotContainer)

        // 1. Setup the adapter to create one fragment per menu item
        menuPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = menuItems.size
            override fun createFragment(position: Int): Fragment = 
                MenuItemPageFragment.newInstance(position)
        }

        // 2. Create the dots for the page indicator
        buildDots()

        // 3. Update the dots whenever the user swipes to a new page
        menuPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position)
            }
        })
    }

    /** Switches the pager to a specific page index (e.g. from chevron clicks). */
    fun moveToPage(position: Int) {
        menuPager.setCurrentItem(position, true)
    }

    /** Creates one white dot per menu item in the header. */
    private fun buildDots() {
        dotContainer.removeAllViews()
        val context = requireContext()
        menuItems.forEach { _ ->
            val dot = ImageView(context).apply {
                setImageResource(R.drawable.dot_indicator)
                val size = (8 * resources.displayMetrics.density).toInt()
                val margin = (4 * resources.displayMetrics.density).toInt()
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    setMargins(margin, 0, margin, 0)
                }
            }
            dotContainer.addView(dot)
        }
        updateDots(0)
    }

    /** Tints the active dot full white and the others semi-transparent. */
    private fun updateDots(activePosition: Int) {
        for (i in 0 until dotContainer.childCount) {
            val dot = dotContainer.getChildAt(i) as ImageView
            dot.alpha = if (i == activePosition) 1.0f else 0.35f
        }
    }
}
