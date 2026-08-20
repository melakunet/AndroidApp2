package com.melakunet.androidapp2.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.melakunet.androidapp2.MainActivity
import com.melakunet.androidapp2.R

/**
 * The landing page for the app. It welcomes the user and has a button to jump
 * straight into ordering.
 */
class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // When the user clicks "Get Started", we tell the main activity to
        // switch us over to the Menu tab.
        view.findViewById<Button>(R.id.getStartedButton).setOnClickListener {
            (activity as? MainActivity)?.showMenuTab()
        }
    }
}
