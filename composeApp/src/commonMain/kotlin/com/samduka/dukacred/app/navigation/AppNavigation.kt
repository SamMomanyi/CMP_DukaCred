package com.samduka.dukacred.app.navigation

import AppRoute
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.samduka.dukacred.core.designsystem.DukaCredColors
import com.samduka.dukacred.feature.auth.presentation.ui.AdminSignInScreen
import com.samduka.dukacred.feature.auth.presentation.ui.MerchantSignInScreen
import com.samduka.dukacred.feature.auth.presentation.ui.RolePickerScreen
import com.samduka.dukacred.feature.auth.presentation.ui.SignUpScreen
import com.samduka.dukacred.feature.merchanthome.presentation.ui.MerchantHomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoute.AuthGraph
    ) {

        // ── AUTH GRAPH ───────────────────────────────────────────────
        navigation<AppRoute.AuthGraph>(startDestination = AppRoute.RolePicker) {

            composable<AppRoute.RolePicker> {
                RolePickerScreen(
                    onNavigateToMerchantSignIn = {
                        navController.navigate(AppRoute.MerchantSignIn)
                    },
                    onNavigateToAdminSignIn = {
                        navController.navigate(AppRoute.AdminSignIn)
                    },
                    onNavigateToMerchantHome = {
                        navController.navigate(AppRoute.MainGraph) {
                            popUpTo(AppRoute.AuthGraph) { inclusive = true }
                        }
                    },
                    onNavigateToAdminQueue = {
                        navController.navigate(AppRoute.MainGraph) {
                            popUpTo(AppRoute.AuthGraph) { inclusive = true }
                        }
                    }
                )
            }

            composable<AppRoute.MerchantSignIn> {
                MerchantSignInScreen(
                    onNavigateToMerchantHome = {
                        navController.navigate(AppRoute.MainGraph) {
                            popUpTo(AppRoute.AuthGraph) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToSignUp = {
                        navController.navigate(AppRoute.SignUp)
                    }
                )
            }

            composable<AppRoute.AdminSignIn> {
                AdminSignInScreen(
                    onNavigateToAdminQueue = {
                        navController.navigate(AppRoute.MainGraph) {
                            popUpTo(AppRoute.AuthGraph) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToSignUp = {
                        navController.navigate(AppRoute.SignUp)
                    }
                )
            }

            composable<AppRoute.SignUp> {
                SignUpScreen(
                    onNavigateToMerchantHome = {
                        navController.navigate(AppRoute.MainGraph) {
                            popUpTo(AppRoute.AuthGraph) { inclusive = true }
                        }
                    },
                    onNavigateToAdminQueue = {
                        navController.navigate(AppRoute.MainGraph) {
                            popUpTo(AppRoute.AuthGraph) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        // ── MAIN GRAPH ───────────────────────────────────────────────
        navigation<AppRoute.MainGraph>(startDestination = AppRoute.MerchantHome) {

            composable<AppRoute.MerchantHome> {
                MerchantHomeScreen(
                    onCaptureInvoice = {
                        println("TODO: Invoice Capture")
                    },
                    onPay = {
                        println("TODO: Payment")
                    },
                    onHistory = {
                        println("TODO: History")
                    },
                    onNotifications = {
                        println("TODO: Notifications")
                    }
                )
            }

            composable<AppRoute.AdminQueue> {
                StubScreen("Admin Queue — coming soon")
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
            color = DukaCredColors.Cream100
        )
    }
}