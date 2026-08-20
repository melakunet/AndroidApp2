package com.melakunet.androidapp2.models

import androidx.annotation.DrawableRes
import com.melakunet.androidapp2.R

/**
 * A single item on the Tim Hortons menu.
 *
 * @param id position in the menu list, also used as the ViewPager2 page index
 * @param nameRes string resource for the item's display name
 * @param descriptionRes string resource for the item's description
 * @param iconRes drawable shown on the item's page
 * @param ratingKey unique SharedPreferences key so each item stores its own
 *                  star rating without overwriting the others
 */
data class MenuItem(
    val id: Int,
    val nameRes: Int,
    val descriptionRes: Int,
    @DrawableRes val iconRes: Int,
    val ratingKey: String
)

/**
 * The fixed Tim Hortons menu. Adding an item here automatically adds a page to
 * the swipeable menu and a dot to the header indicator, since both are built
 * from this list's size.
 */
val menuItems: List<MenuItem> = listOf(
    MenuItem(
        id = 0,
        nameRes = R.string.item_double_double_name,
        descriptionRes = R.string.item_double_double_desc,
        iconRes = R.drawable.ic_coffee,
        ratingKey = "rating_DoubleDouble"
    ),
    MenuItem(
        id = 1,
        nameRes = R.string.item_iced_capp_name,
        descriptionRes = R.string.item_iced_capp_desc,
        iconRes = R.drawable.ic_snowflake,
        ratingKey = "rating_IcedCapp"
    ),
    MenuItem(
        id = 2,
        nameRes = R.string.item_french_vanilla_name,
        descriptionRes = R.string.item_french_vanilla_desc,
        iconRes = R.drawable.ic_flame,
        ratingKey = "rating_FrenchVanilla"
    ),
    MenuItem(
        id = 3,
        nameRes = R.string.item_boston_cream_name,
        descriptionRes = R.string.item_boston_cream_desc,
        iconRes = R.drawable.ic_donut,
        ratingKey = "rating_BostonCream"
    )
)
