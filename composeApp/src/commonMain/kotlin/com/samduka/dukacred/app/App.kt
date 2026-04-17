package com.samduka.dukacred.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.samduka.dukacred.app.di.appModule
import com.samduka.dukacred.app.navigation.AppDestination
import com.samduka.dukacred.app.shell.AppShellAction
import com.samduka.dukacred.app.shell.AppShellViewModel
import com.samduka.dukacred.feature.adminreview.presentation.ui.AdminQueueScreen
import com.samduka.dukacred.feature.merchanthome.presentation.ui.MerchantHomeScreen
import com.samduka.dukacred.feature.rolepicker.presentation.ui.RolePickerScreen
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
fun App() {
    KoinApplication(
        application = {
            modules(appModule())
        }
    ) {
        MaterialTheme {
            val viewModel = koinInject<AppShellViewModel>()
            val state by viewModel.state.collectAsState()

            when (state.currentDestination) {
                AppDestination.RolePicker -> RolePickerScreen(
                    onMerchantSelected = {
                        viewModel.onAction(AppShellAction.SelectMerchant)
                    },
                    onAdminSelected = {
                        viewModel.onAction(AppShellAction.SelectAdmin)
                    },
                )

                AppDestination.MerchantHome -> MerchantHomeScreen(
                    onBack = {
                        viewModel.onAction(AppShellAction.NavigateToRolePicker)
                    },
                )

                AppDestination.AdminQueue -> AdminQueueScreen(
                    onBack = {
                        viewModel.onAction(AppShellAction.NavigateToRolePicker)
                    },
                )
            }
        }
    }
}
