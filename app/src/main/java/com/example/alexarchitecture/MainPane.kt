package com.example.alexarchitecture

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alexarchitecture.contributions.ScreenContribution
import com.example.alexarchitecture.ui.theme.AlexArchitectureTheme
import com.example.alexarchitecture.ui.theme.RainbowDarkBlue
import com.example.alexarchitecture.ui.theme.RainbowGreen
import com.example.alexarchitecture.ui.theme.RainbowLightBlue
import com.example.alexarchitecture.ui.theme.RainbowOrange
import com.example.alexarchitecture.ui.theme.RainbowPurple
import com.example.alexarchitecture.ui.theme.RainbowRed
import com.example.alexarchitecture.ui.theme.RainbowYellow
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.launch

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
            val hazeState = remember { HazeState()}

            ModalNavigationDrawer(
                drawerContent = {
                    if (navSuiteType != NavigationSuiteType.NavigationRail) {
                        currentScreen.drawerContribution?.let { drawerContribution ->
                            ModalDrawerSheet(
                                modifier = Modifier.hazeChild(
                                    state = hazeState,
                                    shape = RoundedCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 0.dp)
                                ),
                                drawerContainerColor = Color.Transparent
                            ) {
                                Spacer(Modifier.height(12.dp))
                                drawerContribution.Content()
                            }
                        }
                    }
                },
                drawerState = drawerState,
                scrimColor = Color.Transparent
            ) {
                MainPaneContent(
                    modifier = Modifier.haze(hazeState),
                    currentScreen = currentScreen,
                    drawerState = drawerState
                )
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
    drawerState: DrawerState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
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
            val infiniteTransition = rememberInfiniteTransition()

            val offset by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 10000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )

            val brush = remember(offset) {
                object : ShaderBrush() {
                    override fun createShader(size: Size): Shader {
                        val widthOffset = size.width * offset
                        val heightOffset = size.height * offset
                        return LinearGradientShader(
                            colors = listOf(RainbowRed, RainbowPurple, RainbowDarkBlue, RainbowLightBlue, RainbowGreen, RainbowYellow, RainbowOrange, RainbowRed),
                            from = Offset(widthOffset, heightOffset),
                            to = Offset(widthOffset + size.width, heightOffset + size.height),
                            tileMode = TileMode.Repeated
                        )
                    }
                }
            }

            var toolbarWidth by remember { mutableIntStateOf(0) }
            var gradientBottom by remember { mutableFloatStateOf(1f) }
            val topBound by remember {
                mutableFloatStateOf(3 * (gradientBottom / 4))
            }
            val surfaceColor = MaterialTheme.colorScheme.surface

            TopAppBar(
                title = { Text(text = currentScreen.toolbarContribution?.title?.collectAsStateWithLifecycle()?.value ?: currentScreen.title) },
                modifier = Modifier
                    .drawWithCache {
                        onDrawBehind {
                            // Draw rainbow gradient
                            drawRect(brush = brush)

                            // Draw opacity mask #1 (radial) - 60% opacity in center (80% in dark mode) to 0% opacity at edge
                            drawRect(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0x66FFFFFF), Color(0x00FFFFFF)),
                                    center = Offset(toolbarWidth / 2f, gradientBottom / 2f),
                                    radius = gradientBottom
                                )
                            )

                            // Draw opacity mask #2 (linear) on bottom (toolbar) area - 100% to 0% opacity
                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color.Transparent, surfaceColor),
                                    start = Offset(0f, topBound),
                                    end = Offset(0f, topBound + 92.dp.toPx())
                                )
                            )
                        }
                    }
                    .onGloballyPositioned {
                        toolbarWidth = it.size.width
                        gradientBottom = toolbarWidth * 1.15f
                    },
                navigationIcon = {
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
                },
                actions = {
                    currentScreen.toolbarContribution?.let { toolbarContribution ->
                        if (toolbarContribution.actions.isNotEmpty()) {
                            toolbarContribution.actions.forEach { toolbarAction ->
                                IconButton(onClick = toolbarAction.onClick) {
                                    Icon(painter = painterResource(id = toolbarAction.icon), contentDescription = toolbarAction.title)
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            currentScreen.content.invoke()
        }
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
