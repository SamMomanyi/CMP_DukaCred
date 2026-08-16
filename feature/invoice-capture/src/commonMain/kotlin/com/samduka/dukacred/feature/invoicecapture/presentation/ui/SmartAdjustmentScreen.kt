// presentation/ui/SmartAdjustmentScreen.kt
package com.samduka.dukacred.feature.invoicecapture.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samduka.dukacred.core.designsystem.components.DukaPrimaryButton
import com.samduka.dukacred.feature.invoicecapture.presentation.FinancingItem
import com.samduka.dukacred.feature.invoicecapture.presentation.InvoiceReviewIntent
import com.samduka.dukacred.feature.invoicecapture.presentation.InvoiceReviewState

@Composable
fun SmartAdjustmentScreen(
    state: InvoiceReviewState.PartialAdjustment,
    onIntent: (InvoiceReviewIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = Color.Black,
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth().background(Color.Black).navigationBarsPadding().padding(horizontal = 20.dp, vertical = 16.dp),
            ) {
                Text("ADJUSTED TOTAL", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                Text(
                    "${state.invoice.currency} ${formatAmount(state.adjustedTotal)}",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(12.dp))
                DukaPrimaryButton(
                    text = "Confirm Adjusted Loan",
                    onClick = { onIntent(InvoiceReviewIntent.ConfirmAdjustedLoan) },
                    trailingIcon = { Icon(Icons.Rounded.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp)) },
                )
            }
        },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.width(4.dp))
                Text("Smart Adjustment", color = MaterialTheme.colorScheme.primary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            ) {
                item {
                    PartialApprovalBanner(
                        maxCoveredLimit = state.maxCoveredLimit,
                        invoiceTotal = state.invoice.totalAmount,
                        currency = state.invoice.currency,
                    )
                    Spacer(Modifier.height(20.dp))
                    Text("Suggested Financing", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(12.dp))
                }
                items(state.items, key = { it.id }) { item ->
                    FinancingItemRow(
                        item = item,
                        currency = state.invoice.currency,
                        onToggle = { onIntent(InvoiceReviewIntent.ToggleItemSelection(item.id)) },
                    )
                    Spacer(Modifier.height(10.dp))
                }
                item { Spacer(Modifier.height(12.dp)) }
            }
        }
    }
}

@Composable
private fun PartialApprovalBanner(maxCoveredLimit: Double, invoiceTotal: Double, currency: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5E6C8).copy(alpha = 0.12f))
            .border(1.dp, Color(0xFFE5A93C).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(16.dp),
    ) {
        Icon(Icons.Rounded.WarningAmber, contentDescription = null, tint = Color(0xFFE5A93C), modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Text(
            "Partial Approval: Your limit covers $currency ${formatAmount(maxCoveredLimit)} of this $currency ${formatAmount(invoiceTotal)} invoice.",
            color = Color(0xFFE5A93C),
            fontSize = 14.sp,
            lineHeight = 20.sp,
        )
    }
}

@Composable
private fun FinancingItemRow(item: FinancingItem, currency: String, onToggle: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
            .padding(16.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Text(
                    item.name,
                    color = if (item.isSelected) Color.White else Color.White.copy(alpha = 0.4f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    textDecoration = if (item.isSelected) TextDecoration.None else TextDecoration.LineThrough,
                )
                item.tag?.let { tag ->
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(tag, color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Switch(
                checked = item.isSelected,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    checkedBorderColor = Color.Transparent,
                ),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "$currency ${formatAmount(item.amount)}",
            color = if (item.isSelected) Color.White.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.35f),
            fontSize = 14.sp,
            textDecoration = if (item.isSelected) TextDecoration.None else TextDecoration.LineThrough,
        )
        if (!item.isSelected) {
            item.warningNote?.let { note ->
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Rounded.Info, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(note, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                }
            }
        }
    }
}