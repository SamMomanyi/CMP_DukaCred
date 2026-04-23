package com.samduka.dukacred.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.ui.graphics.vector.ImageVector

sealed class DashboardTab(
    val label: String,
    val icon: ImageVector
) {
    data object Home : DashboardTab("Home", Icons.Rounded.Home)
    data object Capture : DashboardTab("Capture", Icons.Rounded.ReceiptLong)
    data object History : DashboardTab("History", Icons.Rounded.History)
    data object Profile : DashboardTab("Profile", Icons.Rounded.AccountCircle)
}

val dashboardTabs = listOf(
    DashboardTab.Home,
    DashboardTab.Capture,
    DashboardTab.History,
    DashboardTab.Profile
)