package com.example.alexarchitecture

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuite
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
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
import com.example.alexarchitecture.ui.theme.AlexArchitectureTheme
import kotlinx.coroutines.launch

@Composable
fun MainPane() {
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    val mainViewModel: MainViewModel = viewModel()
    val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val currentScreen = mainUiState.screenContributions[selectedItem]

    val navSuiteType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
    val drawerState: DrawerState = currentScreen.drawerContribution?.drawerState() ?: rememberDrawerState(DrawerValue.Closed)
    NavigationSuiteScaffoldLayout(
        navigationSuite = {
            if (navSuiteType == NavigationSuiteType.NavigationRail) {
                NavigationRail {
                    AnimatedVisibility(currentScreen.drawerContribution != null) {
                        val scope = rememberCoroutineScope()
                        HamburgerButton {
                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    mainUiState.screenContributions.forEachIndexed { index, navItem ->
                        NavigationRailItem(
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

                    Spacer(Modifier.weight(1f))
                }
            } else {
                NavigationSuite {
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
                }
            }
        }
    ) {
        // Screen content
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
                },
                drawerState = drawerState
            ) {
                currentScreen.content.invoke()
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
                currentScreen.content.invoke()
            }
        }
    }
}

@Composable
fun HamburgerButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(painter = painterResource(id = R.drawable.outline_menu_24), contentDescription = "Menu")
    }
}

@DeviceSizePreviews
@Composable
fun MainPanePreview() {
    AlexArchitectureTheme {
        MainPane()
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
