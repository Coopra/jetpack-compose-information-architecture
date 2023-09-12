package com.example.alexarchitecture.email

import com.example.alexarchitecture.R

class DeletedFolder: EmailFolder {
    override val title: String
        get() = "Deleted"
    override val icon: Int
        get() = R.drawable.outline_delete_24
}
