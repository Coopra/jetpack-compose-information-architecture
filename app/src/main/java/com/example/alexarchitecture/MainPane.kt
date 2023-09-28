package com.example.alexarchitecture

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.layout.DisplayFeature
import com.example.alexarchitecture.email.EmailPane
import com.example.alexarchitecture.email.EmailViewModel
import com.example.alexarchitecture.interfaces.NavigationLocation
import kotlinx.coroutines.launch

@Composable
fun MainPane(
    windowSizeClass: WindowSizeClass,
    navigationLocations: List<NavigationLocation>,
    displayFeatures: List<DisplayFeature>,
    viewModelStoreOwner: ViewModelStoreOwner
) {
    assert(navigationLocations.isNotEmpty())
    val widthSizeClass by rememberUpdatedState(windowSizeClass.widthSizeClass)

    when (widthSizeClass) {
        WindowWidthSizeClass.Compact, WindowWidthSizeClass.Medium -> CompactPane(windowSizeClass, displayFeatures, viewModelStoreOwner)
        WindowWidthSizeClass.Expanded -> ExpandedPane(navigationLocations)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompactPane(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    viewModelStoreOwner: ViewModelStoreOwner
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val mainViewModel: MainViewModel = viewModel()
    val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val currentDestination = navBackStackEntry?.destination
    LaunchedEffect(currentDestination) {
        if (currentDestination != null) {
            mainViewModel.updateCurrentScreen(currentDestination)
        }
    }

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch {
            drawerState.close()
        }
    }

    Scaffold(bottomBar = {
        NavigationBar {
            mainUiState.screenContributions.forEach { screen ->
                NavigationBarItem(selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true, onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }, icon = {
                    Icon(painter = painterResource(id = screen.icon), contentDescription = screen.title)
                }, label = {
                    Text(text = screen.title)
                })
            }
        }
    }, floatingActionButton = {
        FloatingActionButton(onClick = { /*TODO*/ }) {
            Icon(painter = painterResource(id = R.drawable.outline_edit_24), contentDescription = "Compose")
        }
    }, topBar = {
        TopAppBar(title = { Text(text = mainUiState.currentScreen.title) }, navigationIcon = {
            if (mainUiState.currentScreen.drawerContribution != null) {
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
        mainUiState.currentScreen.drawerContribution?.let { drawerContribution ->
            ModalDrawerSheet(modifier = Modifier.fillMaxWidth()) {
                Spacer(Modifier.height(12.dp))
                drawerContribution.drawerContent.invoke()
            }
        }
    }, scaffoldState = rememberScaffoldState(drawerState = drawerState)) { padding ->
        NavHost(navController = navController, startDestination = ScreenContribution.Email.route, modifier = Modifier.padding(padding)) {
            composable(ScreenContribution.Email.route) {
                CompositionLocalProvider(LocalViewModelStoreOwner provides viewModelStoreOwner) {
                    val emailViewModel: EmailViewModel = viewModel()
                    val emailUiState by emailViewModel.uiState.collectAsStateWithLifecycle()
                    EmailPane(
                        windowSizeClass = windowSizeClass, displayFeatures = displayFeatures, onEmailSelect = { email ->
                            emailViewModel.updateSelectedEmail(email)
                        }, selectedEmail = emailUiState.selectedEmail, folderEmails = emailUiState.folderEmails
                    )
                    LaunchedEffect(emailUiState.selectedFolder) {
                        drawerState.close()
                    }
                }
            }
            composable(ScreenContribution.Calendar.route) { Text(text = "Calendar") }
            composable(ScreenContribution.Feed.route) { Text(text = "Feed") }
            composable(ScreenContribution.Apps.route) { Text(text = "Apps") }
        }
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
                        modifier = Modifier
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

// @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
// @DeviceSizePreviews
// @Composable
// fun MainPanePreview() {
//     AlexArchitectureTheme {
//         val configuration = LocalConfiguration.current
//         MainPane(WindowSizeClass.calculateFromSize(DpSize(configuration.screenWidthDp.dp, configuration.screenHeightDp.dp)), listOf(
//             EmailNavigationLocation(WindowSizeClass.calculateFromSize(DpSize(configuration.screenWidthDp.dp, configuration.screenHeightDp.dp)), listOf()),
//             CalendarNavigationLocation(),
//             FeedNavigationLocation(),
//             AppsNavigationLocation()
//         ))
//     }
// }

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
