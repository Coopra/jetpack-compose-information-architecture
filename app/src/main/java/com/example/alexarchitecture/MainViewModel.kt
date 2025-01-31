package com.example.alexarchitecture

import androidx.lifecycle.ViewModel
import com.example.alexarchitecture.contributions.ScreenContribution
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MainUiState(
    val screenContributions: List<ScreenContribution> = emptyList(),
    val currentScreen: ScreenContribution = ScreenContribution.Email
)

class MainViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        fetchScreenContributions()
    }

    private fun fetchScreenContributions() {
        val screenContributions = listOf(ScreenContribution.Email, ScreenContribution.Calendar, ScreenContribution.Feed, ScreenContribution.Apps)
        _uiState.update {
            it.copy(
                screenContributions = screenContributions,
                currentScreen = screenContributions.first()
            )
        }
    }
}
