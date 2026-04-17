package com.samduka.dukacred

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.samduka.dukacred.app.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "DukaCred",
    ) {
        App()
    }
}
