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
import com.example.alexarchitecture.apps.AppsNavigationLocation
import com.example.alexarchitecture.calendar.CalendarNavigationLocation
import com.example.alexarchitecture.email.EmailNavigationLocation
import com.example.alexarchitecture.feed.FeedNavigationLocation
import com.example.alexarchitecture.ui.theme.AlexArchitectureTheme
import com.google.accompanist.adaptive.calculateDisplayFeatures

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlexArchitectureTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val windowSizeClass = calculateWindowSizeClass(activity = this)
                    val displayFeatures = calculateDisplayFeatures(activity = this)
                    val navigationLocations = listOf(EmailNavigationLocation(windowSizeClass, displayFeatures), CalendarNavigationLocation(), FeedNavigationLocation(), AppsNavigationLocation())
                    val viewModelStoreOwner = compositionLocalOf<ViewModelStoreOwner> { this }

                    MainPane(
                        windowSizeClass = windowSizeClass,
                        navigationLocations = navigationLocations,
                        displayFeatures = displayFeatures,
                        viewModelStoreOwner = viewModelStoreOwner.current
                    )
                }
            }
        }
    }
}
