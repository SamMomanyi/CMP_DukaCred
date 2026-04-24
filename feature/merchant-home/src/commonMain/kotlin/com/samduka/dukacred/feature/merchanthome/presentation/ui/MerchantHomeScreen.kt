package com.samduka.dukacred.feature.merchanthome.presentation.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samduka.dukacred.core.designsystem.generated.resources.Res
import com.samduka.dukacred.core.designsystem.generated.resources.*
import com.samduka.dukacred.core.designsystem.DukaCredColors
import com.samduka.dukacred.core.designsystem.DukaCredFonts
import com.samduka.dukacred.core.designsystem.component.DukaCredPrimaryButton
import com.samduka.dukacred.feature.merchanthome.presentation.state.MerchantHomeState
import com.samduka.dukacred.feature.merchanthome.presentation.state.ObligationUiModel
import org.jetbrains.compose.resources.stringResource

private fun formatMoney(amountCents: Long, currency: String = "KES"): String {
    val amount = amountCents / 100.0
    val intPart = amount.toLong().toString()
        .reversed().chunked(3).joinToString(",").reversed()
    val fracPart = ((amountCents % 100).coerceAtLeast(0)).toString().padStart(2, '0')
    return "$currency $intPart.$fracPart"
}

@Composable
fun MerchantHomeScreen(
    state: MerchantHomeState = MerchantHomeState.fakeState,
    onCaptureInvoice: () -> Unit = {},
    onPay: () -> Unit = {},
    onHistory: () -> Unit = {},
    onNotifications: () -> Unit = {},
) {
    Scaffold(
        containerColor = DukaCredColors.ForestGreen900,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {

            item {
                DashboardHeader(
                    merchantName = state.merchantName,
                    onNotificationsClick = onNotifications
                )
            }

            item {
                CreditHeroCard(
                    state = state,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                QuickActions(
                    onCaptureInvoice = onCaptureInvoice,
                    onPay = onPay,
                    onHistory = onHistory,
                    modifier = Modifier.padding(20.dp)
                )
            }

            item {
                ObligationsHeader(
                    count = state.obligations.size,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            if (state.obligations.isEmpty()) {
                item { EmptyState() }
            } else {
                items(state.obligations) {
                    ObligationCard(
                        obligation = it,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    merchantName: String,
    onNotificationsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.home_greeting_morning),
                    fontSize = 14.sp,
                    color = DukaCredColors.Cream300
                )
                Text(
                    text = merchantName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = DukaCredColors.Cream100,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(Res.string.home_subtitle),
                    fontSize = 12.sp,
                    color = DukaCredColors.Cream300
                )
            }

            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(DukaCredColors.ForestGreen800)
            ) {
                Icon(
                    Icons.Filled.Notifications,
                    contentDescription = stringResource(Res.string.home_notifications),
                    tint = DukaCredColors.Ochre400
                )
            }
        }
    }
}

@Composable
private fun CreditHeroCard(
    state: MerchantHomeState,
    modifier: Modifier = Modifier
) {
    val progress by animateFloatAsState(
        targetValue = state.creditUsageFraction,
        animationSpec = tween(800),
        label = ""
    )

    val status = when {
        progress < 0.5f -> stringResource(Res.string.home_credit_status_healthy)
        progress < 0.8f -> stringResource(Res.string.home_credit_status_warning)
        else -> stringResource(Res.string.home_credit_status_critical)
    }

    val next = state.obligations.firstOrNull { it.isUrgent }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            DukaCredColors.ForestGreen600,
                            DukaCredColors.ForestGreen700
                        )
                    )
                )
                .padding(24.dp)
        ) {

            Text(stringResource(Res.string.home_credit_title), color = DukaCredColors.Cream300)

            Text(
                formatMoney(state.availableCredit.amountCents),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = DukaCredColors.Cream100
            )

            Text(status, color = DukaCredColors.Ochre400, fontSize = 12.sp)

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50)),
                strokeCap = StrokeCap.Round
            )

            if (next != null && next.nextPaymentAmount != null) {
                Spacer(Modifier.height(16.dp))

                Text(stringResource(Res.string.home_suggested_action), color = DukaCredColors.Cream300)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Pay ${formatMoney(next.nextPaymentAmount.amountCents)}",
                        fontWeight = FontWeight.Bold,
                        color = DukaCredColors.Cream100
                    )

                    TextButton(onClick = {}) {
                        Text(stringResource(Res.string.home_pay_now))
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActions(
    onCaptureInvoice: () -> Unit,
    onPay: () -> Unit,
    onHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        DukaCredPrimaryButton(
            text = stringResource(Res.string.home_action_capture),
            onClick = onCaptureInvoice
        )

        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onPay,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(Res.string.home_action_pay))
            }

            Button(
                onClick = onHistory,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(Res.string.home_action_history))
            }
        }
    }
}

