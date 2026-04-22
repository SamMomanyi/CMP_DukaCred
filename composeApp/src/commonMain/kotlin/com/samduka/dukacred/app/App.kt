package com.samduka.dukacred.app

import androidx.compose.runtime.Composable
import com.samduka.dukacred.app.navigation.AppNavigation
import com.samduka.dukacred.core.designsystem.DukaCredTheme
import org.koin.compose.KoinApplication
import com.samduka.dukacred.app.di.appModules


@Composable
fun App() {
    KoinApplication(
        application = {
            modules(appModules)
        }
    ) {
        DukaCredTheme {
            AppNavigation()
        }
    }
}