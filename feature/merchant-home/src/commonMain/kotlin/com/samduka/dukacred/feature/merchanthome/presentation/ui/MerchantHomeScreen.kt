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
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samduka.dukacred.core.designsystem.DukaCredColors
import com.samduka.dukacred.core.designsystem.DukaCredFonts
import com.samduka.dukacred.core.designsystem.component.DukaCredPrimaryButton
import com.samduka.dukacred.feature.merchanthome.presentation.state.MerchantHomeState
import com.samduka.dukacred.feature.merchanthome.presentation.state.ObligationUiModel

private fun formatMoney(amountCents: Long, currency: String = "KES"): String {
    val amount = amountCents / 100.0
    // Using a simple fallback formatter since java.text isn't fully supported in pure KMP commonMain without expect/actual
    val amountString = amount.toString()
    val padded = if (amountString.substringAfter(".").length == 1) "${amountString}0" else amountString
    return "$currency $padded"
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
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            item {
                DashboardHeader(
                    merchantName = state.merchantName,
                    onNotificationsClick = onNotifications,
                )
            }

            item {
                CreditHeroCard(
                    state = state,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                )
            }

            item {
                QuickActionBar(
                    onCaptureInvoice = onCaptureInvoice,
                    onPay = onPay,
                    onHistory = onHistory,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                )
            }

            item {
                ObligationsSectionHeader(
                    count = state.obligations.size,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                )
            }

            if (state.obligations.isEmpty()) {
                item { EmptyObligations() }
            } else {
                items(state.obligations, key = { it.id }) { obligation ->
                    ObligationCard(
                        obligation = obligation,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    merchantName: String,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Morning,",
                fontFamily = DukaCredFonts.dmSansFamily(),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = DukaCredColors.Cream300,
            )
            Text(
                text = merchantName,
                fontFamily = DukaCredFonts.soraFamily(),
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = DukaCredColors.Cream100,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Box {
            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(DukaCredColors.ForestGreen800),
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = DukaCredColors.Ochre400,
                    modifier = Modifier.size(22.dp),
                )
            }
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(DukaCredColors.Error)
                    .align(Alignment.TopEnd)
                    .offset(x = (-6).dp, y = 6.dp),
            )
        }
    }
}

@Composable
private fun CreditHeroCard(
    state: MerchantHomeState,
    modifier: Modifier = Modifier,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = state.creditUsageFraction,
        animationSpec = tween(durationMillis = 900),
        label = "creditProgress",
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            DukaCredColors.ForestGreen600,
                            DukaCredColors.ForestGreen700
                        )
                    )
                )
                .padding(24.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Text(
                    text = "Available Credit",
                    fontFamily = DukaCredFonts.dmSansFamily(),
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = DukaCredColors.Cream300,
                )

                Text(
                    text = formatMoney(state.availableCredit.amountCents, state.availableCredit.currency),
                    fontFamily = DukaCredFonts.soraFamily(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 34.sp,
                    color = DukaCredColors.Cream100,
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Used: ${formatMoney(state.totalCreditLimit.amountCents - state.availableCredit.amountCents, state.availableCredit.currency)}",
                            fontFamily = DukaCredFonts.dmSansFamily(),
                            fontSize = 12.sp,
                            color = DukaCredColors.Cream300,
                        )
                        Text(
                            text = "Limit: ${formatMoney(state.totalCreditLimit.amountCents, state.totalCreditLimit.currency)}",
                            fontFamily = DukaCredFonts.dmSansFamily(),
                            fontSize = 12.sp,
                            color = DukaCredColors.Cream300,
                        )
                    }

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(50)),
                        color = if (animatedProgress > 0.8f) DukaCredColors.Error else DukaCredColors.Ochre400,
                        trackColor = DukaCredColors.BlackAlpha40,
                        strokeCap = StrokeCap.Round,
                    )
                }

                HorizontalDivider(color = DukaCredColors.Cream300.copy(alpha = 0.2f), thickness = 1.dp)

                val nextObligation = state.obligations.firstOrNull { it.nextPaymentAmount != null && !it.isUrgent } ?: state.obligations.firstOrNull { it.nextPaymentAmount != null }

                if (nextObligation != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = "Next Payment",
                                fontFamily = DukaCredFonts.dmSansFamily(),
                                fontSize = 12.sp,
                                color = DukaCredColors.Cream300,
                            )
                            Text(
                                text = nextObligation.nextPaymentDueDate ?: "—",
                                fontFamily = DukaCredFonts.dmSansFamily(),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = DukaCredColors.Cream100,
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = DukaCredColors.Charcoal800.copy(alpha = 0.4f),
                        ) {
                            Text(
                                text = formatMoney(nextObligation.nextPaymentAmount!!.amountCents, nextObligation.nextPaymentAmount.currency),
                                fontFamily = DukaCredFonts.soraFamily(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = DukaCredColors.Cream200,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionBar(
    onCaptureInvoice: () -> Unit,
    onPay: () -> Unit,
    onHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DukaCredPrimaryButton(
            text = "Capture Invoice",
            onClick = onCaptureInvoice,
            containerColor = DukaCredColors.Ochre500,
            contentColor = DukaCredColors.Cream100,
            modifier = Modifier.weight(1f).height(52.dp)
        )

        QuickSecondaryAction(icon = Icons.Outlined.Payments, label = "Pay", onClick = onPay)
        QuickSecondaryAction(icon = Icons.Outlined.History, label = "History", onClick = onHistory)
    }
}

@Composable
private fun QuickSecondaryAction(icon: ImageVector, label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .height(52.dp)
            .widthIn(min = 72.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = DukaCredColors.ForestGreen800, contentColor = DukaCredColors.Cream200),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(20.dp))
            Text(text = label, fontFamily = DukaCredFonts.dmSansFamily(), fontWeight = FontWeight.SemiBold, fontSize = 10.sp)
        }
    }
}

@Composable
private fun ObligationsSectionHeader(count: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Active Obligations",
            fontFamily = DukaCredFonts.soraFamily(),
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = DukaCredColors.Cream100,
        )
        Surface(shape = CircleShape, color = DukaCredColors.ForestGreen800) {
            Text(
                text = "$count",
                fontFamily = DukaCredFonts.soraFamily(),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = DukaCredColors.Ochre400,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
            )
        }
    }
}

@Composable
private fun ObligationCard(obligation: ObligationUiModel, modifier: Modifier = Modifier) {
    val chipBackground = when {
        obligation.isPositive -> DukaCredColors.Success.copy(alpha = 0.15f)
        obligation.isUrgent   -> DukaCredColors.Error.copy(alpha = 0.15f)
        else                  -> DukaCredColors.Warning.copy(alpha = 0.15f)
    }
    val chipContent = when {
        obligation.isPositive -> DukaCredColors.Success
        obligation.isUrgent   -> DukaCredColors.Error
        else                  -> DukaCredColors.Warning
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DukaCredColors.ForestGreen800),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = obligation.invoiceReference,
                    fontFamily = DukaCredFonts.soraFamily(),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = DukaCredColors.Cream100,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(Modifier.width(8.dp))
                StatusChip(label = obligation.statusLabel, backgroundColor = chipBackground, contentColor = chipContent)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                AmountColumn(label = "Principal", value = formatMoney(obligation.principalAmount.amountCents))
                if (obligation.outstandingAmount != null) {
                    AmountColumn(label = "Outstanding", value = formatMoney(obligation.outstandingAmount.amountCents), valueColor = if (obligation.isUrgent) DukaCredColors.Error else DukaCredColors.Cream200)
                }
            }
        }
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