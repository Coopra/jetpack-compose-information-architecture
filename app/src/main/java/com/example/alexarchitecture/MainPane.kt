package com.example.alexarchitecture

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.alexarchitecture.contributions.ScreenContribution
import kotlinx.coroutines.launch

@Composable
fun MainPane(
    windowSizeClass: WindowSizeClass,
    viewModelStoreOwner: ViewModelStoreOwner
) {
    val widthSizeClass by rememberUpdatedState(windowSizeClass.widthSizeClass)
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val mainViewModel: MainViewModel = viewModel()
    val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(currentDestination) {
        if (currentDestination != null) {
            mainViewModel.updateCurrentScreen(currentDestination)
        }
    }

    when (widthSizeClass) {
        WindowWidthSizeClass.Compact, WindowWidthSizeClass.Medium -> CompactPane(
            viewModelStoreOwner,
            mainUiState.screenContributions,
            mainUiState.currentScreen,
            navController,
            currentDestination
        ) { screenContribution ->
            navigateToScreen(navController, screenContribution)
        }

        WindowWidthSizeClass.Expanded -> ExpandedPane(
            mainUiState.screenContributions,
            mainUiState.currentScreen,
            navController,
            currentDestination
        ) { screenContribution ->
            navigateToScreen(navController, screenContribution)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompactPane(
    viewModelStoreOwner: ViewModelStoreOwner,
    screenContributions: List<ScreenContribution>,
    currentScreen: ScreenContribution,
    navController: NavHostController,
    currentDestination: NavDestination?,
    onNavigateToScreen: (ScreenContribution) -> Unit
) {
    val drawerState: DrawerState = currentScreen.drawerContribution?.drawerState() ?: rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch {
            drawerState.close()
        }
    }

    Scaffold(bottomBar = {
        NavigationBar {
            screenContributions.forEach { screen ->
                NavigationBarItem(selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true, onClick = {
                    onNavigateToScreen(screen)
                }, icon = {
                    Icon(painter = painterResource(id = screen.icon), contentDescription = screen.title)
                }, label = {
                    Text(text = screen.title)
                })
            }
        }
    }, floatingActionButton = {
        currentScreen.fabContribution?.let { fabContribution ->
            FloatingActionButton(onClick = { /*TODO*/ }) {
                AnimatedContent(targetState = fabContribution.icon) { targetState ->
                    Icon(painter = painterResource(id = targetState), contentDescription = fabContribution.description)
                }
            }
        }
    }, topBar = {
        TopAppBar(title = { Text(text = currentScreen.title) }, navigationIcon = {
            if (currentScreen.drawerContribution != null) {
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
        currentScreen.drawerContribution?.let { drawerContribution ->
            ModalDrawerSheet(modifier = Modifier.fillMaxWidth()) {
                Spacer(Modifier.height(12.dp))
                drawerContribution.Content()
            }
        }
    }, scaffoldState = rememberScaffoldState(drawerState = drawerState)) { padding ->
        NavHost(navController = navController, startDestination = ScreenContribution.Email.route, modifier = Modifier.padding(padding)) {
            screenContributions.forEach { screenContribution ->
                composable(screenContribution.route) {
                    CompositionLocalProvider(LocalViewModelStoreOwner provides viewModelStoreOwner) {
                        screenContribution.content.invoke()
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandedPane(
    screenContributions: List<ScreenContribution>,
    currentScreen: ScreenContribution,
    navController: NavHostController,
    currentDestination: NavDestination?,
    onNavigateToScreen: (ScreenContribution) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    Row {
        NavigationRail(modifier = Modifier.verticalScroll(rememberScrollState())) {
            AnimatedVisibility(visible = currentScreen.drawerContribution != null) {
                IconButton(onClick = {
                    when (drawerState.currentValue) {
                        DrawerValue.Open -> scope.launch { drawerState.close() }
                        DrawerValue.Closed -> scope.launch { drawerState.open() }
                    }
                }) {
                    Icon(painter = painterResource(id = R.drawable.outline_menu_24), contentDescription = "Menu")
                }
            }

            currentScreen.fabContribution?.let { fabContribution ->
                FloatingActionButton(onClick = { /*TODO*/ }) {
                    AnimatedContent(targetState = fabContribution.icon) { targetState ->
                        Icon(painter = painterResource(id = targetState), contentDescription = fabContribution.description)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            screenContributions.forEach { screen ->
                NavigationRailItem(selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true, onClick = {
                    onNavigateToScreen(screen)
                }, icon = { Icon(painter = painterResource(id = screen.icon), contentDescription = screen.title) }, label = {
                    Text(text = screen.title)
                })
            }
        }

        NavHost(navController = navController, startDestination = ScreenContribution.Email.route) {
            screenContributions.forEach { screenContribution ->
                composable(screenContribution.route) {
                    if (screenContribution.drawerContribution != null) {
                        MyDismissibleNavigationDrawer(drawerContent = {
                            DismissibleDrawerSheet {
                                Spacer(Modifier.height(12.dp))
                                screenContribution.drawerContribution.Content()
                            }
                        }, drawerState = drawerState) {
                            screenContribution.content.invoke()
                        }
                    } else {
                        screenContribution.content.invoke()
                    }
                }
            }
        }
    }
}

private fun navigateToScreen(
    navController: NavHostController,
    screenContribution: ScreenContribution
) {
    navController.navigate(screenContribution.route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // re-selecting the same item
        launchSingleTop = true
        // Restore state when re-selecting a previously selected item
        restoreState = true
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
