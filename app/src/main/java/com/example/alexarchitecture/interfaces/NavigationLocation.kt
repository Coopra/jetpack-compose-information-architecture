package com.example.alexarchitecture.interfaces

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface NavigationLocation {
    val title: String
    @get:DrawableRes
    val icon: Int
    val toolbarTitle: String
    val hasDrawerContent: Boolean

    @Composable
    fun Content(modifier: Modifier)

    @Composable
    fun DrawerContent(modifier: Modifier, onDrawerItemClick: (() -> Unit)? = null): Unit? = null
}