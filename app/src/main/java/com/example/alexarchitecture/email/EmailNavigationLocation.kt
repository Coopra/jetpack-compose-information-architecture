package com.example.alexarchitecture.email

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.window.layout.DisplayFeature
import com.example.alexarchitecture.R
import com.example.alexarchitecture.interfaces.NavigationLocation

class EmailNavigationLocation(
    private val windowSizeClass: WindowSizeClass,
    private val displayFeatures: List<DisplayFeature>
): NavigationLocation {
    override val title: String
        get() = "Email"
    override val icon: Int
        get() = R.drawable.outline_email_24

    @Composable
    override fun Content(modifier: Modifier) {
        EmailPane(windowSizeClass, displayFeatures, modifier = modifier)
    }
}
