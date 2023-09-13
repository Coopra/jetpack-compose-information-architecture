package com.example.alexarchitecture.interfaces

import androidx.annotation.DrawableRes

interface EmailFolder {
    val title: String
    @get:DrawableRes
    val icon: Int
}