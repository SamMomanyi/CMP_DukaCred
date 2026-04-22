package com.samduka.dukacred.app.di

import com.samduka.dukacred.core.network.di.networkModule
import com.samduka.dukacred.feature.auth.di.authModule
import org.koin.core.module.Module

/**
 * Master list of all Koin modules for the app.

 */
val appModules: List<Module> = listOf(
    networkModule,
    authModule,

)