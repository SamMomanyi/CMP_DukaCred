package com.samduka.dukacred.app.shell

import com.samduka.dukacred.app.navigation.AppDestination
import com.samduka.dukacred.core.common.mvi.BaseViewModel

class AppShellViewModel : BaseViewModel<AppShellState, AppShellAction, AppShellEffect>(
    initialState = AppShellState(),
) {
    override fun onAction(action: AppShellAction) {
        when (action) {
            AppShellAction.SelectAdmin -> updateState {
                it.copy(currentDestination = AppDestination.AdminQueue)
            }

            AppShellAction.SelectMerchant -> updateState {
                it.copy(currentDestination = AppDestination.MerchantHome)
            }

            AppShellAction.NavigateToRolePicker -> updateState {
                it.copy(currentDestination = AppDestination.RolePicker)
            }
        }
    }
}
