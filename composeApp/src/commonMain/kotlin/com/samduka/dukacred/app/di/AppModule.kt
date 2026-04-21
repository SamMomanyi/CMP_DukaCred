package com.samduka.dukacred.app.di

import com.samduka.dukacred.core.network.di.networkModule
import com.samduka.dukacred.feature.auth.di.authModule
import org.koin.dsl.module

val appModules = listOf(
    networkModule,
    authModule
    // more modules added here as we build features
)