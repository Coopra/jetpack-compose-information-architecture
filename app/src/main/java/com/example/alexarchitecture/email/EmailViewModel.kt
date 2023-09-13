package com.example.alexarchitecture.email

import androidx.lifecycle.ViewModel
import com.example.alexarchitecture.interfaces.EmailFolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EmailUiState(
    val selectedFolder: EmailFolder = InboxFolder(),
    val selectedEmail: Email? = null,
    val folderEmails: List<Email> = emptyList(),
    val emailFolders: List<EmailFolder> = emptyList(),
)

class EmailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EmailUiState())
    val uiState: StateFlow<EmailUiState> = _uiState.asStateFlow()

    init {
        val emailFolders = fetchEmailFolders()

        _uiState.update {
            it.copy(
                emailFolders = emailFolders,
                folderEmails = fetchEmailsForFolder(emailFolders.first())
            )
        }
    }

    fun updateFolderSelection(folder: EmailFolder) {
        _uiState.update {
            it.copy(
                selectedFolder = folder,
                folderEmails = fetchEmailsForFolder(folder)
            )
        }
    }

    fun updateSelectedEmail(email: Email?) {
        _uiState.update {
            it.copy(selectedEmail = email)
        }
    }

    private fun fetchEmailsForFolder(folder: EmailFolder): List<Email> {
        return when (folder) {
            is InboxFolder -> {
                listOf(
                    Email("John Doe", "Hello", "Hello, how are you?"),
                    Email("Jane Doe", "Re: Hello", "I'm good, thanks!"),
                    Email("John Doe", "Re: Re: Hello", "That's good to hear!")
                )
            }

            else -> emptyList()
        }
    }

    private fun fetchEmailFolders(): List<EmailFolder> {
        return listOf(InboxFolder(), DraftsFolder(), ArchiveFolder(), SentFolder(), DeletedFolder(), JunkFolder())
    }
}
