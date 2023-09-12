package com.example.alexarchitecture.email

import com.example.alexarchitecture.R

class ArchiveFolder: EmailFolder {
    override val title: String
        get() = "Archive"
    override val icon: Int
        get() = R.drawable.outline_archive_24
}
