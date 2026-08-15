package com.samduka.dukacred.core.designsystem.components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CurrencyExchange
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samduka.dukacred.core.designsystem.theme.DukaSize
import com.samduka.dukacred.core.designsystem.theme.DukaSpacing
import com.samduka.dukacred.core.designsystem.theme.dukaColors
import com.samduka.dukacred.core.designsystem.theme.dukaShapes
import com.samduka.dukacred.core.designsystem.theme.dukaTypography

// ─────────────────────────────────────────────────────────────────────────
// Buttons
// ─────────────────────────────────────────────────────────────────────────

/**
 * High-contrast primary CTA. Forest-Green fill on light, mint fill on dark
 * (both come from `colorScheme.primary`), 48dp minimum height per spec,
 * built-in loading state that swaps the label for a spinner without
 * reflowing the button.
 */
@Composable
fun DukaPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val shape = MaterialTheme.dukaShapes.small
    val containerColor = MaterialTheme.colorScheme.primary
    val contentColor = MaterialTheme.colorScheme.onPrimary
    val isEnabled = enabled && !loading

    Surface(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = DukaSize.minTouchTarget),
        shape = shape,
        color = if (isEnabled) containerColor else containerColor.copy(alpha = 0.38f),
        contentColor = contentColor,
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = DukaSpacing.md)) {
            AnimatedVisibility(visible = loading, enter = fadeIn(), exit = fadeOut()) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = contentColor,
                    strokeWidth = 2.5.dp,
                )
            }
            AnimatedVisibility(visible = !loading, enter = fadeIn(), exit = fadeOut()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    leadingIcon?.invoke()
                    if (leadingIcon != null) Spacer(Modifier.width(DukaSpacing.base * 2))
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        textAlign = TextAlign.Center,
                    )
                    if (trailingIcon != null) Spacer(Modifier.width(DukaSpacing.base * 2))
                    trailingIcon?.invoke()
                }
            }
        }
    }
}

/** Shortcut for the M-Pesa payment CTA seen on the Financing Hub (Image 11). */
@Composable
fun DukaMpesaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
) {
    DukaPrimaryButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        loading = loading,
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.CurrencyExchange,
                contentDescription = null,
                modifier = Modifier.size(DukaSize.iconMd),
            )
        },
    )
}

/** Outlined secondary action — same footprint as [DukaPrimaryButton], border instead of fill. */
@Composable
fun DukaSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    val shape = MaterialTheme.dukaShapes.small
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = DukaSize.minTouchTarget),
        shape = shape,
        color = MaterialTheme.colorScheme.surface,
        contentColor = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        border = BorderStroke(
            width = DukaSize.borderHairline,
            color = if (enabled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = DukaSpacing.md),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leadingIcon?.invoke()
            if (leadingIcon != null) Spacer(Modifier.width(DukaSpacing.base * 2))
            Text(text = text, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
        }
    }
}

/** Lowest-emphasis text-only action — "Back", "Forgot PIN?", "Edit Details". */
@Composable
fun DukaGhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.dukaShapes.small)
            .clickable(enabled = enabled, onClick = onClick)
            .defaultMinSize(minHeight = DukaSize.minTouchTarget)
            .wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────
// Input field
// ─────────────────────────────────────────────────────────────────────────

/**
 * Boxed input matching the "Business / Duka Name" / "Secure PIN" fields in
 * Images 1–2: persistent (never placeholder-only) label, 1px border that
 * thickens and turns primary on focus, optional leading/trailing icon slot,
 * and an inline error state.
 */
