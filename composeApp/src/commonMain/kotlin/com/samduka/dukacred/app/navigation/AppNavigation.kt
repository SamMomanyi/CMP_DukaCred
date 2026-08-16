package com.samduka.dukacred.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.samduka.dukacred.app.presentation.AnimatedSplashScreen
import com.samduka.dukacred.app.presentation.DashboardShellScreen
import com.samduka.dukacred.feature.auth.presentation.ui.AdminSignInScreen
import com.samduka.dukacred.feature.auth.presentation.ui.MerchantSignInScreen
import com.samduka.dukacred.feature.auth.presentation.ui.RolePickerScreen
import com.samduka.dukacred.feature.auth.presentation.ui.SignUpScreen
import com.samduka.dukacred.feature.invoicecapture.presentation.ui.FinancingSuccessScreen
import com.samduka.dukacred.feature.invoicecapture.presentation.ui.InvoiceReviewScreen
import com.samduka.dukacred.feature.invoicecapture.presentation.ui.ManualInvoiceEditScreen

private const val INVOICE_EDITED_RESULT_KEY = "invoice_edited"

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


            composable<AppRoute.SmartAdjustment> { backStackEntry ->
                val route: AppRoute.SmartAdjustment = backStackEntry.toRoute()
                val invoiceWasEdited by backStackEntry.savedStateHandle
                    .getStateFlow(INVOICE_EDITED_RESULT_KEY, false)
                    .collectAsState()

                InvoiceReviewScreen(
                    invoiceId = route.invoiceId,
                    invoiceWasEdited = invoiceWasEdited,
                    onInvoiceEditedHandled = {
                        backStackEntry.savedStateHandle[INVOICE_EDITED_RESULT_KEY] = false
                    },
                    onBack = { navController.popBackStack() },
                    onNavigateToManualEdit = { invoice ->
                        navController.navigate(AppRoute.ManualInvoiceEdit(invoiceId = invoice.id ?: route.invoiceId))
                    },
                    onNavigateToFinancingSuccess = { loanId ->
                        navController.navigate(AppRoute.FinancingSuccess(loanId)) {
                            popUpTo<AppRoute.SmartAdjustment> { inclusive = true }
                        }
                    },
                )
            }

            composable<AppRoute.ManualInvoiceEdit> { backStackEntry ->
                val route: AppRoute.ManualInvoiceEdit = backStackEntry.toRoute()
                ManualInvoiceEditScreen(
                    invoiceId = route.invoiceId,
                    onBack = { didSave ->
                        if (didSave) {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set(INVOICE_EDITED_RESULT_KEY, true)
                        }
                        navController.popBackStack()
                    },
                )
            }

            composable<AppRoute.FinancingSuccess> { backStackEntry ->
                val route: AppRoute.FinancingSuccess = backStackEntry.toRoute()
                FinancingSuccessScreen(
                    loanId = route.loanId,
                    onBackToDashboard = {
                        navController.navigate(AppRoute.MerchantHome) {
                            popUpTo<AppRoute.MerchantHome> { inclusive = true }
                        }
                    },
                )
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
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}