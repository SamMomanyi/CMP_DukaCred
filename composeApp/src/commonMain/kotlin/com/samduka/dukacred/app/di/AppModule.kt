package com.samduka.dukacred.app.di

import com.samduka.dukacred.core.network.di.networkModule
import org.koin.dsl.module

val appModules = listOf(
    networkModule,
    // more modules added here as we build features
)