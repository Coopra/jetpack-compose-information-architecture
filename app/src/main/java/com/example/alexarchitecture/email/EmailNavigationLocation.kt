package com.example.alexarchitecture.email

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.layout.DisplayFeature
import com.example.alexarchitecture.R
import com.example.alexarchitecture.interfaces.EmailFolder
import com.example.alexarchitecture.interfaces.NavigationLocation

class EmailNavigationLocation(
    private val windowSizeClass: WindowSizeClass,
    private val displayFeatures: List<DisplayFeature>
): NavigationLocation {
    override val title: String
        get() = "Email"
    override val icon: Int
        get() = R.drawable.outline_email_24
    override var toolbarTitle: String = title
    override val hasDrawerContent: Boolean = true

    @Composable
    override fun Content(modifier: Modifier) {
        val viewModel: EmailViewModel = viewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        EmailPane(windowSizeClass, displayFeatures, modifier = modifier, selectedFolder = uiState.selectedFolder)
    }

    @Composable
    override fun DrawerContent(
        modifier: Modifier,
        onDrawerItemClick: (() -> Unit)?) {
        val viewModel: EmailViewModel = viewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        toolbarTitle = uiState.selectedFolder.title

        EmailFolder.values().forEach { emailFolder ->
            NavigationDrawerItem(label = { Text(text = emailFolder.title) },
                selected = emailFolder == uiState.selectedFolder,
                onClick = {
                    viewModel.updateFolderSelection(emailFolder)
                    toolbarTitle = uiState.selectedFolder.title
                    onDrawerItemClick?.invoke()
                },
                icon = { Icon(painter = painterResource(id = emailFolder.icon), contentDescription = null) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}
