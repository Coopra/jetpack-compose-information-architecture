package com.example.alexarchitecture.contributions

import androidx.annotation.DrawableRes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alexarchitecture.R
import com.example.alexarchitecture.email.EmailPane
import com.example.alexarchitecture.email.EmailViewModel

sealed class ScreenContribution(
    val route: String,
    val title: String,
    @DrawableRes
    val icon: Int,
    val content: @Composable () -> Unit,
    val drawerContribution: DrawerContribution? = null,
    val fabContribution: FABContribution? = null,
    val toolbarContribution: ToolbarContribution? = null
) {
    object Email : ScreenContribution(
        route = "email", title = "Email", icon = R.drawable.outline_email_24, content = {
            val emailViewModel: EmailViewModel = viewModel()
            val emailUiState by emailViewModel.uiState.collectAsStateWithLifecycle()
            ToolbarContribution.Email.updateTitle(emailUiState.selectedFolder?.title ?: "Email")

            EmailPane(
                onEmailSelect = { email ->
                    emailViewModel.updateSelectedEmail(email)
                }, selectedEmail = emailUiState.selectedEmail, folderEmails = emailUiState.folderEmails
            )
        }, drawerContribution = DrawerContribution.Email, fabContribution = FABContribution.Email, toolbarContribution = ToolbarContribution.Email
    )

    object Calendar : ScreenContribution("calendar", "Calendar", R.drawable.outline_calendar_month_24, {
        Text(text = "Calendar")
    }, fabContribution = FABContribution.Calendar, toolbarContribution = ToolbarContribution.Calendar)

    object Feed : ScreenContribution("feed", "Feed", R.drawable.outline_dynamic_feed_24, {
        Text(text = "Feed")
    })

    object Apps : ScreenContribution("apps", "Apps", R.drawable.outline_apps_24, {
        Text(text = "Apps")
    })
}
