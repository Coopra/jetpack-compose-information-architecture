package com.example.alexarchitecture.contributions

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alexarchitecture.email.EmailDrawer
import com.example.alexarchitecture.email.EmailViewModel
import kotlinx.coroutines.launch

sealed class DrawerContribution {
    @Composable
    abstract fun Content()

    @Composable
    abstract fun drawerState(): DrawerState

    abstract suspend fun openDrawer()

    abstract suspend fun closeDrawer()

    abstract fun isDrawerOpen(): Boolean

    object Email : DrawerContribution() {
        private var drawerState: DrawerState? = null

        @Composable
        override fun Content() {
            val viewModel: EmailViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val scope = rememberCoroutineScope()

            EmailDrawer(emailFolders = uiState.emailFolders, selectedFolder = uiState.selectedFolder, onFolderSelected = { folder ->
                viewModel.updateFolderSelection(folder)
                scope.launch {
                    drawerState?.close()
                }
            })
        }

        @Composable
        override fun drawerState(): DrawerState {
            if (drawerState == null) {
                drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            }

            return drawerState!!
        }

        override suspend fun openDrawer() {
            drawerState?.open()
        }

        override suspend fun closeDrawer() {
            drawerState?.close()
        }

        override fun isDrawerOpen(): Boolean {
            return drawerState?.isOpen ?: false
        }
    }
}
