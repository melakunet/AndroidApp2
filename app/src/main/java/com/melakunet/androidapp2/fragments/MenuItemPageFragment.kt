package com.melakunet.androidapp2.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.melakunet.androidapp2.R
import com.melakunet.androidapp2.models.HistoryStore
import com.melakunet.androidapp2.models.menuItems

/**
 * Handles the display and logic for one specific menu item (e.g. "Double Double").
 * It manages the selection state, star ratings, and order submission for that item.
 */
class MenuItemPageFragment : Fragment(R.layout.item_menu_page) {

    private var position: Int = 0
    private var isSelected: Boolean = false
    private var currentRating: Int = 0

    // UI elements
    private lateinit var itemIcon: ImageView
    private lateinit var selectButton: Button
    private lateinit var doneButton: Button
    private lateinit var nameInput: EditText
    private lateinit var starViews: List<ImageView>

    companion object {
        private const val ARG_POSITION = "position"

        /** Creates a new page for the menu at the given index. */
        fun newInstance(position: Int) = MenuItemPageFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_POSITION, position)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments?.getInt(ARG_POSITION) ?: 0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val item = menuItems[position]

        // 1. Fill in static item data
        itemIcon = view.findViewById(R.id.itemIcon)
        itemIcon.setImageResource(item.iconRes)
        view.findViewById<TextView>(R.id.itemName).text = getString(item.nameRes)
        view.findViewById<TextView>(R.id.itemDescription).text = getString(item.descriptionRes)
        view.findViewById<TextView>(R.id.pageCounter).text = 
            getString(R.string.page_counter, position + 1, menuItems.size)

        // 2. Setup selection logic
        selectButton = view.findViewById(R.id.selectButton)
        selectButton.setOnClickListener {
            isSelected = !isSelected
            updateSelectionUi()
            refreshDoneButton()
        }
        updateSelectionUi()

        // 3. Setup star ratings from SharedPreferences
        val prefs = requireContext().getSharedPreferences("coffee_run_prefs", Context.MODE_PRIVATE)
        currentRating = prefs.getInt(item.ratingKey, 0)
        
        starViews = listOf(
            view.findViewById(R.id.star1),
            view.findViewById(R.id.star2),
            view.findViewById(R.id.star3),
            view.findViewById(R.id.star4),
            view.findViewById(R.id.star5)
        )

        starViews.forEachIndexed { index, star ->
            star.setOnClickListener {
                currentRating = index + 1
                prefs.edit().putInt(item.ratingKey, currentRating).apply()
                updateStars()
            }
        }
        updateStars()

        // 4. Setup navigation chevrons
        val prevBtn = view.findViewById<ImageButton>(R.id.previousButton)
        val nextBtn = view.findViewById<ImageButton>(R.id.nextButton)

        if (position == 0) prevBtn.visibility = View.INVISIBLE
        if (position == menuItems.size - 1) nextBtn.visibility = View.INVISIBLE

        prevBtn.setOnClickListener { (parentFragment as? MenuFragment)?.moveToPage(position - 1) }
        nextBtn.setOnClickListener { (parentFragment as? MenuFragment)?.moveToPage(position + 1) }

        // 5. Setup name input (shared with parent MenuFragment)
        nameInput = view.findViewById(R.id.nameInput)
        val parent = parentFragment as? MenuFragment
        nameInput.setText(parent?.currentPersonName ?: "")

        nameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                parent?.currentPersonName = s.toString()
                refreshDoneButton()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 6. Setup action buttons
        view.findViewById<Button>(R.id.timerButton).setOnClickListener {
            Toast.makeText(context, "Timer coming soon", Toast.LENGTH_SHORT).show()
        }

        doneButton = view.findViewById(R.id.doneButton)
        doneButton.setOnClickListener {
            addOrderToHistory()
        }
        refreshDoneButton()
    }

    /** Toggles the icon color and button text when an item is selected. */
    private fun updateSelectionUi() {
        val context = requireContext()
        if (isSelected) {
            selectButton.text = getString(R.string.selected)
            selectButton.backgroundTintList = ContextCompat.getColorStateList(context, R.color.selected_green)
            itemIcon.setColorFilter(ContextCompat.getColor(context, R.color.selected_green))
        } else {
            selectButton.text = getString(R.string.select_to_order)
            selectButton.backgroundTintList = ContextCompat.getColorStateList(context, android.R.color.transparent) // Using card overlay logic
            // The prompt says #1FFFFFFF when false, which is card_overlay
            selectButton.setBackgroundColor(Color.parseColor("#1FFFFFFF"))
            itemIcon.setColorFilter(ContextCompat.getColor(context, R.color.tims_red))
        }
    }

    /** Fills in stars up to the current rating and outlines the rest. */
    private fun updateStars() {
        val starColor = ContextCompat.getColor(requireContext(), R.color.star_yellow)
        starViews.forEachIndexed { index, imageView ->
            if (index < currentRating) {
                imageView.setImageResource(R.drawable.ic_star_filled)
            } else {
                imageView.setImageResource(R.drawable.ic_star_outline)
            }
            imageView.setColorFilter(starColor)
        }
    }

    /** Enables the "Done" button only if an item is selected and a name exists. */
    private fun refreshDoneButton() {
        val hasName = nameInput.text.trim().isNotEmpty()
        val canSubmit = isSelected && hasName
        doneButton.isEnabled = canSubmit
        doneButton.alpha = if (canSubmit) 1.0f else 0.5f
    }

    /** Saves the order, clears the input, and resets the page. */
    private fun addOrderToHistory() {
        val name = nameInput.text.toString().trim()
        val itemName = getString(menuItems[position].nameRes)
        
        // Hide the keyboard so it's not in the way of the next step
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)

        HistoryStore.add(requireContext(), itemName, name)

        // Clear everything for the next order
        nameInput.setText("")
        (parentFragment as? MenuFragment)?.currentPersonName = ""
        isSelected = false
        updateSelectionUi()
        refreshDoneButton()

        Toast.makeText(context, "Order added for $name", Toast.LENGTH_SHORT).show()
    }
}
