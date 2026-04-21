package com.samduka.dukacred.feature.auth.presentation.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samduka.dukacred.core.designsystem.DukaCredColors
import com.samduka.dukacred.core.designsystem.generated.resources.Res
import com.samduka.dukacred.core.designsystem.generated.resources.app_name
import com.samduka.dukacred.core.designsystem.generated.resources.app_tagline
import com.samduka.dukacred.core.designsystem.generated.resources.role_admin_cta
import com.samduka.dukacred.core.designsystem.generated.resources.role_admin_subtitle
import com.samduka.dukacred.core.designsystem.generated.resources.role_admin_title
import com.samduka.dukacred.core.designsystem.generated.resources.role_merchant_cta
import com.samduka.dukacred.core.designsystem.generated.resources.role_merchant_subtitle
import com.samduka.dukacred.core.designsystem.generated.resources.role_merchant_title
import com.samduka.dukacred.core.designsystem.generated.resources.role_picker_headline
import com.samduka.dukacred.core.designsystem.generated.resources.role_picker_subheading
import com.samduka.dukacred.feature.auth.presentation.action.RolePickerAction
import com.samduka.dukacred.feature.auth.presentation.effect.RolePickerEffect
import com.samduka.dukacred.feature.auth.presentation.viewmodel.RolePickerViewModel
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RolePickerScreen(
    onNavigateToMerchantSignIn: () -> Unit,
    onNavigateToAdminSignIn: () -> Unit,
    onNavigateToMerchantHome: () -> Unit,
    onNavigateToAdminQueue: () -> Unit,
    viewModel: RolePickerViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                RolePickerEffect.NavigateToMerchantSignIn -> onNavigateToMerchantSignIn()
                RolePickerEffect.NavigateToAdminSignIn    -> onNavigateToAdminSignIn()
                RolePickerEffect.NavigateToMerchantHome   -> onNavigateToMerchantHome()
                RolePickerEffect.NavigateToAdminQueue     -> onNavigateToAdminQueue()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DukaCredColors.ForestGreen900,
                        DukaCredColors.ForestGreen800,
                        DukaCredColors.Charcoal800,
                    )
                )
            )
    ) {
        // Decorative diagonal accent top-right
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.TopEnd)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            DukaCredColors.ForestGreen500.copy(alpha = 0.25f),
                            Color.Transparent,
                        )
                    )
                )
        )

        // Decorative bottom-left ochre glow
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomStart)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            DukaCredColors.Ochre500.copy(alpha = 0.15f),
                            Color.Transparent,
                        )
                    )
                )
        )

        if (state.isCheckingSession) {
            CircularProgressIndicator(
                color    = DukaCredColors.ForestGreen400,
                modifier = Modifier.align(Alignment.Center),
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(56.dp))

                // Brand mark
                Text(
                    text  = stringResource(Res.string.app_name),
                    style = MaterialTheme.typography.displayMedium.copy(
                        letterSpacing = (-1).sp,
                    ),
                    color = DukaCredColors.Cream100,
                )

                Spacer(Modifier.height(6.dp))

                // Tagline with green accent dot
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                DukaCredColors.ForestGreen400,
                                RoundedCornerShape(50),
                            )
                    )
                    Text(
                        text  = "  " + stringResource(Res.string.app_tagline),
                        style = MaterialTheme.typography.bodyMedium,
                        color = DukaCredColors.Cream300,
                    )
                }

                Spacer(Modifier.height(64.dp))

                Text(
                    text      = stringResource(Res.string.role_picker_headline),
                    style     = MaterialTheme.typography.headlineLarge,
                    color     = DukaCredColors.Cream100,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text      = stringResource(Res.string.role_picker_subheading),
                    style     = MaterialTheme.typography.bodyLarge,
                    color     = DukaCredColors.Cream300,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(48.dp))

                // Merchant card
                RoleCard(
                    title        = stringResource(Res.string.role_merchant_title),
                    subtitle     = stringResource(Res.string.role_merchant_subtitle),
                    ctaText      = stringResource(Res.string.role_merchant_cta),
                    accentColor  = DukaCredColors.Ochre400,
                    surfaceColor = DukaCredColors.ForestGreen700,
                    emoji        = "🏪",
                    onClick      = {
                        viewModel.onAction(RolePickerAction.SelectMerchant)
                    },
                )

                Spacer(Modifier.height(16.dp))

                // Admin card
                RoleCard(
                    title        = stringResource(Res.string.role_admin_title),
                    subtitle     = stringResource(Res.string.role_admin_subtitle),
                    ctaText      = stringResource(Res.string.role_admin_cta),
                    accentColor  = DukaCredColors.ForestGreen400,
                    surfaceColor = DukaCredColors.ForestGreen700,
                    emoji        = "📊",
                    onClick      = {
                        viewModel.onAction(RolePickerAction.SelectAdmin)
                    },
                )

                Spacer(Modifier.weight(1f))

                Text(
                    text   = "© 2026 DukaCred · Kenya",
                    style  = MaterialTheme.typography.labelSmall,
                    color  = DukaCredColors.Cream300.copy(alpha = 0.5f),
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun RoleCard(
    title: String,
    subtitle: String,
    ctaText: String,
    accentColor: Color,
    surfaceColor: Color,
    emoji: String,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue    = if (isPressed) 0.97f else 1f,
        animationSpec  = tween(120),
        label          = "card_scale",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(surfaceColor)
            .border(
                width = 1.dp,
                color = accentColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp),
            )
            .clickable(
                interactionSource = interactionSource,
                indication        = null,
                onClick           = onClick,
            )
            .padding(24.dp),
    ) {
        // Left accent bar
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(width = 3.dp, height = 48.dp)
                .background(
                    accentColor,
                    RoundedCornerShape(2.dp),
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(text = emoji, style = MaterialTheme.typography.titleLarge)
                Text(
                    text  = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = DukaCredColors.Cream100,
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text  = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = DukaCredColors.Cream300,
            )

            Spacer(Modifier.height(20.dp))

            // CTA pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(
                        width = 1.dp,
                        color = accentColor.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(50.dp),
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text  = ctaText,
                    style = MaterialTheme.typography.labelLarge,
                    color = accentColor,
                )
            }
        }
    }
}