package com.samduka.dukacred.app.shell

import com.samduka.dukacred.app.navigation.AppDestination
import com.samduka.dukacred.core.common.mvi.UiAction
import com.samduka.dukacred.core.common.mvi.UiEffect
import com.samduka.dukacred.core.common.mvi.UiState

data class AppShellState(
    val currentDestination: AppDestination = AppDestination.RolePicker,
) : UiState

sealed interface AppShellAction : UiAction {
    data object SelectMerchant : AppShellAction
    data object SelectAdmin : AppShellAction
    data object NavigateToRolePicker : AppShellAction
}

sealed interface AppShellEffect : UiEffect
