package com.samduka.dukacred.app

import androidx.compose.runtime.Composable
import com.samduka.dukacred.app.navigation.AppNavigation
import com.samduka.dukacred.core.designsystem.DukaCredTheme
import org.koin.compose.KoinApplication
import com.samduka.dukacred.app.di.appModules
import org.koin.compose.KoinApplication
import org.koin.core.KoinApplication
import org.koin.dsl.koinConfiguration


@Composable
fun App() {
    KoinApplication(
        configuration = koinConfiguration(declaration = { modules(appModules) }),
        content = {
            DukaCredTheme {
                AppNavigation()
            }
        })
}