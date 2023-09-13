package com.example.alexarchitecture.email

import androidx.lifecycle.ViewModel
import com.example.alexarchitecture.interfaces.EmailFolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EmailUiState(
    val selectedFolder: EmailFolder = EmailFolder.Inbox
)

class EmailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EmailUiState())
    val uiState: StateFlow<EmailUiState> = _uiState.asStateFlow()

    fun updateFolderSelection(folder: EmailFolder) {
        _uiState.update {
            it.copy(selectedFolder = folder)
        }
    }
}
