package com.example.alexarchitecture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelStoreOwner
import com.example.alexarchitecture.ui.theme.AlexArchitectureTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlexArchitectureTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val windowSizeClass = calculateWindowSizeClass(activity = this)
                    val viewModelStoreOwner = compositionLocalOf<ViewModelStoreOwner> { this }

                    MainPane(
                        windowSizeClass = windowSizeClass, viewModelStoreOwner = viewModelStoreOwner.current
                    )
                }
            }
        }
    }
}
