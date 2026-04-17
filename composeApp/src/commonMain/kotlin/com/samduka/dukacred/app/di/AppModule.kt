package com.samduka.dukacred.app.di

import com.samduka.dukacred.app.shell.AppShellViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

fun appModule(): Module = module {
    factory { AppShellViewModel() }
}
