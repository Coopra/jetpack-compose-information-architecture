package com.example.alexarchitecture.interfaces

import androidx.annotation.DrawableRes
import com.example.alexarchitecture.R

enum class EmailFolder (
    val title: String,
    @get:DrawableRes
    val icon: Int
) {
    Inbox("Inbox", R.drawable.outline_inbox_24),
    Drafts("Drafts", R.drawable.outline_edit_note_24),
    Archive("Archive", R.drawable.outline_archive_24),
    Sent("Sent", R.drawable.outline_send_24),
    Deleted("Deleted", R.drawable.outline_delete_24),
    Junk("Junk", R.drawable.outline_block_24)
}
