package com.samduka.dukacred.feature.invoicecapture.di

import androidx.lifecycle.viewmodel.CreationExtras.Empty.get
import com.samduka.dukacred.feature.invoicecapture.presentation.InvoiceCaptureViewModel
import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceImageCache
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val invoiceCaptureModule = module {
    single { InvoiceImageCache() }
    // Tells Koin how to create your ViewModel
    viewModel { InvoiceCaptureViewModel() }

}