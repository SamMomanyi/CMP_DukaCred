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

//@Composable
//fun DashboardShellScreen(
//    onNavigateToInvoiceCapture: () -> Unit = {}
//) {
//    var selectedTab by remember { mutableStateOf<DashboardTab>(DashboardTab.Home) }
//
//    Scaffold(
//        containerColor = DukaCredColors.ForestGreen900,
//        bottomBar = {
//            NavigationBar(
//                containerColor = DukaCredColors.ForestGreen800,
//                tonalElevation = 0.dp
//            ) {
//                dashboardTabs.forEach { tab ->
//                    val isSelected = tab == selectedTab
//                    NavigationBarItem(
//                        selected = isSelected,
//                        onClick = {
//                            if (tab == DashboardTab.Capture) {
//                                onNavigateToInvoiceCapture()
//                            } else {
//                                selectedTab = tab
//                            }
//                        },
//                        icon = {
//                            Icon(
//                                imageVector = tab.icon,
//                                contentDescription = tab.label
//                            )
//                        },
//                        label = { Text(text = tab.label) },
//                        colors = NavigationBarItemDefaults.colors(
//                            selectedIconColor = DukaCredColors.Ochre500,
//                            selectedTextColor = DukaCredColors.Ochre500,
//                            unselectedIconColor = DukaCredColors.Cream300,
//                            unselectedTextColor = DukaCredColors.Cream300,
//                            indicatorColor = Color.Transparent
//                        )
//                    )
//                }
//            }
//        }
//    ) { innerPadding ->
//        Box(
//            modifier = Modifier
//                .padding(innerPadding)
//                .background(DukaCredColors.ForestGreen900)
//        ) {
//            when (selectedTab) {
//                DashboardTab.Home -> MerchantHomeScreen(
//                    onCaptureInvoice = onNavigateToInvoiceCapture
//                )
//                DashboardTab.Capture -> StubScreen("Capture Invoice — coming soon")
//                DashboardTab.History -> StubScreen("History — coming soon")
//                DashboardTab.Profile -> ProfileScreen()
//            }
//        }
//    }
//}


package com.samduka.dukacred.app.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * The master container for the merchant's logged-in experience.
 * Holds the Bottom Navigation Bar and swaps between the Home and Inventory tabs.
 */
@Composable
fun DashboardShellScreen(
    onNavigateToInvoiceCapture: () -> Unit
) {
    // Lazy state for now, we will tie this to a nested NavController later
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Text("🏠") }, // Lazy placeholder icons
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Text("📦") },
                    label = { Text("Inventory") }
                )
            }
        }
    ) { paddingValues ->
        // This is where we will drop in the actual DashboardScreen
        // and InventoryScreen we built earlier this week!
        Box(modifier = Modifier.padding(paddingValues)) {
            if (selectedTab == 0) {
                Text("Dashboard Content Goes Here")
            } else {
                Text("Inventory Content Goes Here")
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
