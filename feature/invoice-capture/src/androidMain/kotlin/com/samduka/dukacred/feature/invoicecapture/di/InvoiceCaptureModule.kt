package com.samduka.dukacred.feature.invoicecapture.di

import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceImageCache
import com.samduka.dukacred.feature.invoicecapture.presentation.InvoiceCaptureViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val invoiceCaptureModule = module {
    // Stores the image safely outside the Android Navigation system
    single { InvoiceImageCache() }

    // Tells Koin how to create your ViewModel
    viewModel { InvoiceCaptureViewModel() }
}