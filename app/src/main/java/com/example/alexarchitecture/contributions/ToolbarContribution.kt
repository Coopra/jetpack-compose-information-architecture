package com.example.alexarchitecture.contributions

import androidx.annotation.DrawableRes
import com.example.alexarchitecture.R
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

sealed class ToolbarContribution(
    var title: String,
    val actions: List<ToolbarAction> = emptyList()
) {
    object Email : ToolbarContribution(
        title = "Email",
        actions = listOf(ToolbarAction("Notifications", R.drawable.outline_notifications_24) {},
            ToolbarAction("Search", R.drawable.outline_search_24) {})
    )

    object Calendar : ToolbarContribution(
        title = getMonth(),
        actions = listOf(ToolbarAction("Month view", R.drawable.baseline_calendar_view_month_24) {},
            ToolbarAction("Search", R.drawable.outline_search_24) {})
    )
}

data class ToolbarAction(
    val title: String,
    @DrawableRes
    val icon: Int,
    val onClick: () -> Unit
)

private fun getMonth(): String {
    return LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale.getDefault())
}
