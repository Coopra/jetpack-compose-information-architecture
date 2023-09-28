package com.example.alexarchitecture.contributions

sealed class ToolbarContribution(var title: String) {
    object Email : ToolbarContribution("Email")
    object Calendar : ToolbarContribution("Calendar")
    object Feed : ToolbarContribution("Feed")
    object Apps : ToolbarContribution("Apps")
}
