package com.example.alexarchitecture

import androidx.activity.compose.BackHandler
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
        TopAppBar(title = { Text(text = navigationLocations[navigationLocationIndex].toolbarTitle) }, navigationIcon = {
            if (navigationLocations[navigationLocationIndex].hasDrawerContent) {
                IconButton(onClick = {
                    scope.launch {
                        drawerState.open()
                    }
                }) {
                    Icon(painter = painterResource(id = R.drawable.outline_menu_24), contentDescription = "Menu")
                }
            }
        })
    }, drawerContent = {
        if (navigationLocations[navigationLocationIndex].hasDrawerContent) {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                navigationLocations[navigationLocationIndex].DrawerContent(
                    modifier = Modifier,
                    onDrawerItemClick = {
                        scope.launch {
                            drawerState.close()
                        }
                    },
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
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    Row {
        NavigationRail {
            AnimatedVisibility(visible = navigationLocations[navigationLocationIndex].hasDrawerContent) {
                IconButton(onClick = {
                    when (drawerState.currentValue) {
                        DrawerValue.Open -> scope.launch { drawerState.close() }
                        DrawerValue.Closed -> scope.launch { drawerState.open() }
                    }
                }) {
                    Icon(painter = painterResource(id = R.drawable.outline_menu_24), contentDescription = "Menu")
                }
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

        if (navigationLocations[navigationLocationIndex].hasDrawerContent) {
            MyDismissibleNavigationDrawer(drawerContent = {
                DismissibleDrawerSheet {
                    Spacer(Modifier.height(12.dp))
                    navigationLocations[navigationLocationIndex].DrawerContent(
                        modifier = Modifier,
                        onDrawerItemClick = {
                            scope.launch {
                                drawerState.close()
                            }
                        },
                    )
                }
            }, drawerState = drawerState) {
                navigationLocations[navigationLocationIndex].Content(modifier = Modifier)
            }
        } else {
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
