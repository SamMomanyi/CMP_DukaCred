package com.samduka.dukacred.feature.invoicecapture.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.samduka.dukacred.core.designsystem.DukaCredColors
import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceLineItem
import kotlin.math.abs
import kotlin.math.round

// Same green ScannerOverlay uses for its "document locked" state — reused
// here for visual consistency since it's not in the confirmed DukaCredColors set.
internal val InvoiceSuccessGreen = Color(0xFF4CAF50)

/** Multiplatform-safe 2dp amount formatting — String.format is JVM-only. */
internal fun formatAmount(value: Double): String {
    val isNegative = value < 0
    val cents = round(abs(value) * 100).toLong()
    val whole = cents / 100
    val fraction = cents % 100
    val sign = if (isNegative) "-" else ""
    return "$sign$whole.${fraction.toString().padStart(2, '0')}"
}

@Composable
internal fun dukaCredFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = DukaCredColors.ForestGreen900,
    focusedLabelColor = DukaCredColors.ForestGreen900,
    cursorColor = DukaCredColors.ForestGreen900,
)

@Composable
internal fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        colors = dukaCredFieldColors(),
    )
}

@Composable
internal fun AmountField(
    label: String,
    value: Double,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Local text buffer so the field doesn't snap mid-edit (e.g. typing
    // "12." before the decimal digits show up). Reformats from the upstream
    // Double on every valid keystroke too — a bit eager, but keeps the
    // single source of truth in ParsedInvoice rather than duplicating state.
    var text by remember(value) { mutableStateOf(formatAmount(value)) }
    OutlinedTextField(
        value = text,
        onValueChange = { input ->
            text = input
            input.toDoubleOrNull()?.let(onValueChange)
        },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier.fillMaxWidth(),
        colors = dukaCredFieldColors(),
    )
}

@Composable
internal fun LineItemRow(
    lineItem: InvoiceLineItem,
    onChange: (InvoiceLineItem) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LabeledField(
                label = "Item",
                value = lineItem.description,
                onValueChange = { onChange(lineItem.copy(description = it)) },
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Remove item",
                    tint = DukaCredColors.Error,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AmountField(
                label = "Qty",
                value = lineItem.quantity,
                onValueChange = { qty ->
                    onChange(lineItem.copy(quantity = qty, totalPrice = qty * lineItem.unitPrice))
                },
                modifier = Modifier.weight(0.7f),
            )
            AmountField(
                label = "Unit price",
                value = lineItem.unitPrice,
                onValueChange = { price ->
                    onChange(lineItem.copy(unitPrice = price, totalPrice = lineItem.quantity * price))
                },
                modifier = Modifier.weight(1f),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Line total",
                    style = MaterialTheme.typography.labelSmall,
                    color = DukaCredColors.ForestGreen900.copy(alpha = 0.6f),
                )
                Text(
                    formatAmount(lineItem.totalPrice),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = DukaCredColors.ForestGreen900,
                )
            }
        }
    }
}