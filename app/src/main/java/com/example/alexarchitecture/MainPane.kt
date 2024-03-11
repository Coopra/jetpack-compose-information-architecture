package com.example.alexarchitecture

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alexarchitecture.contributions.ScreenContribution
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainPane() {
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    val mainViewModel: MainViewModel = viewModel()
    val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val currentScreen = mainUiState.screenContributions[selectedItem]

    val navSuiteType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
    NavigationSuiteScaffold(navigationSuiteItems = {
        mainUiState.screenContributions.forEachIndexed { index, navItem ->
            item(
                icon = {
                    Icon(painter = painterResource(id = navItem.icon), contentDescription = navItem.title)
                },
                label = {
                    Text(text = navItem.title)
                },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                }
            )
        }
    }) {
        // Screen content
        val drawerState: DrawerState = currentScreen.drawerContribution?.drawerState() ?: rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        BackHandler(enabled = drawerState.isOpen) {
            scope.launch {
                drawerState.close()
            }
        }

        if (navSuiteType != NavigationSuiteType.NavigationRail) {
            ModalNavigationDrawer(
                drawerContent = {
                    if (navSuiteType != NavigationSuiteType.NavigationRail) {
                        currentScreen.drawerContribution?.let { drawerContribution ->
                            ModalDrawerSheet {
                                Spacer(Modifier.height(12.dp))
                                drawerContribution.Content()
                            }
                        }
                    }
                }, drawerState = drawerState
            ) {
                MainPaneContent(currentScreen = currentScreen, drawerState = drawerState)
            }
        } else {
            MyDismissibleNavigationDrawer(drawerContent = {
                DismissibleDrawerSheet {
                    currentScreen.drawerContribution?.let { drawerContribution ->
                        Spacer(Modifier.height(12.dp))
                        drawerContribution.Content()
                    }
                }
            },
                drawerState = drawerState) {
                MainPaneContent(currentScreen = currentScreen, drawerState = drawerState)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainPaneContent(
    currentScreen: ScreenContribution,
    drawerState: DrawerState
) {
    Scaffold(
        floatingActionButton = {
            currentScreen.fabContribution?.let { fabContribution ->
                FloatingActionButton(onClick = { /*TODO*/ }) {
                    AnimatedContent(targetState = fabContribution.icon) { targetState ->
                        Icon(painter = painterResource(id = targetState), contentDescription = fabContribution.description)
                    }
                }
            }
        },
        topBar = {
            TopAppBar(title = { Text(text = currentScreen.toolbarContribution?.title?.collectAsStateWithLifecycle()?.value ?: currentScreen.title) }, navigationIcon = {
                if (currentScreen.drawerContribution != null) {
                    val scope = rememberCoroutineScope()
                    IconButton(onClick = {
                        scope.launch {
                            if (drawerState.isClosed) {
                                drawerState.open()
                            } else {
                                drawerState.close()
                            }
                        }
                    }) {
                        Icon(painter = painterResource(id = R.drawable.outline_menu_24), contentDescription = "Menu")
                    }
                }
            }, actions = {
                currentScreen.toolbarContribution?.let { toolbarContribution ->
                    if (toolbarContribution.actions.isNotEmpty()) {
                        toolbarContribution.actions.forEach { toolbarAction ->
                            IconButton(onClick = toolbarAction.onClick) {
                                Icon(painter = painterResource(id = toolbarAction.icon), contentDescription = toolbarAction.title)
                            }
                        }
                    }
                }
            })
        }) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            currentScreen.content.invoke()
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