@Composable
fun DukaInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorText: String? = null,
    supportingText: String? = null,
    isPassword: Boolean = false,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val borderColor = when {
        isError -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outlineVariant
    }
    val focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = DukaSpacing.base),
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = singleLine,
            isError = isError,
            keyboardOptions = keyboardOptions,
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            placeholder = placeholder?.let {
                { Text(it, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) }
            },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon ?: if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                            contentDescription = if (passwordVisible) "Hide" else "Show",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else null,
            textStyle = MaterialTheme.typography.bodyLarge,
            shape = MaterialTheme.dukaShapes.medium,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f),
                errorContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                focusedIndicatorColor = focusedBorderColor,
                unfocusedIndicatorColor = borderColor,
                errorIndicatorColor = MaterialTheme.colorScheme.error,
                cursorColor = MaterialTheme.colorScheme.primary,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = DukaSize.minTouchTarget),
        )
        val caption = if (isError) errorText else supportingText
        AnimatedVisibility(visible = caption != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = DukaSpacing.base, start = DukaSpacing.base),
            ) {
                if (isError) {
                    Icon(
                        imageVector = Icons.Rounded.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(DukaSize.iconXs),
                    )
                    Spacer(Modifier.width(DukaSpacing.base))
                }
                Text(
                    text = caption.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
// Surface card
// ─────────────────────────────────────────────────────────────────────────

/** Tonal elevation step for [DukaSurfaceCard] — mirrors "Elevation & Depth" § levels 1–3. */
enum class DukaCardElevation { Level1, Level2, Level3 }

/**
 * Base card container: tonal `surfaceContainer*` fill, hairline 1px border,
 * 8px corner radius. This is the building block behind every "Header-Body-
 * Footer" data card in the spec (dashboard hero, insights, settled-loan
 * rows, verification panel).
 */
@Composable
fun DukaSurfaceCard(
    modifier: Modifier = Modifier,
    elevation: DukaCardElevation = DukaCardElevation.Level1,
    shape: Shape = MaterialTheme.dukaShapes.large,
    showBorder: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(DukaSpacing.md),
    onClick: (() -> Unit)? = null,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    val containerColor = when (elevation) {
        DukaCardElevation.Level1 -> MaterialTheme.colorScheme.surfaceContainerLow
        DukaCardElevation.Level2 -> MaterialTheme.colorScheme.surfaceContainer
        DukaCardElevation.Level3 -> MaterialTheme.colorScheme.surfaceContainerHigh
    }
    val borderColor = MaterialTheme.dukaColors.hairline

    val cardModifier = modifier
        .clip(shape)
        .background(containerColor, shape)
        .let { if (showBorder) it.border(DukaSize.borderHairline, borderColor, shape) else it }
        .let { if (onClick != null) it.clickable(onClick = onClick) else it }
        .padding(contentPadding)

    Column(modifier = cardModifier, content = content)
}

// ─────────────────────────────────────────────────────────────────────────
// Status badge / chip
// ─────────────────────────────────────────────────────────────────────────

/** Semantic tone for [DukaStatusBadge] — Success (Forest/Emerald), Warning (Ochre/Gold), Neutral, Error. */
enum class DukaStatusTone { Success, Warning, Neutral, Error }

/**
 * Small rounded-`sm` badge — "VERIFIED" / "SUCCESS" chips on the dashboard
 * activity feed (Image 9), "High Sales Velocity" / "Fast Mover" tags on the
 * Smart Adjustment screen (Image 7).
 */
@Composable
fun DukaStatusBadge(
    text: String,
    tone: DukaStatusTone,
    modifier: Modifier = Modifier,
    showLeadingIcon: Boolean = false,
) {
    val colors = MaterialTheme.dukaColors
    val (bg, fg, icon) = when (tone) {
        DukaStatusTone.Success -> Triple(colors.successBg, colors.successOn, Icons.Rounded.CheckCircle)
        DukaStatusTone.Warning -> Triple(colors.warningBg, colors.warningOn, Icons.Rounded.Warning)
        DukaStatusTone.Neutral -> Triple(colors.neutralBg, colors.neutralOn, Icons.Rounded.Info)
        DukaStatusTone.Error -> Triple(colors.errorBg, colors.errorOn, Icons.Rounded.ErrorOutline)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.dukaShapes.small)
            .background(bg)
            .defaultMinSize(minHeight = DukaSize.statusBadgeHeight)
            .padding(horizontal = DukaSpacing.xs, vertical = DukaSpacing.base),
    ) {
        if (showLeadingIcon) {
            Icon(imageVector = icon, contentDescription = null, tint = fg, modifier = Modifier.size(DukaSize.iconXs))
            Spacer(Modifier.width(DukaSpacing.base))
        }
        Text(
            text = text,
            style = MaterialTheme.dukaTypography.labelCaps,
            color = fg,
        )
    }
}

/** Alias — interchangeable with [DukaStatusBadge]; kept as a distinct name for call-site clarity on filter/tag chips. */
@Composable
fun DukaChip(
    text: String,
    tone: DukaStatusTone,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val base = modifier.let { if (onClick != null) it.clickable(onClick = onClick) else it }
    DukaStatusBadge(text = text, tone = tone, modifier = base, showLeadingIcon = selected)
}

// ─────────────────────────────────────────────────────────────────────────
// Bottom navigation
// ─────────────────────────────────────────────────────────────────────────

/** One tab in [DukaBottomNavBar] — Dashboard / Invoices / Financing / Account (Images 9, 11). */
data class DukaBottomNavItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector = icon,
)

/**
 * Fixed four-tab bottom bar with an animated pill indicator behind the
 * active icon+label, matching the "Dashboard / Invoices / Financing /
 * Account" bar visible in Images 9 and 11.
 */
