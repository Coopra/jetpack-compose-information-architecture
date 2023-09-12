package com.example.alexarchitecture.email

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.window.layout.DisplayFeature
import com.example.alexarchitecture.R
import com.example.alexarchitecture.interfaces.NavigationLocation

class EmailNavigationLocation(
    private val windowSizeClass: WindowSizeClass,
    private val displayFeatures: List<DisplayFeature>
): NavigationLocation {
    private val emailFolders = listOf(InboxFolder(), DraftsFolder(), ArchiveFolder(), SentFolder(), DeletedFolder(), JunkFolder())
    private var selectedFolder by mutableStateOf(emailFolders.first())

    override val title: String
        get() = "Email"
    override val icon: Int
        get() = R.drawable.outline_email_24
    override var toolbarTitle: String = title
    override val hasDrawerContent: Boolean = true

    @Composable
    override fun Content(modifier: Modifier) {
        EmailPane(windowSizeClass, displayFeatures, modifier = modifier, selectedFolder = selectedFolder)
    }

    @Composable
    override fun DrawerContent(
        modifier: Modifier,
        onDrawerItemClick: (() -> Unit)?) {
        toolbarTitle = selectedFolder.title

        emailFolders.forEach { emailFolder ->
            NavigationDrawerItem(label = { Text(text = emailFolder.title) },
                selected = selectedFolder == emailFolder,
                onClick = {
                    selectedFolder = emailFolder
                    toolbarTitle = selectedFolder.title
                    onDrawerItemClick?.invoke()
                },
                icon = { Icon(painter = painterResource(id = emailFolder.icon), contentDescription = null) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}
