package com.samduka.dukacred.feature.auth.di

import com.samduka.dukacred.feature.auth.data.repository.AuthRepositoryImpl
import com.samduka.dukacred.feature.auth.domain.repository.AuthRepository
import com.samduka.dukacred.feature.auth.domain.usecase.RestoreSessionUseCase
import com.samduka.dukacred.feature.auth.domain.usecase.SignInAdminUseCase
import com.samduka.dukacred.feature.auth.domain.usecase.SignInMerchantUseCase
import com.samduka.dukacred.feature.auth.presentation.viewmodel.AdminSignInViewModel
import com.samduka.dukacred.feature.auth.presentation.viewmodel.MerchantSignInViewModel
import com.samduka.dukacred.feature.auth.presentation.viewmodel.RolePickerViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authModule = module {
    // Repository — single instance, bound to the interface
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class

    // Use cases — factory so each caller gets a fresh instance
    factoryOf(::SignInMerchantUseCase)
    factoryOf(::SignInAdminUseCase)
    factoryOf(::RestoreSessionUseCase)

    // ViewModels
    factoryOf(::RolePickerViewModel)
    factoryOf(::MerchantSignInViewModel)
    factoryOf(::AdminSignInViewModel)
}