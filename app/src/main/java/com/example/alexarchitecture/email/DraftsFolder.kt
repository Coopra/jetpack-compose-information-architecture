package com.example.alexarchitecture.email

import com.example.alexarchitecture.R

class DraftsFolder: EmailFolder {
    override val title: String
        get() = "Drafts"
    override val icon: Int
        get() = R.drawable.outline_edit_note_24
}
