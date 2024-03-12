package com.example.alexarchitecture.email

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.alexarchitecture.DeviceSizePreviews
import com.example.alexarchitecture.R
import com.example.alexarchitecture.ui.theme.AlexArchitectureTheme

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun EmailPane(
    onEmailSelect: (Email?) -> Unit,
    modifier: Modifier = Modifier,
    selectedEmail: Email? = null,
    folderEmails: List<Email> = emptyList()
) {
    val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>()
    BackHandler(navigator.canNavigateBack()) {
        navigator.navigateBack()
        onEmailSelect(null)
    }

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane(modifier = Modifier) {
                ListContent(
                    emails = folderEmails, selectedEmail = selectedEmail,
                    onEmailClick = { email ->
                        onEmailSelect(email)
                        navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                    }
                )
            }
        },
        modifier = modifier,
    ) {
        AnimatedPane(modifier = Modifier) {
            DetailContent(
                selectedEmail = selectedEmail
            )
        }
    }
}

@Composable
fun EmailDrawer(
    emailFolders: List<EmailFolder>,
    selectedFolder: EmailFolder?,
    onFolderSelected: (EmailFolder) -> Unit
) {
    emailFolders.forEach { emailFolder ->
        val isSelected = emailFolder.id == selectedFolder?.id
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(NavigationDrawerItemDefaults.ItemPadding)
                .clickable { onFolderSelected(emailFolder) },
            shape = RoundedCornerShape(3.dp),
            color = when (isSelected) {
                true -> Color.Black.copy(alpha = 0.0373f)
                false -> Color.Transparent

            }
        ) {
            Row(modifier = Modifier
                .height(IntrinsicSize.Min)
                .padding(start = 0.dp, top = 12.dp, end = 16.dp, bottom = 12.dp)
            ) {
                if (isSelected) {
                    Box(modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(99)))
                    Spacer(modifier = Modifier.width(11.dp))
                } else {
                    Spacer(modifier = Modifier.width(16.dp))
                }

                Icon(painter = painterResource(id = emailFolder.icon), contentDescription = null)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = emailFolder.title)
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
    }
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

@Composable
@DeviceSizePreviews
fun EmailPanePreview() {
    AlexArchitectureTheme {
        EmailPane(onEmailSelect = {})
    }
}
