package com.example.alexarchitecture.calendar

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.alexarchitecture.R
import com.example.alexarchitecture.interfaces.NavigationLocation

class CalendarNavigationLocation: NavigationLocation {
    override val title: String
        get() = "Calendar"
    override val icon: Int
        get() = R.drawable.outline_calendar_month_24

    @Composable
    override fun Content(modifier: Modifier) {
        Text(text = "Calendar")
    }
}
