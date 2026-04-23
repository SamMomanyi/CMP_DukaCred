package com.samduka.dukacred.app.navigation

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
        startDestination = AppRoute.RolePicker
    ) {

        // ── Role Picker ───────────────────────────────────────────────
        composable<AppRoute.RolePicker> {
            RolePickerScreen(
                onNavigateToMerchantSignIn = {
                    navController.navigate(AppRoute.MerchantSignIn)
                },
                onNavigateToAdminSignIn = {
                    navController.navigate(AppRoute.AdminSignIn)
                },
                onNavigateToMerchantHome = {
                    navController.navigate(AppRoute.MerchantHome) {
                        popUpTo(AppRoute.RolePicker) { inclusive = true }
                    }
                },
                onNavigateToAdminQueue = {
                    navController.navigate(AppRoute.AdminQueue) {
                        popUpTo(AppRoute.RolePicker) { inclusive = true }
                    }
                }
            )
        }

        // ── Merchant Sign-In ──────────────────────────────────────────
        composable<AppRoute.MerchantSignIn> {
            MerchantSignInScreen(
                onNavigateToMerchantHome = {
                    navController.navigate(AppRoute.MerchantHome) {
                        popUpTo(AppRoute.RolePicker) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToSignUp = {
                    navController.navigate(AppRoute.SignUp)
                }
            )
        }

        // ── Admin Sign-In ─────────────────────────────────────────────
        composable<AppRoute.AdminSignIn> {
            AdminSignInScreen(
                onNavigateToAdminQueue = {
                    navController.navigate(AppRoute.AdminQueue) {
                        popUpTo(AppRoute.RolePicker) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToSignUp = {
                    navController.navigate(AppRoute.SignUp)
                }
            )
        }

        // ── Sign Up ───────────────────────────────────────────────────
        composable<AppRoute.SignUp> {
            SignUpScreen(
                onNavigateToMerchantHome = {
                    navController.navigate(AppRoute.MerchantHome) {
                        popUpTo(AppRoute.RolePicker) { inclusive = true }
                    }
                },
                onNavigateToAdminQueue = {
                    navController.navigate(AppRoute.AdminQueue) {
                        popUpTo(AppRoute.RolePicker) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ── Merchant Home ─────────────────────────────────────────────
        composable<AppRoute.MerchantHome> {
            MerchantHomeScreen(
                onCaptureInvoice = {
                    println("TODO: Navigate to Invoice Capture")
                    // navController.navigate(AppRoute.InvoiceCapture)
                },
                onPay = {
                    println("TODO: Navigate to Payment")
                },
                onHistory = {
                    println("TODO: Navigate to History")
                },
                onNotifications = {
                    println("TODO: Navigate to Notifications")
                }
            )
        }

        // ── Admin Queue (stub for now) ────────────────────────────────
        composable<AppRoute.AdminQueue> {
            StubScreen(label = "Admin Queue — coming soon")
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