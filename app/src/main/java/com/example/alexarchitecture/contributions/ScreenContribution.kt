package com.example.alexarchitecture.contributions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.annotation.DrawableRes
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alexarchitecture.R
import com.example.alexarchitecture.email.EmailPane
import com.example.alexarchitecture.email.EmailViewModel
import com.google.accompanist.adaptive.calculateDisplayFeatures

sealed class ScreenContribution(
    val route: String,
    val title: String,
    @DrawableRes
    val icon: Int,
    val content: @Composable () -> Unit,
    val drawerContribution: DrawerContribution? = null,
    val fabContribution: FABContribution? = null
) {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    object Email : ScreenContribution(
        route = "email", title = "Email", icon = R.drawable.outline_email_24, content = {
            val activity = LocalContext.current.findActivity()
            val emailViewModel: EmailViewModel = viewModel()
            val emailUiState by emailViewModel.uiState.collectAsStateWithLifecycle()

            EmailPane(
                windowSizeClass = calculateWindowSizeClass(activity = activity),
                displayFeatures = calculateDisplayFeatures(activity = activity),
                onEmailSelect = { email ->
                    emailViewModel.updateSelectedEmail(email)
                },
                selectedEmail = emailUiState.selectedEmail,
                folderEmails = emailUiState.folderEmails
            )
        }, drawerContribution = DrawerContribution.Email, fabContribution = FABContribution.Email
    )

    object Calendar : ScreenContribution("calendar", "Calendar", R.drawable.outline_calendar_month_24, {
        Text(text = "Calendar")
    }, fabContribution = FABContribution.Calendar)

    object Feed : ScreenContribution("feed", "Feed", R.drawable.outline_dynamic_feed_24, {
        Text(text = "Feed")
    })

    object Apps : ScreenContribution("apps", "Apps", R.drawable.outline_apps_24, {
        Text(text = "Apps")
    })
}

/**
 * Find the closest Activity in a given Context.
 */
internal fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("No Activity found")
}