@Composable
private fun ObligationsHeader(count: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            stringResource(Res.string.home_obligations_title),
            fontWeight = FontWeight.Bold,
            color = DukaCredColors.Cream100
        )

        Text("$count", color = DukaCredColors.Ochre400)
    }
}


@Composable
private fun ObligationCard(obligation: ObligationUiModel, modifier: Modifier = Modifier) {
    val statusBackground = when {
        obligation.isUrgent   -> DukaCredColors.Error.copy(alpha = 0.15f)
        obligation.isPositive -> DukaCredColors.Success.copy(alpha = 0.12f)
        else                  -> DukaCredColors.ForestGreen700.copy(alpha = 0.4f)
    }
    val statusContent = when {
        obligation.isUrgent   -> DukaCredColors.Error
        obligation.isPositive -> DukaCredColors.Success
        else                  -> DukaCredColors.Ochre400
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            Modifier
                .background(DukaCredColors.ForestGreen800)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    obligation.invoiceReference,
                    fontFamily = DukaCredFonts.soraFamily(),
                    fontWeight = FontWeight.Bold,
                    color = DukaCredColors.Cream100
                )
                StatusChip(
                    label = obligation.statusLabel,
                    backgroundColor = statusBackground as Color,
                    contentColor = statusContent
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                AmountColumn(
                    label = stringResource(Res.string.home_obligation_principal),
                    value = formatMoney(obligation.principalAmount.amountCents)
                )
                obligation.outstandingAmount?.let {
                    AmountColumn(
                        label = stringResource(Res.string.home_obligation_outstanding),
                        value = formatMoney(it.amountCents),
                        valueColor = if (obligation.isUrgent) DukaCredColors.Error else DukaCredColors.Cream200
                    )
                }
                obligation.nextPaymentDueDate?.let {
                    AmountColumn(
                        label = stringResource(Res.string.home_obligation_due_date),
                        value = it,
                        valueColor = if (obligation.isUrgent) DukaCredColors.Error else DukaCredColors.Cream300
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📋", fontSize = 40.sp)
        Text(
            stringResource(Res.string.home_obligations_empty_title),
            fontWeight = FontWeight.Bold
        )
        Text(stringResource(Res.string.home_obligations_empty_subtitle))
    }
}

@Composable
private fun StatusChip(label: String, backgroundColor: Color, contentColor: Color) {
    Surface(shape = RoundedCornerShape(50), color = backgroundColor) {
        Text(text = label, fontFamily = DukaCredFonts.dmSansFamily(), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = contentColor, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
    }
}

@Composable
private fun AmountColumn(label: String, value: String, valueColor: Color = DukaCredColors.Cream200) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = label, fontFamily = DukaCredFonts.dmSansFamily(), fontSize = 11.sp, color = DukaCredColors.Cream300)
        Text(text = value, fontFamily = DukaCredFonts.soraFamily(), fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = valueColor)
    }
}

@Composable
private fun EmptyObligations() {
    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "📋", fontSize = 40.sp)
            Text(text = "No active obligations", fontFamily = DukaCredFonts.soraFamily(), fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = DukaCredColors.Cream200)
        }
    }
}