package com.samduka.dukacred

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.samduka.dukacred.app.App
import com.samduka.dukacred.app.di.appModules
import org.koin.core.context.startKoin

fun main() {
    // 1. Boot up Koin and load your modules FIRST
    startKoin {
        modules(appModules)
    }

    // 2. Launch the Desktop UI SECOND
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "DukaCred",
        ) {
            App()
        }
    }
}