package com.samduka.dukacred.feature.invoicecapture.di

import com.samduka.dukacred.feature.invoicecapture.presentation.InvoiceCaptureViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val invoiceCaptureModule = module {
    // Tells Koin how to create your ViewModel
    viewModel { InvoiceCaptureViewModel() }

}