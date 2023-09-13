package com.example.alexarchitecture

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.window.layout.DisplayFeature
import com.google.accompanist.adaptive.FoldAwareConfiguration
import com.google.accompanist.adaptive.TwoPane
import com.google.accompanist.adaptive.TwoPaneStrategy

/**
 * A higher-order component displaying an opinionated list-detail format.
 *
 * The [list] slot is the primary content, and is in a parent relationship with the content
 * displayed in [detail].
 *
 * When there is enough space to display both list and detail, pass `true` to [showListAndDetail]
 * to show both the list and the detail at the same time. This content is displayed in a [TwoPane]
 * with the given [twoPaneStrategy].
 *
 * When there is not enough space to display both list and detail, which slot is displayed is based
 * on [isDetailOpen]. Internally, this state is changed in an opinionated way via [setIsDetailOpen].
 * For instance, when showing just the detail screen, a back button press will call
 * [setIsDetailOpen] passing `false`.
 */
@Composable
fun ListDetail(
    isDetailOpen: Boolean,
    setIsDetailOpen: (Boolean) -> Unit,
    showListAndDetail: Boolean,
    list: @Composable (isDetailVisible: Boolean) -> Unit,
    detail: @Composable (isListVisible: Boolean) -> Unit,
    twoPaneStrategy: TwoPaneStrategy,
    displayFeatures: List<DisplayFeature>,
    modifier: Modifier = Modifier,
) {
    val currentIsDetailOpen by rememberUpdatedState(isDetailOpen)
    val currentShowListAndDetail by rememberUpdatedState(showListAndDetail)

    // Determine whether to show the list and/or the detail.
    // This is a function of current app state, and the width size class.
    val showList by remember {
        derivedStateOf {
            currentShowListAndDetail || !currentIsDetailOpen
        }
    }
    val showDetail by remember {
        derivedStateOf {
            currentShowListAndDetail || currentIsDetailOpen
        }
    }
    // Validity check: we should always be showing something
    check(showList || showDetail)

    val start = remember {
        movableContentOf {
            Box {
                list(showDetail)
            }
        }
    }

    val end = remember {
        movableContentOf {
            Box {
                detail(showList)
            }

            // Allow a back press to hide whatever is shown in the detail pane.
            BackHandler(currentIsDetailOpen) {
                setIsDetailOpen(false)
            }
        }
    }

    Box(modifier = modifier) {
        if (showList && showDetail) {
            TwoPane(
                first = {
                    start()
                },
                second = {
                    end()
                },
                strategy = twoPaneStrategy,
                displayFeatures = displayFeatures,
                foldAwareConfiguration = FoldAwareConfiguration.VerticalFoldsOnly,
                modifier = Modifier.fillMaxSize(),
            )
        } else if (showList) {
            start()
        } else {
            end()
        }
    }
}
