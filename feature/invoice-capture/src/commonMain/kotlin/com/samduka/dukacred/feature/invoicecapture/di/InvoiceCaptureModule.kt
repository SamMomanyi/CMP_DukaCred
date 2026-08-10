package com.samduka.dukacred.feature.invoicecapture.di


import com.samduka.dukacred.feature.invoicecapture.data.SupabaseInvoiceRepository
import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceImageCache
import com.samduka.dukacred.feature.invoicecapture.domain.repository.InvoiceRepository
import com.samduka.dukacred.feature.invoicecapture.presentation.InvoiceCaptureViewModel
import com.samduka.dukacred.feature.invoicecapture.presentation.InvoiceProcessingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val invoiceCaptureModule = module {
    single { InvoiceImageCache() }



    single<InvoiceRepository> {
        SupabaseInvoiceRepository(
            ocrService = get(),
            supabaseClient = get()
        )
    }


    viewModel {
        InvoiceProcessingViewModel(
            imageCache = get(),
            repository = get()
        )
    }


    viewModel {
        InvoiceCaptureViewModel(
            imageCache = get()
        )
    }
}