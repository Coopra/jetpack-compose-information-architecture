package com.example.alexarchitecture.contributions

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alexarchitecture.R
import com.example.alexarchitecture.calendar.CalendarPane
import com.example.alexarchitecture.email.EmailPane
import com.example.alexarchitecture.email.EmailViewModel
import kotlinx.coroutines.launch

sealed class ScreenContribution(
    val title: String,
    @DrawableRes
    val icon: Int,
    val content: @Composable () -> Unit,
    val drawerContribution: DrawerContribution? = null
) {
    object Email : ScreenContribution(
        title = "Email", icon = R.drawable.outline_email_24, content = {
            val emailViewModel: EmailViewModel = viewModel()
            val emailUiState by emailViewModel.uiState.collectAsStateWithLifecycle()
            val scope = rememberCoroutineScope()

            EmailPane(
                onEmailSelect = { email ->
                    emailViewModel.updateSelectedEmail(email)
                },
                selectedEmail = emailUiState.selectedEmail,
                folderEmails = emailUiState.folderEmails,
                onDrawerButtonClick = {
                    scope.launch {
                        if (DrawerContribution.Email.isDrawerOpen()) {
                            DrawerContribution.Email.closeDrawer()
                        } else {
                            DrawerContribution.Email.openDrawer()
                        }
                    }
                },
                toolbarTitle = emailUiState.selectedFolder?.title ?: "Email"
            )
        }, drawerContribution = DrawerContribution.Email
    )

    object Calendar : ScreenContribution("Calendar", R.drawable.outline_calendar_month_24, {
        CalendarPane()
    })

    object Feed : ScreenContribution("Feed", R.drawable.outline_dynamic_feed_24, {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Feed")
        }
    })

    object Apps : ScreenContribution("Apps", R.drawable.outline_apps_24, {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Apps")
        }
    })
}
