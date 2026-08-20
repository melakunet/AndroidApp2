package com.melakunet.androidapp2.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.melakunet.androidapp2.R

/**
 * A full-screen timer that counts down from 30 seconds.
 * It changes the text color to red when time is running out.
 */
class TimerDialogFragment : DialogFragment(R.layout.fragment_timer) {

    private var timer: CountDownTimer? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the style to be full-screen with no title bar, matching the iOS experience.
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val countdownGroup = view.findViewById<LinearLayout>(R.id.countdownGroup)
        val finishedGroup = view.findViewById<LinearLayout>(R.id.finishedGroup)
        val secondsText = view.findViewById<TextView>(R.id.secondsText)
        
        // Start the 30-second countdown.
        timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000).toInt()
                secondsText.text = seconds.toString()
                
                // Alert the user by turning the text red during the last 10 seconds.
                if (seconds <= 10) {
                    secondsText.setTextColor(ContextCompat.getColor(requireContext(), R.color.tims_red))
                }
            }

            override fun onFinish() {
                // Swap the groups to show the finished state.
                countdownGroup.visibility = View.GONE
                finishedGroup.visibility = View.VISIBLE
            }
        }.start()

        view.findViewById<Button>(R.id.cancelButton).setOnClickListener {
            dismiss()
        }

        view.findViewById<Button>(R.id.doneButton).setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Always cancel the timer if the dialog is closed manually to prevent memory leaks.
        timer?.cancel()
    }
}
