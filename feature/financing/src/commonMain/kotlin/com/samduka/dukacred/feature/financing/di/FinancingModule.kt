package com.samduka.dukacred.feature.financing.di

import com.samduka.dukacred.feature.financing.data.SupabaseCashflowRepository
import com.samduka.dukacred.feature.financing.domain.repository.CashflowRepository
import com.samduka.dukacred.feature.financing.seed.DevSeedDataInjector
import org.koin.dsl.module

val financingModule = module {
    single<CashflowRepository> {
        SupabaseCashflowRepository(supabaseClient = get())
    }

    factory {
        DevSeedDataInjector(
            invoiceRepository = get(),
            cashflowRepository = get(),
        )
    }
}
