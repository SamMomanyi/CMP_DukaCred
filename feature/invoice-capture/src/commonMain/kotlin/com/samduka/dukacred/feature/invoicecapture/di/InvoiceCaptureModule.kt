package com.samduka.dukacred.feature.invoicecapture.di


import com.samduka.dukacred.feature.invoicecapture.data.SupabaseInvoiceRepository
import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceImageCache
import com.samduka.dukacred.feature.invoicecapture.data.GeminiInvoiceOcrService
import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceOcrService
import com.samduka.dukacred.feature.invoicecapture.domain.repository.InvoiceRepository
import com.samduka.dukacred.feature.invoicecapture.domain.usecase.AnalyzeFrameQualityUseCase
import com.samduka.dukacred.feature.invoicecapture.domain.usecase.CompressImageUseCase
import com.samduka.dukacred.feature.invoicecapture.domain.usecase.ExtractInvoiceDataUseCase
import com.samduka.dukacred.feature.invoicecapture.domain.usecase.GetInvoiceByIdUseCase
import com.samduka.dukacred.feature.invoicecapture.domain.usecase.SaveInvoiceDraftUseCase
import com.samduka.dukacred.feature.invoicecapture.domain.usecase.UpdateInvoiceDraftUseCase
import com.samduka.dukacred.feature.invoicecapture.presentation.CaptureViewModel
import com.samduka.dukacred.feature.invoicecapture.presentation.InvoiceCaptureViewModel
import com.samduka.dukacred.feature.invoicecapture.presentation.InvoiceProcessingViewModel
import com.samduka.dukacred.feature.invoicecapture.presentation.InvoiceReviewViewModel
import com.samduka.dukacred.feature.invoicecapture.presentation.ManualInvoiceEditViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

val invoiceCaptureModule = module {
    single { InvoiceImageCache() }

    single<CoroutineDispatcher> { Dispatchers.Default }

    single<InvoiceOcrService> { GeminiInvoiceOcrService(geminiApi = get()) }

    single<InvoiceRepository> {
        SupabaseInvoiceRepository(
            ocrService = get(),
            supabaseClient = get()
        )
    }

    factory { AnalyzeFrameQualityUseCase() }
    factory { CompressImageUseCase() }
    factory { ExtractInvoiceDataUseCase(invoiceOcrService = get()) }
    factory { SaveInvoiceDraftUseCase(invoiceRepository = get()) }
    factory { GetInvoiceByIdUseCase(invoiceRepository = get()) }
    factory { UpdateInvoiceDraftUseCase(invoiceRepository = get()) }


    viewModel {
        InvoiceProcessingViewModel(
            imageCache = get(),
            extractInvoiceData = get(),
            saveInvoiceDraft = get(),
        )
    }

    viewModel {
        CaptureViewModel(
            analyzeFrameQuality = get(),
            compressImage = get(),
            extractInvoiceData = get(),
            saveInvoiceDraft = get(),
            dispatcher = get(),
        )
    }


    viewModel {
        InvoiceCaptureViewModel(
            imageCache = get()
        )
    }
    viewModel { parameters ->
        InvoiceReviewViewModel(
            invoiceId = parameters.get(),
            evaluateFinancingRequest = get(),
            confirmFinancingRequest = get(),
        )
    }

    viewModel { parameters ->
        ManualInvoiceEditViewModel(
            invoiceId = parameters.get(),
            getInvoiceById = get(),
            updateInvoiceDraft = get(),
        )
    }
}