@Composable
fun DukaBottomNavBar(
    items: List<DukaBottomNavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(DukaSize.bottomNavHeight)
                .padding(horizontal = DukaSpacing.xs, vertical = DukaSpacing.base),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { index, item ->
                val selected = index == selectedIndex
                val contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(MaterialTheme.dukaShapes.medium)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { onItemSelected(index) }
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent,
                            MaterialTheme.dukaShapes.medium,
                        )
                        .padding(vertical = DukaSpacing.base),
                ) {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.icon,
                        contentDescription = item.label,
                        tint = contentColor,
                        modifier = Modifier.size(DukaSize.iconLg),
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
// Metric card (trust score / credit limit hero)
// ─────────────────────────────────────────────────────────────────────────

/**
 * Dark hero card from the dashboard (Image 9): "Credit Limit KES 50,000"
 * with a circular trust-score progress ring and a "View Details" CTA.
 * Renders on `primaryContainer` regardless of app theme, since the spec
 * treats this card as an always-dark "vault" surface for emphasis.
 */
@Composable
fun DukaMetricCard(
    label: String,
    valueText: String,
    progressPercent: Int,
    progressLabel: String,
    ctaText: String,
    onCtaClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ringTrack = Color.White.copy(alpha = 0.16f)
    val vaultBackground = Color(0xFF013220) // brandPrimaryAnchor — stays dark in both themes
    val ringProgress = Color(0xFFA2D1B7)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.dukaShapes.large)
            .background(vaultBackground, MaterialTheme.dukaShapes.large)
            .padding(DukaSpacing.md),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.dukaTypography.labelCaps,
                    color = Color(0xFFA2D1B7),
                )
                Spacer(Modifier.height(DukaSpacing.base))
                Text(
                    text = valueText,
                    style = MaterialTheme.dukaTypography.display.copy(fontSize = 30.sp, lineHeight = 36.sp),
                    color = Color.White,
                )
                Spacer(Modifier.height(DukaSpacing.sm))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(MaterialTheme.dukaShapes.full)
                        .background(Color(0xFFA2D1B7))
                        .clickable(onClick = onCtaClick)
                        .padding(horizontal = DukaSpacing.sm, vertical = DukaSpacing.base),
                ) {
                    Text(
                        text = ctaText,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF013220),
                    )
                }
            }
            Spacer(Modifier.width(DukaSpacing.md))
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(DukaSize.metricRingSize)) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(DukaSize.metricRingSize)) {
                    val stroke = DukaSize.metricRingStroke.toPx()
                    drawArc(
                        color = ringTrack,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round),
                    )
                    drawArc(
                        color = ringProgress,
                        startAngle = -90f,
                        sweepAngle = 360f * (progressPercent.coerceIn(0, 100) / 100f),
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round),
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$progressPercent%",
                        style = MaterialTheme.dukaTypography.dataTabular,
                        color = Color.White,
                    )
                    Text(
                        text = progressLabel,
                        style = MaterialTheme.dukaTypography.labelCaps.copy(fontSize = 8.sp, lineHeight = 10.sp),
                        color = Color(0xFFA2D1B7),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
// Transaction row
// ─────────────────────────────────────────────────────────────────────────

/**
 * High-density ledger row: leading circular icon, title + date/subtitle
 * column, trailing tabular-nums amount (+ optional status badge under the
 * title). Matches "Recent Activity" (Image 9) and "Settled Loans" (Image 11)
 * rows.
 */
@Composable
fun DukaTransactionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    amountText: String,
    modifier: Modifier = Modifier,
    statusText: String? = null,
    statusTone: DukaStatusTone = DukaStatusTone.Success,
    isPositiveAmount: Boolean = false,
    iconBackground: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(vertical = DukaSpacing.sm),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(DukaSize.avatarMd)
                .clip(MaterialTheme.dukaShapes.medium)
                .background(iconBackground),
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(DukaSize.iconLg))
        }
        Spacer(Modifier.width(DukaSpacing.sm))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(2.dp))
            if (statusText != null) {
                DukaStatusBadge(text = statusText, tone = statusTone)
            } else {
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.width(DukaSpacing.sm))
        Text(
            text = amountText,
            style = MaterialTheme.dukaTypography.dataTabular,
            color = if (isPositiveAmount) MaterialTheme.dukaColors.successOn else MaterialTheme.colorScheme.onSurface,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────
// Skeleton loader
// ─────────────────────────────────────────────────────────────────────────

/**
 * Animated shimmer brush for AI-processing skeleton states (Images 3, 5, 6:
 * "Reading Supplier Data" / "Analyzing Invoice"). Apply as a background in
 * place of real content:
 *
 * ```kotlin
 * Box(Modifier.fillMaxWidth().height(16.dp).dukaSkeletonBrush(shape = MaterialTheme.dukaShapes.small))
 * ```
 */
@Composable
fun Modifier.dukaSkeletonBrush(
    shape: Shape = RoundedCornerShape(4.dp),
): Modifier {
    val base = MaterialTheme.dukaColors.skeletonBase
    val highlight = MaterialTheme.dukaColors.skeletonHighlight
    val transition = rememberInfiniteTransition(label = "duka-skeleton")
    val translate by transition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "duka-skeleton-translate",
    )
    return this
        .clip(shape)
        .drawWithCache {
            val brush = Brush.linearGradient(
                colors = listOf(base, highlight, base),
                start = Offset(translate - 400f, 0f),
                end = Offset(translate + 400f, size.height),
            )
            onDrawBehind { drawRect(brush = brush, size = size) }
        }
}

/** Convenience skeleton block — a plain shimmering box, sized by the caller. */
@Composable
fun DukaSkeletonBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
) {
    Box(modifier = modifier.dukaSkeletonBrush(shape = shape))
}
