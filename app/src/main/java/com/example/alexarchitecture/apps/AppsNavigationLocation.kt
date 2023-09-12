package com.example.alexarchitecture.apps

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.alexarchitecture.R
import com.example.alexarchitecture.interfaces.NavigationLocation

class AppsNavigationLocation: NavigationLocation {
    override val title: String
        get() = "Apps"
    override val icon: Int
        get() = R.drawable.outline_apps_24

    @Composable
    override fun Content(modifier: Modifier) {
        Text(text = "Apps")
    }
}
