package com.example.alexarchitecture

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.alexarchitecture.apps.AppsNavigationLocation
import com.example.alexarchitecture.calendar.CalendarNavigationLocation
import com.example.alexarchitecture.email.EmailNavigationLocation
import com.example.alexarchitecture.feed.FeedNavigationLocation
import com.example.alexarchitecture.interfaces.NavigationLocation
import com.example.alexarchitecture.ui.theme.AlexArchitectureTheme
import kotlinx.coroutines.launch

@Composable
fun MainPane(
    windowSizeClass: WindowSizeClass,
    navigationLocations: List<NavigationLocation>
) {
    assert(navigationLocations.isNotEmpty())
    val widthSizeClass by rememberUpdatedState(windowSizeClass.widthSizeClass)

    when (widthSizeClass) {
        WindowWidthSizeClass.Compact, WindowWidthSizeClass.Medium -> CompactPane(navigationLocations)
        WindowWidthSizeClass.Expanded -> ExpandedPane(navigationLocations)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompactPane(navigationLocations: List<NavigationLocation>) {
    var navigationLocationIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    var selectedEmailFolder by rememberSaveable {
        mutableStateOf(EmailFolders.Inbox)
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch {
            drawerState.close()
        }
    }

    Scaffold(bottomBar = {
        NavigationBar {
            navigationLocations.forEachIndexed { index, navigationLocation ->
                NavigationBarItem(selected = navigationLocationIndex == index, onClick = { navigationLocationIndex = index }, icon = {
                    Icon(painter = painterResource(id = navigationLocation.icon), contentDescription = navigationLocation.title)
                })
            }
        }
    }, floatingActionButton = {
        FloatingActionButton(onClick = { /*TODO*/ }) {
            Icon(painter = painterResource(id = R.drawable.outline_edit_24), contentDescription = "Compose")
        }
    }, topBar = {
        TopAppBar(title = { Text(text = selectedEmailFolder.title) }, navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    drawerState.open()
                }
            }) {
                Icon(painter = painterResource(id = R.drawable.outline_menu_24), contentDescription = "Menu")
            }
        })
    }, drawerContent = {
        ModalDrawerSheet {
            Spacer(Modifier.height(12.dp))
            EmailFolders.values().forEach {
                NavigationDrawerItem(label = { Text(text = it.title) },
                    selected = selectedEmailFolder == it,
                    onClick = {
                        selectedEmailFolder = it
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    icon = { Icon(painter = painterResource(id = it.icon), contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    }, scaffoldState = rememberScaffoldState(drawerState = drawerState)) { padding ->
        navigationLocations[navigationLocationIndex].Content(modifier = Modifier.padding(padding))
    }
}

@Composable
private fun ExpandedPane(navigationLocations: List<NavigationLocation>) {
    var navigationLocationIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    var selectedEmailFolder by rememberSaveable {
        mutableStateOf(EmailFolders.Inbox)
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    Row {
        NavigationRail {
            IconButton(onClick = {
                when (drawerState.currentValue) {
                    DrawerValue.Open -> scope.launch { drawerState.close() }
                    DrawerValue.Closed -> scope.launch { drawerState.open() }
                }
            }) {
                Icon(painter = painterResource(id = R.drawable.outline_menu_24), contentDescription = "Menu")
            }

            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(painter = painterResource(id = R.drawable.outline_edit_24), contentDescription = "Compose")
            }

            Spacer(Modifier.height(24.dp))

            navigationLocations.forEachIndexed { index, navigationLocation ->
                NavigationRailItem(selected = navigationLocationIndex == index,
                    onClick = { navigationLocationIndex = index },
                    icon = { Icon(painter = painterResource(id = navigationLocation.icon), contentDescription = navigationLocation.title) })
            }
        }

        MyDismissibleNavigationDrawer(drawerContent = {
            DismissibleDrawerSheet {
                Spacer(Modifier.height(12.dp))
                EmailFolders.values().forEach {
                    NavigationDrawerItem(label = { Text(text = it.title) },
                        selected = selectedEmailFolder == it,
                        onClick = { selectedEmailFolder = it },
                        icon = { Icon(painter = painterResource(id = it.icon), contentDescription = null) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }, drawerState = drawerState) {
            navigationLocations[navigationLocationIndex].Content(modifier = Modifier)
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@DeviceSizePreviews
@Composable
fun MainPanePreview() {
    AlexArchitectureTheme {
        val configuration = LocalConfiguration.current
        MainPane(WindowSizeClass.calculateFromSize(DpSize(configuration.screenWidthDp.dp, configuration.screenHeightDp.dp)), listOf(
            EmailNavigationLocation(WindowSizeClass.calculateFromSize(DpSize(configuration.screenWidthDp.dp, configuration.screenHeightDp.dp)), listOf()),
            CalendarNavigationLocation(),
            FeedNavigationLocation(),
            AppsNavigationLocation()
        ))
    }
}

@Preview(name = "Phone", showSystemUi = true, device = "spec:width=411dp,height=891dp")
@Preview(name = "Foldable", showSystemUi = true, device = "spec:width=673dp,height=841dp")
@Preview(name = "Tablet", showSystemUi = true, device = "spec:width=1280dp,height=800dp,dpi=240")
annotation class DeviceSizePreviews

enum class EmailFolders(
    val title: String,
    @DrawableRes
    val icon: Int
) {
    Inbox("Inbox", R.drawable.outline_inbox_24), Drafts("Drafts", R.drawable.outline_edit_note_24), Archive(
        "Archive", R.drawable.outline_archive_24
    ),
    Sent("Sent", R.drawable.outline_send_24), Deleted("Deleted", R.drawable.outline_delete_24), Junk("Junk", R.drawable.outline_block_24)
}

@Composable
fun MyDismissibleNavigationDrawer(
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Open),
    content: @Composable () -> Unit,
) {
    Row(modifier.fillMaxSize()) {
        AnimatedVisibility(visible = drawerState.currentValue == DrawerValue.Open) {
            drawerContent()
        }
        Box {
            content()
        }
    }
}
