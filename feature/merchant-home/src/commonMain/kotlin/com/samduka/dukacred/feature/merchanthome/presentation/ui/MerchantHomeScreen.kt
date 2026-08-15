package com.samduka.dukacred.feature.merchanthome.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samduka.dukacred.core.designsystem.generated.resources.Res
import com.samduka.dukacred.core.designsystem.generated.resources.*
import com.samduka.dukacred.core.designsystem.theme.DukaCredFonts
import com.samduka.dukacred.core.designsystem.theme.dukaColors
import com.samduka.dukacred.core.designsystem.components.DukaBottomNavBar
import com.samduka.dukacred.core.designsystem.components.DukaBottomNavItem
import com.samduka.dukacred.core.designsystem.components.DukaPrimaryButton
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

// FIND
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MerchantHomeScreen(
    state: MerchantHomeState = MerchantHomeState.fakeState,
    onCaptureInvoice: () -> Unit = {},
    onPay: () -> Unit = {},
    onHistory: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onRefresh: () -> Unit = { println("TODO: Refreshing data") },
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) { innerPadding ->

        // REPLACE
        @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
        @Composable
        fun MerchantHomeScreen(
            state: MerchantHomeState = MerchantHomeState.fakeState,
            selectedNavIndex: Int = 0,
            onNavItemSelected: (Int) -> Unit = {},
            onCaptureInvoice: () -> Unit = {},
            onPay: () -> Unit = {},
            onHistory: () -> Unit = {},
            onNotifications: () -> Unit = {},
            onRefresh: () -> Unit = { println("TODO: Refreshing data") },
        ) {
            val navItems = remember {
                listOf(
                    DukaBottomNavItem(label = "Dashboard", icon = Icons.Filled.Home),
                    DukaBottomNavItem(label = "Invoices", icon = Icons.Filled.Description),
                    DukaBottomNavItem(
                        label = "Financing",
                        icon = Icons.Filled.AccountBalanceWallet
                    ),
                    DukaBottomNavItem(label = "Account", icon = Icons.Filled.Person),
                )
            }

            Scaffold(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                bottomBar = {
                    DukaBottomNavBar(
                        items = navItems,
                        selectedIndex = selectedNavIndex,
                        onItemSelected = onNavItemSelected,
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = onCaptureInvoice,
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = stringResource(Res.string.home_action_capture),
                        )
                    }
                },
            ) { innerPadding ->
                PullToRefreshBox(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    isRefreshing = state.isRefreshing,
                    onRefresh = onRefresh,
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
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
                            CashflowOverviewCard(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = merchantName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(Res.string.home_subtitle),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Icon(
                    Icons.Filled.Notifications,
                    contentDescription = stringResource(Res.string.home_notifications),
                    tint = MaterialTheme.colorScheme.tertiary
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
                            MaterialTheme.colorScheme.outline,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                )
                .padding(24.dp)
        ) {

            Text(stringResource(Res.string.home_credit_title), color = MaterialTheme.colorScheme.onSurfaceVariant)

            Text(
                formatMoney(state.availableCredit.amountCents),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Text(status, color = MaterialTheme.colorScheme.tertiary, fontSize = 12.sp)

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

                Text(stringResource(Res.string.home_suggested_action), color = MaterialTheme.colorScheme.onSurfaceVariant)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Pay ${formatMoney(next.nextPaymentAmount.amountCents)}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
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

        DukaPrimaryButton(
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
            color = MaterialTheme.colorScheme.onPrimary
        )

        Text("$count", color = MaterialTheme.colorScheme.tertiary)
    }
}


@Composable
private fun ObligationCard(obligation: ObligationUiModel, modifier: Modifier = Modifier) {
    val statusBackground = when {
        obligation.isUrgent   -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
        obligation.isPositive -> MaterialTheme.dukaColors.successOn.copy(alpha = 0.12f)
        else                  -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    }
    val statusContent = when {
        obligation.isUrgent   -> MaterialTheme.colorScheme.error
        obligation.isPositive -> MaterialTheme.dukaColors.successOn
        else                  -> MaterialTheme.colorScheme.tertiary
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
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
                    color = MaterialTheme.colorScheme.onPrimary
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
                        valueColor = if (obligation.isUrgent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }
                obligation.nextPaymentDueDate?.let {
                    AmountColumn(
                        label = stringResource(Res.string.home_obligation_due_date),
                        value = it,
                        valueColor = if (obligation.isUrgent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
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
private fun AmountColumn(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = label, fontFamily = DukaCredFonts.dmSansFamily(), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontFamily = DukaCredFonts.soraFamily(), fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = valueColor)
    }
}

@Composable
private fun EmptyObligations() {
    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "📋", fontSize = 40.sp)
            Text(text = "No active obligations", fontFamily = DukaCredFonts.soraFamily(), fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
// ─────────────────────────────────────────────────────────────────────────
// Cashflow Overview
// ─────────────────────────────────────────────────────────────────────────

private enum class CashflowPeriod(val label: String) { TODAY("Today"), WEEK("Week"), MONTH("Month") }

private data class CashflowPoint(val label: String, val salesCents: Long, val expensesCents: Long)

// Placeholder series per period — swap for a real field on MerchantHomeState
// once the backend exposes a sales/expenses breakdown. Not wired to the
// ViewModel yet; flagging rather than inventing that data contract silently.
private fun placeholderCashflow(period: CashflowPeriod): List<CashflowPoint> = when (period) {
    CashflowPeriod.TODAY -> listOf(
        CashflowPoint("9a", 4_200_00, 1_100_00),
        CashflowPoint("12p", 7_800_00, 2_300_00),
        CashflowPoint("3p", 5_100_00, 900_00),
        CashflowPoint("6p", 9_400_00, 3_200_00),
    )
    CashflowPeriod.WEEK -> listOf(
        CashflowPoint("Mon", 22_000_00, 8_000_00),
        CashflowPoint("Tue", 18_500_00, 6_200_00),
        CashflowPoint("Wed", 25_100_00, 9_400_00),
        CashflowPoint("Thu", 19_800_00, 7_100_00),
        CashflowPoint("Fri", 31_200_00, 11_000_00),
        CashflowPoint("Sat", 34_600_00, 12_500_00),
        CashflowPoint("Sun", 15_200_00, 5_800_00),
    )
    CashflowPeriod.MONTH -> listOf(
        CashflowPoint("W1", 142_000_00, 51_000_00),
        CashflowPoint("W2", 158_000_00, 60_000_00),
        CashflowPoint("W3", 133_000_00, 47_000_00),
        CashflowPoint("W4", 171_000_00, 65_000_00),
    )
}

@Composable
private fun CashflowOverviewCard(modifier: Modifier = Modifier) {
    var selectedPeriod by remember { mutableStateOf(CashflowPeriod.WEEK) }
    val points = remember(selectedPeriod) { placeholderCashflow(selectedPeriod) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.home_cashflow_title),
                    fontFamily = DukaCredFonts.soraFamily(),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LegendDot(color = MaterialTheme.dukaColors.chartSales, label = "Sales")
                    Spacer(Modifier.width(10.dp))
                    LegendDot(color = MaterialTheme.dukaColors.chartExpenses, label = "Expenses")
                }
            }

            Spacer(Modifier.height(14.dp))

            CashflowSegmentedControl(
                selected = selectedPeriod,
                onSelect = { selectedPeriod = it },
            )

            Spacer(Modifier.height(20.dp))

            CashflowBarChart(
                points = points,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
            )
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun CashflowSegmentedControl(
    selected: CashflowPeriod,
    onSelect: (CashflowPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(4.dp),
    ) {
        CashflowPeriod.entries.forEach { period ->
            val isSelected = period == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onSelect(period) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = period.label,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun CashflowBarChart(points: List<CashflowPoint>, modifier: Modifier = Modifier) {
    val salesColor = MaterialTheme.dukaColors.chartSales
    val expensesColor = MaterialTheme.dukaColors.chartExpenses
    val gridColor = MaterialTheme.dukaColors.chartGridLine
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    val maxValue = remember(points) {
        (points.maxOfOrNull { maxOf(it.salesCents, it.expensesCents) } ?: 1L).coerceAtLeast(1L)
    }

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val groupWidth = size.width / points.size
            val barWidth = groupWidth * 0.28f
            val gap = groupWidth * 0.06f

            repeat(3) { i ->
                val y = size.height * (i / 3f)
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx(),
                )
            }

            points.forEachIndexed { index, point ->
                val groupStart = index * groupWidth
                val salesHeight = size.height * (point.salesCents.toFloat() / maxValue)
                val expensesHeight = size.height * (point.expensesCents.toFloat() / maxValue)

                drawRoundRect(
                    color = salesColor,
                    topLeft = Offset(groupStart + gap, size.height - salesHeight),
                    size = Size(barWidth, salesHeight),
                    cornerRadius = CornerRadius(3.dp.toPx()),
                )
                drawRoundRect(
                    color = expensesColor,
                    topLeft = Offset(groupStart + gap + barWidth + gap, size.height - expensesHeight),
                    size = Size(barWidth, expensesHeight),
                    cornerRadius = CornerRadius(3.dp.toPx()),
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            points.forEach { point -> Text(point.label, fontSize = 10.sp, color = labelColor) }
        }
    }
}