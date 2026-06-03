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
import com.samduka.dukacred.app.presentation.AnimatedSplashScreen
import com.samduka.dukacred.app.presentation.DashboardShellScreen
import com.samduka.dukacred.core.designsystem.DukaCredColors
import com.samduka.dukacred.feature.auth.presentation.ui.AdminSignInScreen
import com.samduka.dukacred.feature.auth.presentation.ui.MerchantSignInScreen
import com.samduka.dukacred.feature.auth.presentation.ui.RolePickerScreen
import com.samduka.dukacred.feature.auth.presentation.ui.SignUpScreen
import com.samduka.dukacred.feature.invoicecapture.presentation.ui.InvoiceCaptureScreen
import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceImageCache
import org.koin.compose.koinInject

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoute.Splash
    ) {
        composable<AppRoute.Splash> {
            AnimatedSplashScreen(
                onSplashFinished = {
                    navController.navigate(AppRoute.AuthGraph) {
                        popUpTo(AppRoute.Splash) { inclusive = true }
                    }
                }
            )
        }

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
                        navController.navigate(AppRoute.MerchantGraph) {
                            popUpTo(AppRoute.AuthGraph) { inclusive = true }
                        }
                    },
                    onNavigateToAdminQueue = {
                        navController.navigate(AppRoute.AdminGraph) {
                            popUpTo(AppRoute.AuthGraph) { inclusive = true }
                        }
                    }
                )
            }

            composable<AppRoute.MerchantSignIn> {
                MerchantSignInScreen(
                    onNavigateToMerchantHome = {
                        navController.navigate(AppRoute.MerchantGraph) {
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
                        navController.navigate(AppRoute.AdminGraph) {
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
                        navController.navigate(AppRoute.MerchantGraph) {
                            popUpTo(AppRoute.AuthGraph) { inclusive = true }
                        }
                    },
                    onNavigateToAdminQueue = {
                        navController.navigate(AppRoute.AdminGraph) {
                            popUpTo(AppRoute.AuthGraph) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        // ── MERCHANT GRAPH ───────────────────────────────────────────
        navigation<AppRoute.MerchantGraph>(startDestination = AppRoute.MerchantHome) {

            composable<AppRoute.MerchantHome> {
                DashboardShellScreen(
                    onNavigateToInvoiceCapture = {
                        navController.navigate(AppRoute.InvoiceCapture)
                    }
                )
            }

            composable<AppRoute.InvoiceCapture> {
                // Inject the cache singleton
                val imageCache: InvoiceImageCache = koinInject()

                InvoiceCaptureScreen(
                    onClose = {
                        navController.popBackStack()
                    },
                    onImageCaptured = { bytes ->
                        // 1. Save the massive byte array to the cache
                        imageCache.capturedImageBytes = bytes

                        // 2. Safely pop the heavy camera screen off the stack
                        navController.popBackStack()

                        // 3. Navigate to the processing screen
                        navController.navigate(AppRoute.InvoiceProcessing)
                    }
                )
            }

            // The new processing screen
            composable<AppRoute.InvoiceProcessing> {
                val imageCache: InvoiceImageCache = koinInject()

                // Temporary stub until we build the actual AI API call
                StubScreen("Processing ${imageCache.capturedImageBytes?.size ?: 0} bytes...")
            }
        }

        // ── ADMIN GRAPH ──────────────────────────────────────────────
        navigation<AppRoute.AdminGraph>(startDestination = AppRoute.AdminQueue) {
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