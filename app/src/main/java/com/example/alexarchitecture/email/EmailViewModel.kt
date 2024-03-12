package com.example.alexarchitecture.email

import androidx.lifecycle.ViewModel
import com.example.alexarchitecture.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EmailUiState(
    val selectedFolder: EmailFolder? = null,
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
                selectedFolder = emailFolders.first(),
                folderEmails = fetchEmailsForFolder(emailFolders.first().id)
            )
        }
    }

    fun updateFolderSelection(folder: EmailFolder) {
        _uiState.update {
            it.copy(
                selectedFolder = folder,
                folderEmails = fetchEmailsForFolder(folder.id)
            )
        }
    }

    fun updateSelectedEmail(email: Email?) {
        _uiState.update {
            it.copy(selectedEmail = email)
        }
    }

    private fun fetchEmailsForFolder(id: Int): List<Email> {
        return when (id) {
            INBOX_ID -> {
                List(20) { index ->
                    Email(
                        sender = "Sender$index",
                        subject = "Subject $index",
                        body = "This is the body of email $index."
                    )
                }
            }

            else -> emptyList()
        }
    }

    private fun fetchEmailFolders(): List<EmailFolder> {
        return listOf(
            EmailFolder(id = INBOX_ID, title = "Inbox", icon = R.drawable.outline_inbox_24, type = EmailFolderType.INBOX),
            EmailFolder(id = 1, title = "Drafts", icon = R.drawable.outline_edit_note_24, type = EmailFolderType.DRAFTS),
            EmailFolder(id = 2, title = "Archive", icon = R.drawable.outline_archive_24, type = EmailFolderType.ARCHIVE),
            EmailFolder(id = 3, title = "Sent", icon = R.drawable.outline_send_24, type = EmailFolderType.SENT),
            EmailFolder(id = 4, title = "Deleted", icon = R.drawable.outline_delete_24, type = EmailFolderType.DELETED),
            EmailFolder(id = 5, title = "Junk", icon = R.drawable.outline_block_24, type = EmailFolderType.JUNK),
        )
    }

    companion object {
        private const val INBOX_ID = 0
    }
}
