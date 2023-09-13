package com.example.alexarchitecture.email

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.layout.DisplayFeature
import com.example.alexarchitecture.DeviceSizePreviews
import com.example.alexarchitecture.ListDetail
import com.example.alexarchitecture.R
import com.example.alexarchitecture.ui.theme.AlexArchitectureTheme
import com.google.accompanist.adaptive.HorizontalTwoPaneStrategy

@Composable
fun EmailPane(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    modifier: Modifier = Modifier
) {
    // Query for the current window size class
    val widthSizeClass by rememberUpdatedState(windowSizeClass.widthSizeClass)

    val viewModel: EmailViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val showListAndDetail = when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> false
        else -> true
    }

    ListDetail(isDetailOpen = uiState.selectedEmail != null, setIsDetailOpen = {
        if (!it) {
            viewModel.updateSelectedEmail(null)
        }
    }, showListAndDetail = showListAndDetail, list = { isDetailVisible ->
        ListContent(
            emails = uiState.folderEmails, selectedEmail = uiState.selectedEmail,
            onEmailClick = { email ->
                viewModel.updateSelectedEmail(email)
            }, modifier = if (isDetailVisible) {
                Modifier.padding(end = 12.dp)
            } else {
                Modifier
            }
        )
    }, detail = { isListVisible ->
        DetailContent(
            selectedEmail = uiState.selectedEmail,
            modifier = if (isListVisible) {
                Modifier.padding(start = 12.dp)
            } else {
                Modifier
            }
        )
    }, twoPaneStrategy = HorizontalTwoPaneStrategy(
        splitFraction = 1f / 3f,
    ), displayFeatures = displayFeatures, modifier = modifier.padding(
        horizontal = when(showListAndDetail) {
            true -> 24.dp
            false -> 16.dp
        }
    )
    )
}

/**
 * The content for the list pane.
 */
@Composable
private fun ListContent(
    emails: List<Email>,
    selectedEmail: Email?,
    onEmailClick: (email: Email) -> Unit,
    modifier: Modifier = Modifier
) {
    if (emails.isEmpty()) {
        Text(
            text = "Empty folder",
            modifier = modifier
        )
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .then(
                when (selectedEmail) {
                    null -> Modifier
                    else -> Modifier.selectableGroup()
                }
            )
    ) {
        items(emails) { email ->
            val interactionSource = remember { MutableInteractionSource() }

            val interactionModifier = when (selectedEmail) {
                null -> {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = rememberRipple(),
                        onClick = { onEmailClick(email) }
                    )
                }
                else -> {
                    Modifier.selectable(
                        selected = email == selectedEmail,
                        interactionSource = interactionSource,
                        indication = rememberRipple(),
                        onClick = { onEmailClick(email) }
                    )
                }
            }

            val containerColor = when (selectedEmail) {
                null -> MaterialTheme.colorScheme.surface
                else -> if (email == selectedEmail) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.surface
                }
            }
            val borderStroke = when (selectedEmail) {
                null -> BorderStroke(
                    1.dp, MaterialTheme.colorScheme.outline
                )
                else -> if (email == selectedEmail) {
                    null
                } else {
                    BorderStroke(
                        1.dp, MaterialTheme.colorScheme.outline
                    )
                }
            }

            // TODO: Card selection overfills the Card
            Card(
                colors = CardDefaults.cardColors(containerColor = containerColor),
                border = borderStroke,
                modifier = Modifier
                    .then(interactionModifier)
                    .fillMaxWidth()
            ) {
                Text(
                    text = email.subject,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}

/**
 * The content for the detail pane.
 */
@Composable
private fun DetailContent(
    selectedEmail: Email?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp)
    ) {
        if (selectedEmail != null) {
            Text(
                text = selectedEmail.subject,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = selectedEmail.body
            )
        } else {
            Text(
                text = stringResource(R.string.placeholder)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
@DeviceSizePreviews
fun EmailPanePreview() {
    AlexArchitectureTheme {
        val configuration = LocalConfiguration.current
        EmailPane(WindowSizeClass.calculateFromSize(DpSize(configuration.screenWidthDp.dp, configuration.screenHeightDp.dp)), listOf())
    }
}
