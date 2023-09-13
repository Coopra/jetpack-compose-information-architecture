package com.example.alexarchitecture.email

import com.example.alexarchitecture.R
import com.example.alexarchitecture.interfaces.EmailFolder

class SentFolder: EmailFolder {
    override val title: String
        get() = "Sent"
    override val icon: Int
        get() = R.drawable.outline_send_24
}
