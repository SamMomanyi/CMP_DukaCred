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

/**
 * Root navigation host.
 *
 * Rules:
 * - Start destination is always [AppRoute.RolePicker]. The [RolePickerViewModel]
 *   handles session-restore internally; if a valid session exists it fires an
 *   effect that navigates forward before the picker UI is ever visible.
 * - Sign-in screens pop themselves off the back stack on success so that the
 *   system Back button cannot return to a sign-in screen from the home screen.
 * - MerchantHome and AdminQueue clear the entire auth back stack on entry, so
 *   Back from those screens exits the app rather than returning to auth.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController    = navController,
        startDestination = AppRoute.RolePicker.route,
    ) {

        // ── Role Picker ───────────────────────────────────────────────────────
        composable(route = AppRoute.RolePicker.route) {
            RolePickerScreen(
                onNavigateToMerchantSignIn = {
                    navController.navigate(AppRoute.MerchantSignIn.route)
                },
                onNavigateToAdminSignIn = {
                    navController.navigate(AppRoute.AdminSignIn.route)
                },
                onNavigateToMerchantHome = {
                    navController.navigate(AppRoute.MerchantHome.route) {
                        // Clear the entire auth back stack — user is now authenticated
                        popUpTo(AppRoute.RolePicker.route) { inclusive = true }
                    }
                },
                onNavigateToAdminQueue = {
                    navController.navigate(AppRoute.AdminQueue.route) {
                        popUpTo(AppRoute.RolePicker.route) { inclusive = true }
                    }
                },
            )
        }

        // ── Merchant Sign-In ──────────────────────────────────────────────────
        composable(route = AppRoute.MerchantSignIn.route) {
            MerchantSignInScreen(
                onNavigateToMerchantHome = {
                    navController.navigate(AppRoute.MerchantHome.route) {
                        // Pop everything up to (and including) RolePicker so the
                        // auth graph is fully removed from the back stack
                        popUpTo(AppRoute.RolePicker.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
            )
        }

        // ── Admin Sign-In ─────────────────────────────────────────────────────
        composable(route = AppRoute.AdminSignIn.route) {
            AdminSignInScreen(
                onNavigateToAdminQueue = {
                    navController.navigate(AppRoute.AdminQueue.route) {
                        popUpTo(AppRoute.RolePicker.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
            )
        }

        // ── Merchant Home (stub) ──────────────────────────────────────────────
        composable(route = AppRoute.MerchantHome.route) {
            StubScreen(label = "Merchant Home — coming soon")
        }

        // ── Admin Queue (stub) ────────────────────────────────────────────────
        composable(route = AppRoute.AdminQueue.route) {
            StubScreen(label = "Admin Queue — coming soon")
        }
    }
}


@Composable
private fun StubScreen(label: String) {
    Box(
        modifier          = Modifier
            .fillMaxSize()
            .background(DukaCredColors.ForestGreen900),
        contentAlignment  = Alignment.Center,
    ) {
        Text(
            text  = label,
            color = DukaCredColors.Cream100,
        )
    }
}