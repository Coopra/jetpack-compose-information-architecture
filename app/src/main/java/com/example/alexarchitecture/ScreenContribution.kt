package com.example.alexarchitecture

import androidx.annotation.DrawableRes

sealed class ScreenContribution(val route: String, val title: String, @DrawableRes val icon: Int, val drawerContribution: DrawerContribution? = null) {
    object Email : ScreenContribution("email", "Email", R.drawable.outline_email_24, DrawerContribution.Email)
    object Calendar : ScreenContribution("calendar", "Calendar", R.drawable.outline_calendar_month_24)
    object Feed : ScreenContribution("feed", "Feed", R.drawable.outline_dynamic_feed_24)
    object Apps : ScreenContribution("apps", "Apps", R.drawable.outline_apps_24)
}
