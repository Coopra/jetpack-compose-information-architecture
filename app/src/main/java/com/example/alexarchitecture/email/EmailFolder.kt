package com.example.alexarchitecture.email

import androidx.annotation.DrawableRes

interface EmailFolder {
    val title: String
    @get:DrawableRes
    val icon: Int
}