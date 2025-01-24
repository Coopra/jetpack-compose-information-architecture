package com.example.alexarchitecture.email

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.alexarchitecture.DeviceSizePreviews
import com.example.alexarchitecture.HamburgerButton
import com.example.alexarchitecture.R
import com.example.alexarchitecture.ui.theme.AlexArchitectureTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun EmailPane(
    onEmailSelect: (Email?) -> Unit,
    onDrawerButtonClick: () -> Unit,
    toolbarTitle: String,
    modifier: Modifier = Modifier,
    selectedEmail: Email? = null,
    folderEmails: List<Email> = emptyList()
) {
    val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>()
    val scope = rememberCoroutineScope()
    BackHandler(navigator.canNavigateBack()) {
        scope.launch {
            navigator.navigateBack()
            onEmailSelect(null)
        }
    }

    Surface {
        ListDetailPaneScaffold(directive = navigator.scaffoldDirective, value = navigator.scaffoldValue, listPane = {
            AnimatedPane {
                val navSuiteType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
                ListContent(
                    emails = folderEmails,
                    selectedEmail = selectedEmail,
                    onEmailClick = { email ->
                        scope.launch {
                            onEmailSelect(email)
                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                        }
                    },
                    showDrawerIcon = navSuiteType == NavigationSuiteType.NavigationBar,
                    onDrawerButtonClick = onDrawerButtonClick,
                    toolbarTitle = toolbarTitle
                )
            }
        }, detailPane = {
            AnimatedPane {
                DetailContent(
                    selectedEmail = selectedEmail,
                    onBackButtonClick = {
                        scope.launch {
                            navigator.navigateBack()
                            onEmailSelect(null)
                        }
                    }
                )
            }
        }, modifier = modifier, paneExpansionState = rememberPaneExpansionState(
            keyProvider = navigator.scaffoldValue
        ), paneExpansionDragHandle = { state ->
            val interactionSource = remember { MutableInteractionSource() }
            VerticalDragHandle(
                modifier = Modifier.paneExpansionDraggable(
                    state, LocalMinimumInteractiveComponentSize.current, interactionSource
                )
            )
        })
    }
}

@Composable
fun EmailDrawer(
    emailFolders: List<EmailFolder>,
    selectedFolder: EmailFolder?,
    onFolderSelected: (EmailFolder) -> Unit
) {
    emailFolders.forEach { emailFolder ->
        NavigationDrawerItem(
            label = { Text(text = emailFolder.title) },
            selected = emailFolder.id == selectedFolder?.id,
            onClick = {
                onFolderSelected(emailFolder)
            },
            icon = { Icon(painter = painterResource(id = emailFolder.icon), contentDescription = null) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

/**
 * The content for the list pane.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListContent(
    emails: List<Email>,
    selectedEmail: Email?,
    onEmailClick: (email: Email) -> Unit,
    showDrawerIcon: Boolean,
    onDrawerButtonClick: () -> Unit,
    toolbarTitle: String,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = toolbarTitle) },
                navigationIcon = {
                    if (showDrawerIcon) {
                        HamburgerButton {
                            onDrawerButtonClick()
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(painter = painterResource(id = R.drawable.outline_notifications_24), contentDescription = "Notifications")
                    }

                    IconButton(onClick = {}) {
                        Icon(painter = painterResource(id = R.drawable.outline_search_24), contentDescription = "Search")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(painter = painterResource(id = R.drawable.outline_edit_24), contentDescription = "New email")
            }
        }
    ) { innerPadding ->
        if (emails.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Empty folder"
                )
            }
            return@Scaffold
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier
                .padding(innerPadding)
                .then(
                    when (selectedEmail) {
                        null -> Modifier
                        else -> Modifier.selectableGroup()
                    }
                )
        ) {
            items(emails) { email ->
                val interactionModifier = when (selectedEmail) {
                    null -> {
                        Modifier.clickable(onClick = { onEmailClick(email) })
                    }

                    else -> {
                        Modifier.selectable(selected = email == selectedEmail, onClick = { onEmailClick(email) })
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
                    modifier = Modifier.fillMaxWidth().clip(CardDefaults.shape).then(interactionModifier)
                ) {
                    Text(
                        text = email.subject, modifier = Modifier.fillMaxWidth().padding(8.dp)
                    )
                }
            }
        }
    }
}

/**
 * The content for the detail pane.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailContent(
    selectedEmail: Email?,
    onBackButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        if (selectedEmail != null) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {},
                        navigationIcon = {
                            IconButton(onClick = onBackButtonClick) {
                                Icon(painter = painterResource(id = R.drawable.outline_arrow_back_24), contentDescription = "Back")
                            }
                        },
                        actions = {
                            IconButton(onClick = {}) {
                                Icon(painter = painterResource(id = R.drawable.outline_delete_24), contentDescription = "Delete")
                            }
                            IconButton(onClick = {}) {
                                Icon(painter = painterResource(id = R.drawable.outline_archive_24), contentDescription = "Archive")
                            }
                        }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = selectedEmail.subject, style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = selectedEmail.body
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.placeholder)
                )
            }
        }

    }
}

@Composable
@DeviceSizePreviews
fun EmailPanePreview() {
    AlexArchitectureTheme {
        EmailPane(onEmailSelect = {}, onDrawerButtonClick = {}, toolbarTitle = "Inbox")
    }
}
