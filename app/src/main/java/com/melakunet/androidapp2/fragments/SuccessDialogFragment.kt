package com.melakunet.androidapp2.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.melakunet.androidapp2.MainActivity
import com.melakunet.androidapp2.R

/**
 * A full-screen success screen that confirms the order was added.
 * It allows the user to quickly jump to the history or order more.
 */
class SuccessDialogFragment : DialogFragment(R.layout.fragment_success) {

    companion object {
        private const val ARG_ITEM = "itemName"
        private const val ARG_PERSON = "personName"

        /** Factory method to create the success screen with order details. */
        fun newInstance(itemName: String, personName: String) = SuccessDialogFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ITEM, itemName)
                putString(ARG_PERSON, personName)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Full-screen style with the coffee background.
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemName = arguments?.getString(ARG_ITEM) ?: ""
        val personName = arguments?.getString(ARG_PERSON) ?: ""

        view.findViewById<TextView>(R.id.successItemName).text = itemName
        view.findViewById<TextView>(R.id.successPersonName).text = 
            getString(R.string.success_for_person, personName)

        view.findViewById<Button>(R.id.viewHistoryButton).setOnClickListener {
            dismiss()
            // Direct navigation to the History tab.
            (activity as? MainActivity)?.showHistoryTab()
        }

        view.findViewById<Button>(R.id.orderAnotherButton).setOnClickListener {
            dismiss()
        }
    }
}
