package com.example.alexarchitecture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.alexarchitecture.ui.theme.AlexArchitectureTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            AlexArchitectureTheme {
                CompositionLocalProvider(LocalLifecycleOwner provides this@MainActivity) {
                    MainPane()
                }
            }
        }
    }
}
