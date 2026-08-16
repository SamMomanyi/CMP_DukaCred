// presentation/ui/InvoiceVerificationScreen.kt
package com.samduka.dukacred.feature.invoicecapture.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samduka.dukacred.core.designsystem.components.DukaPrimaryButton
import com.samduka.dukacred.core.designsystem.components.DukaSecondaryButton
import com.samduka.dukacred.core.designsystem.components.DukaSurfaceCard
import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceLineItem
import com.samduka.dukacred.feature.invoicecapture.presentation.InvoiceReviewIntent
import com.samduka.dukacred.feature.invoicecapture.presentation.InvoiceReviewState

@Composable
fun InvoiceVerificationScreen(
    state: InvoiceReviewState.FullApproval,
    onIntent: (InvoiceReviewIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var lineItemsExpanded by remember { mutableStateOf(false) }

    Scaffold(modifier = modifier, containerColor = Color.Black) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(Modifier.width(4.dp))
                Text("Invoice Verification", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(8.dp))

                DukaSurfaceCard(showBorder = true) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Rounded.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Data extracted successfully. Please review before proceeding.",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp,
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                DukaSurfaceCard(showBorder = true) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Invoice Details", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Icon(Icons.Rounded.Description, contentDescription = null, tint = Color.White.copy(alpha = 0.5f))
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        LabeledDetail(label = "SUPPLIER", value = state.invoice.merchantName)
                        LabeledDetail(label = "DATE", value = state.invoice.invoiceDate)
                    }
                    Spacer(Modifier.height(16.dp))
                    LabeledDetail(
                        label = "TOTAL AMOUNT",
                        value = "${state.invoice.currency} ${formatAmount(state.invoice.totalAmount)}",
                        valueSize = 26.sp,
                    )
                    Spacer(Modifier.height(16.dp))
                    LineItemsDropdown(
                        count = state.invoice.lineItems.size,
                        expanded = lineItemsExpanded,
                        onToggle = { lineItemsExpanded = !lineItemsExpanded },
                        lineItems = state.invoice.lineItems,
                    )
                }

                Spacer(Modifier.height(16.dp))
                ApprovedCard(termDays = state.termDays, interestRatePercent = state.interestRate * 100)
                Spacer(Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                DukaSecondaryButton(text = "Edit Details", onClick = { onIntent(InvoiceReviewIntent.EditDetails) }, modifier = Modifier.weight(1f))
                DukaPrimaryButton(text = "Confirm & Request Loan", onClick = { onIntent(InvoiceReviewIntent.ConfirmFullLoan) }, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun LabeledDetail(label: String, value: String, valueSize: TextUnit = 16.sp) {
    Column {
        Text(label, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Text(value, color = Color.White, fontSize = valueSize, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun LineItemsDropdown(count: Int, expanded: Boolean, onToggle: () -> Unit, lineItems: List<InvoiceLineItem>) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.04f))) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle).padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("$count Line Item${if (count == 1) "" else "s"}", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Icon(Icons.Rounded.ExpandMore, contentDescription = null, tint = Color.White, modifier = Modifier.rotate(if (expanded) 180f else 0f))
        }
        if (expanded) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                lineItems.forEach { item ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(item.description, color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                        Text("KES ${formatAmount(item.totalPrice)}", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ApprovedCard(termDays: Int, interestRatePercent: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F2419))
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(10.dp))
        Text("Approved for Inventory Loan", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.Black.copy(alpha = 0.3f)).padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            TermStat(label = "TERM", value = "$termDays days")
            Box(modifier = Modifier.width(1.dp).height(32.dp).background(Color.White.copy(alpha = 0.15f)))
            TermStat(label = "INTEREST", value = "${interestRatePercent.toInt()}%")
        }
    }
}

@Composable
private fun TermStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}