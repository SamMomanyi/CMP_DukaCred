// presentation/ui/ManualInvoiceEditScreen.kt
package com.samduka.dukacred.feature.invoicecapture.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samduka.dukacred.core.designsystem.components.DukaGhostButton
import com.samduka.dukacred.core.designsystem.components.DukaPrimaryButton
import com.samduka.dukacred.feature.invoicecapture.presentation.ManualInvoiceEditEffect
import com.samduka.dukacred.feature.invoicecapture.presentation.ManualInvoiceEditIntent
import com.samduka.dukacred.feature.invoicecapture.presentation.ManualInvoiceEditViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualInvoiceEditScreen(
    invoiceId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ManualInvoiceEditViewModel = koinViewModel { parametersOf(invoiceId) },
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ManualInvoiceEditEffect.NavigateBack -> onBack()
                // ShowError: wire to a SnackbarHostState once this screen owns one.
                is ManualInvoiceEditEffect.ShowError -> Unit
            }
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color.Black,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(Modifier.width(4.dp))
                Text("Edit Invoice", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        },
        bottomBar = {
            state.invoice?.let {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    DukaGhostButton(
                        text = "Cancel",
                        onClick = { viewModel.onIntent(ManualInvoiceEditIntent.DiscardClicked) },
                        modifier = Modifier.weight(1f),
                    )
                    DukaPrimaryButton(
                        text = "Save Changes",
                        onClick = { viewModel.onIntent(ManualInvoiceEditIntent.SaveClicked) },
                        loading = state.isSaving,
                        modifier = Modifier.weight(2f),
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                state.isLoading -> CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Center),
                )
                state.errorMessage != null -> Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(state.errorMessage.orEmpty(), color = Color.White, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    DukaPrimaryButton(
                        text = "Retry",
                        onClick = { viewModel.onIntent(ManualInvoiceEditIntent.Retry) },
                    )
                }
                state.invoice != null -> {
                    val invoice = state.invoice!!
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        item {
                            LabeledField(
                                label = "Supplier",
                                value = invoice.merchantName,
                                onValueChange = { viewModel.onIntent(ManualInvoiceEditIntent.MerchantNameChanged(it)) },
                            )
                        }
                        item {
                            LabeledField(
                                label = "Invoice Date",
                                value = invoice.invoiceDate,
                                onValueChange = { viewModel.onIntent(ManualInvoiceEditIntent.InvoiceDateChanged(it)) },
                            )
                        }
                        item {
                            LabeledField(
                                label = "Invoice Number",
                                value = invoice.invoiceNumber.orEmpty(),
                                onValueChange = { viewModel.onIntent(ManualInvoiceEditIntent.InvoiceNumberChanged(it)) },
                            )
                        }
                        item {
                            AmountField(
                                label = "Total Amount",
                                value = invoice.totalAmount,
                                onValueChange = { viewModel.onIntent(ManualInvoiceEditIntent.TotalAmountChanged(it)) },
                            )
                        }
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("Line Items", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                IconButton(onClick = { viewModel.onIntent(ManualInvoiceEditIntent.LineItemAdded) }) {
                                    Icon(Icons.Rounded.Add, contentDescription = "Add line item", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                        items(invoice.lineItems, key = { it.localId }) { item ->
                            LineItemRow(
                                lineItem = item,
                                onChange = { viewModel.onIntent(ManualInvoiceEditIntent.LineItemChanged(it)) },
                                onRemove = { viewModel.onIntent(ManualInvoiceEditIntent.LineItemRemoved(item.localId)) },
                            )
                        }
                        item { Spacer(Modifier.height(8.dp)) }
                    }
                }
            }
        }
    }
}