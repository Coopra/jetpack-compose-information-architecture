package com.example.alexarchitecture

import androidx.compose.runtime.Composable
import com.example.alexarchitecture.email.EmailDrawer

sealed class DrawerContribution(val drawerContent: (@Composable () -> Unit)) {
    object Email : DrawerContribution({ EmailDrawer() })
}
