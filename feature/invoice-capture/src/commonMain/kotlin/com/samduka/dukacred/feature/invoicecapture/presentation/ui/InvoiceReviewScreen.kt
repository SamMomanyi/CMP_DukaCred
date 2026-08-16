// presentation/ui/InvoiceReviewScreen.kt — top-level dispatcher, wires effects + koinViewModel
package com.samduka.dukacred.feature.invoicecapture.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samduka.dukacred.core.designsystem.components.DukaPrimaryButton
import com.samduka.dukacred.core.designsystem.theme.*
import com.samduka.dukacred.feature.invoicecapture.domain.ParsedInvoice
import com.samduka.dukacred.feature.invoicecapture.presentation.InvoiceReviewEffect
import com.samduka.dukacred.feature.invoicecapture.presentation.InvoiceReviewIntent
import com.samduka.dukacred.feature.invoicecapture.presentation.InvoiceReviewState
import com.samduka.dukacred.feature.invoicecapture.presentation.InvoiceReviewViewModel
import com.samduka.dukacred.feature.invoicecapture.presentation.isRefreshing
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun InvoiceReviewScreen(
    invoiceId: String,
    invoiceWasEdited: Boolean = false,
    onInvoiceEditedHandled: () -> Unit = {},
    onBack: () -> Unit,
    onNavigateToManualEdit: (ParsedInvoice) -> Unit,
    onNavigateToFinancingSuccess: (loanId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InvoiceReviewViewModel = koinViewModel { parametersOf(invoiceId) },
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(invoiceWasEdited) {
        if (invoiceWasEdited) {
            viewModel.onIntent(InvoiceReviewIntent.RefreshAfterEdit)
            onInvoiceEditedHandled()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is InvoiceReviewEffect.NavigateToFinancingSuccess -> onNavigateToFinancingSuccess(effect.loanId)
                is InvoiceReviewEffect.NavigateToManualEdit -> onNavigateToManualEdit(effect.invoice)
                InvoiceReviewEffect.NavigateBack -> onBack()
                // ShowToast: wire to a SnackbarHostState once this screen owns one.
                is InvoiceReviewEffect.ShowToast -> Unit
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val current = state) {
            InvoiceReviewState.Evaluating -> EvaluatingState(Modifier.fillMaxSize())
            is InvoiceReviewState.FullApproval -> InvoiceVerificationScreen(
                state = current, onIntent = viewModel::onIntent, onBack = onBack, modifier = Modifier.fillMaxSize(),
            )
            is InvoiceReviewState.PartialAdjustment -> SmartAdjustmentScreen(
                state = current, onIntent = viewModel::onIntent, onBack = onBack, modifier = Modifier.fillMaxSize(),
            )
            is InvoiceReviewState.Error -> ReviewErrorState(
                message = current.message,
                onRetry = { viewModel.onIntent(InvoiceReviewIntent.Retry) },
                modifier = Modifier.fillMaxSize(),
            )
        }

        RecheckingBanner(
            visible = state.isRefreshing,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp),
        )
    }
}

@Composable
private fun RecheckingBanner(visible: Boolean, modifier: Modifier = Modifier) {
    androidx.compose.animation.AnimatedVisibility(
        visible = visible,
        enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically { -it },
        exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.slideOutVertically { -it },
        modifier = modifier,
    ) {
        Surface(
            shape = MaterialTheme.dukaShapes.full,
            color = Color.Black.copy(alpha = 0.75f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Re-checking eligibility…",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun EvaluatingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun ReviewErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
            Text(message, color = Color.White, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            DukaPrimaryButton(text = "Retry", onClick = onRetry)
        }
    }
}