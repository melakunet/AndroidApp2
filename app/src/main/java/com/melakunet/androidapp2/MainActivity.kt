package com.melakunet.androidapp2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.melakunet.androidapp2.fragments.HistoryFragment
import com.melakunet.androidapp2.fragments.MenuFragment
import com.melakunet.androidapp2.fragments.ReportFragment
import com.melakunet.androidapp2.fragments.WelcomeFragment

/**
 * Single host activity for the Tim Hortons Coffee Run app.
 *
 * It manages the bottom navigation and swaps between the four main fragments.
 * Edge-to-edge is used so the background runs behind the system bars, while
 * padding ensures our content doesn't get hidden under them.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottomNavigation)
        val fragmentContainer = findViewById<android.view.View>(R.id.fragmentContainer)

        // Swapping fragments when a tab is clicked.
        bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_welcome -> WelcomeFragment()
                R.id.nav_menu -> MenuFragment()
                R.id.nav_history -> HistoryFragment()
                R.id.nav_report -> ReportFragment()
                else -> return@setOnItemSelectedListener false
            }
            replaceFragment(fragment)
            true
        }

        // On first launch, start with the Welcome screen.
        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_welcome
        }

        // Apply system bar insets so content avoids the status and nav bars.
        // We apply them to the fragment container and nav bar, not the root,
        // so the coffee gradient still fills the entire screen.
        ViewCompat.setOnApplyWindowInsetsListener(fragmentContainer) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

            // The top padding handles the status bar. For the bottom, we take the 
            // larger of the navigation bar or the keyboard (IME). Since the 
            // container already sits above the nav bar, we subtract its height
            // to avoid double-padding the bottom.
            val bottomPadding = (maxOf(bars.bottom, ime.bottom) - bars.bottom).coerceAtLeast(0)
            
            v.setPadding(0, bars.top, 0, bottomPadding)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }
    }

    /** Switches the bottom navigation to the Menu tab. */
    fun showMenuTab() {
        bottomNav.selectedItemId = R.id.nav_menu
    }

    /** Switches the bottom navigation to the History tab. */
    fun showHistoryTab() {
        bottomNav.selectedItemId = R.id.nav_history
    }

    /** Helper to replace the current fragment in the container. */
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
