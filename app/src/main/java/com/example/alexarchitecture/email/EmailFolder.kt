package com.example.alexarchitecture.email

import androidx.annotation.DrawableRes
import com.example.alexarchitecture.R

data class EmailFolder(
    val id: Int,
    val title: String,
    @DrawableRes val icon: Int = R.drawable.outline_folder_24,
    val type: EmailFolderType = EmailFolderType.GENERAL
)
