package com.samduka.dukacred.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.samduka.dukacred.core.designsystem.DukaCredColors
import com.samduka.dukacred.feature.merchanthome.presentation.ui.MerchantHomeScreen

@Composable
fun DashboardShellScreen() {
    var selectedTab by remember { mutableStateOf<DashboardTab>(DashboardTab.Home) }

    Scaffold(
        containerColor = DukaCredColors.ForestGreen900,
        bottomBar = {
            NavigationBar(
                containerColor = DukaCredColors.ForestGreen800,
                tonalElevation = 0.dp
            ) {
                dashboardTabs.forEach { tab ->
                    val isSelected = tab == selectedTab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
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
                .background(DukaCredColors.ForestGreen900)
        ) {
            when (selectedTab) {
                DashboardTab.Home -> MerchantHomeScreen()
                DashboardTab.Capture -> StubScreen("Capture Invoice — coming soon")
                DashboardTab.History -> StubScreen("History — coming soon")
                DashboardTab.Profile -> StubScreen("Profile — coming soon")
            }
        }
    }
}

@Composable
private fun StubScreen(label: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DukaCredColors.ForestGreen900),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = DukaCredColors.Cream300
        )
    }
}