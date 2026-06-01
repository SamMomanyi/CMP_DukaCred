package com.samduka.dukacred.app.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.samduka.dukacred.core.designsystem.DukaCredColors

// 1. Define the tabs with real Material Icons
enum class DashboardTab(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Filled.Home),
    Inventory("Inventory", Icons.Filled.List),
    Capture("Scan", Icons.Filled.CameraAlt), // The action button
    Profile("Profile", Icons.Filled.Person)
}

@Composable
fun DashboardShellScreen(
    onNavigateToInvoiceCapture: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(DashboardTab.Home) }

    Scaffold(
        containerColor = DukaCredColors.ForestGreen900,
        bottomBar = {
            NavigationBar(
                containerColor = DukaCredColors.ForestGreen800,
                tonalElevation = 0.dp
            ) {
                DashboardTab.values().forEach { tab ->
                    NavigationBarItem(
                        // Don't show "Scan" as the active tab, it's just a trigger
                        selected = selectedTab == tab && tab != DashboardTab.Capture,
                        onClick = {
                            if (tab == DashboardTab.Capture) {
                                // Instantly route to the AWS Bedrock scanner screen
                                onNavigateToInvoiceCapture()
                            } else {
                                // Switch the bottom bar tab normally
                                selectedTab = tab
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(text = tab.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DukaCredColors.Ochre500,
                            selectedTextColor = DukaCredColors.Ochre500,
                            unselectedIconColor = DukaCredColors.Cream300,
                            unselectedTextColor = DukaCredColors.Cream300,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(DukaCredColors.ForestGreen900)
        ) {
            when (selectedTab) {
                DashboardTab.Home -> {
                    // TODO: Drop in your MerchantHomeScreen here later!
                    StubScreen("Dashboard Content Goes Here")
                }
                DashboardTab.Inventory -> {
                    // TODO: Drop in your InventoryScreen here later!
                    StubScreen("Inventory Content Goes Here")
                }
                DashboardTab.Profile -> {
                    StubScreen("Merchant Profile & Settings")
                }
                DashboardTab.Capture -> {
                    // Left empty because this is handled by the navigation route
                }
            }
        }
    }
}

@Composable
private fun StubScreen(label: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = DukaCredColors.Cream300
        )
    }
}