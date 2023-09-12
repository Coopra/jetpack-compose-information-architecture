package com.example.alexarchitecture.email

import com.example.alexarchitecture.R

class JunkFolder: EmailFolder {
    override val title: String
        get() = "Junk"
    override val icon: Int
        get() = R.drawable.outline_block_24
}
