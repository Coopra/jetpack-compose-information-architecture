package com.example.alexarchitecture.email

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Email(val sender: String, val subject: String, val body: String): Parcelable
