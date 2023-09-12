package com.example.alexarchitecture.interfaces

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface NavigationLocation {
    val title: String
    @get:DrawableRes
    val icon: Int

    @Composable
    fun Content(modifier: Modifier)
}