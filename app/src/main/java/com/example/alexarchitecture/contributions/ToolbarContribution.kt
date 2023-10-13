package com.example.alexarchitecture.contributions

import androidx.annotation.DrawableRes
import com.example.alexarchitecture.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

sealed class ToolbarContribution(
    val actions: List<ToolbarAction> = emptyList()
) {
    abstract val title: StateFlow<String>

    abstract fun updateTitle(newTitle: String)

    object Email : ToolbarContribution(
        actions = listOf(ToolbarAction("Notifications", R.drawable.outline_notifications_24) {},
            ToolbarAction("Search", R.drawable.outline_search_24) {})
    ) {
        private val _title = MutableStateFlow("Email")
        override val title: StateFlow<String>
            get() = _title.asStateFlow()

        override fun updateTitle(newTitle: String) {
            _title.update {
                newTitle
            }
        }
    }

    object Calendar : ToolbarContribution(
        actions = listOf(ToolbarAction("Month view", R.drawable.baseline_calendar_view_month_24) {},
            ToolbarAction("Search", R.drawable.outline_search_24) {})
    ) {
        private var _title = MutableStateFlow(getMonth())

        override val title: StateFlow<String>
            get() = _title.asStateFlow()

        override fun updateTitle(newTitle: String) {
            _title.update {
                newTitle
            }
        }
    }
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
