package com.example.alexarchitecture.email

import com.example.alexarchitecture.R

class InboxFolder: EmailFolder {
    override val title: String
        get() = "Inbox"
    override val icon: Int
        get() = R.drawable.outline_inbox_24
}
