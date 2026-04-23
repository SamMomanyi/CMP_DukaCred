package com.samduka.dukacred.feature.auth.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samduka.dukacred.core.designsystem.DukaCredColors
import com.samduka.dukacred.core.designsystem.component.DukaCredPrimaryButton
import com.samduka.dukacred.core.designsystem.component.DukaCredTextField
import com.samduka.dukacred.core.designsystem.generated.resources.Res
import com.samduka.dukacred.core.designsystem.generated.resources.merchant_signin_back
import com.samduka.dukacred.core.designsystem.generated.resources.merchant_signin_contact_support
import com.samduka.dukacred.core.designsystem.generated.resources.merchant_signin_cta
import com.samduka.dukacred.core.designsystem.generated.resources.merchant_signin_headline
import com.samduka.dukacred.core.designsystem.generated.resources.merchant_signin_loading
import com.samduka.dukacred.core.designsystem.generated.resources.merchant_signin_no_account
import com.samduka.dukacred.core.designsystem.generated.resources.merchant_signin_phone_label
import com.samduka.dukacred.core.designsystem.generated.resources.merchant_signin_phone_placeholder
import com.samduka.dukacred.core.designsystem.generated.resources.merchant_signin_pin_label
import com.samduka.dukacred.core.designsystem.generated.resources.merchant_signin_pin_placeholder
import com.samduka.dukacred.core.designsystem.generated.resources.merchant_signin_subheading
import com.samduka.dukacred.core.designsystem.generated.resources.role_merchant_title
import com.samduka.dukacred.feature.auth.presentation.action.MerchantSignInAction
import com.samduka.dukacred.feature.auth.presentation.effect.MerchantSignInEffect
import com.samduka.dukacred.feature.auth.presentation.viewmodel.MerchantSignInViewModel
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MerchantSignInScreen(
    onNavigateToMerchantHome: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    viewModel: MerchantSignInViewModel = koinViewModel<MerchantSignInViewModel>(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                MerchantSignInEffect.NavigateToMerchantHome -> onNavigateToMerchantHome()
                MerchantSignInEffect.NavigateBack           -> onNavigateBack()
                MerchantSignInEffect.NavigateToSignUp -> onNavigateToSignUp()
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
                        DukaCredColors.Charcoal800,
                    )
                )
            )
    ) {
        // Ochre glow top right — merchant identity color
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(300.dp)
                .align(Alignment.TopEnd)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            DukaCredColors.Ochre500.copy(alpha = 0.12f),
                            Color.Transparent,
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState()),
        ) {
            // Back button
            IconButton(
                onClick  = { viewModel.onAction(MerchantSignInAction.BackClicked) },
                modifier = Modifier.padding(start = 8.dp, top = 8.dp),
            ) {
                Icon(
                    imageVector        = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(Res.string.merchant_signin_back),
                    tint               = DukaCredColors.Cream200,
                )
            }

            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier.padding(horizontal = 28.dp),
            ) {
                // Role badge
                RoleBadge(
                    text  = stringResource(Res.string.role_merchant_title),
                    color = DukaCredColors.Ochre400,
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text  = stringResource(Res.string.merchant_signin_headline),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        letterSpacing = (-0.5).sp,
                    ),
                    color = DukaCredColors.Cream100,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text  = stringResource(Res.string.merchant_signin_subheading),
                    style = MaterialTheme.typography.bodyLarge,
                    color = DukaCredColors.Cream300,
                )

                Spacer(Modifier.height(48.dp))

                // Phone field
                DukaCredTextField(
                    value         = state.phoneNumber,
                    onValueChange = {
                        viewModel.onAction(MerchantSignInAction.PhoneNumberChanged(it))
                    },
                    label         = stringResource(Res.string.merchant_signin_phone_label),
                    placeholder   = stringResource(Res.string.merchant_signin_phone_placeholder),
                    errorMessage  = state.phoneError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                    ),
                )

                Spacer(Modifier.height(20.dp))

                // PIN field
                DukaCredTextField(
                    value         = state.pin,
                    onValueChange = {
                        viewModel.onAction(MerchantSignInAction.PinChanged(it))
                    },
                    label                = stringResource(Res.string.merchant_signin_pin_label),
                    placeholder          = stringResource(Res.string.merchant_signin_pin_placeholder),
                    errorMessage         = state.pinError,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions      = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                    ),
                )

                // General error
                state.generalError?.let { error ->
                    Spacer(Modifier.height(16.dp))
                    ErrorBanner(message = error)
                }

                Spacer(Modifier.height(36.dp))

                DukaCredPrimaryButton(
                    text       = if (state.isLoading)
                        stringResource(Res.string.merchant_signin_loading)
                    else
                        stringResource(Res.string.merchant_signin_cta),
                    onClick    = { viewModel.onAction(MerchantSignInAction.SignInClicked) },
                    isLoading  = state.isLoading,
                    containerColor = DukaCredColors.Ochre500,
                    contentColor   = DukaCredColors.Cream100,
                )

                Spacer(Modifier.height(32.dp))

                // Support hint
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text  = stringResource(Res.string.merchant_signin_no_account),
                        style = MaterialTheme.typography.bodyMedium,
                        color = DukaCredColors.Cream300,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text  = stringResource(Res.string.merchant_signin_contact_support),
                        style = MaterialTheme.typography.labelLarge,
                        color = DukaCredColors.Ochre400,
                    )
                }

                Spacer(Modifier.height(40.dp))
            }

        }
    }
}