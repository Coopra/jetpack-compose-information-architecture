package com.example.alexarchitecture.contributions

import androidx.annotation.DrawableRes
import com.example.alexarchitecture.R

sealed class FABContribution(
    val description: String,
    @DrawableRes val icon: Int
) {
    object Email : FABContribution("New email", R.drawable.outline_edit_24)

    object Calendar : FABContribution("New event", R.drawable.outline_add_circle_outline_24)
}
