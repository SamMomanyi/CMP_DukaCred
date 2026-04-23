package com.samduka.dukacred.feature.auth.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samduka.dukacred.core.designsystem.DukaCredColors
import com.samduka.dukacred.core.designsystem.component.DukaCredPrimaryButton
import com.samduka.dukacred.core.designsystem.component.DukaCredTextField
import com.samduka.dukacred.core.designsystem.generated.resources.Res
import com.samduka.dukacred.core.designsystem.generated.resources.admin_signin_back
import com.samduka.dukacred.core.designsystem.generated.resources.admin_signin_cta
import com.samduka.dukacred.core.designsystem.generated.resources.admin_signin_email_label
import com.samduka.dukacred.core.designsystem.generated.resources.admin_signin_email_placeholder
import com.samduka.dukacred.core.designsystem.generated.resources.admin_signin_forgot_password
import com.samduka.dukacred.core.designsystem.generated.resources.admin_signin_headline
import com.samduka.dukacred.core.designsystem.generated.resources.admin_signin_hide_password
import com.samduka.dukacred.core.designsystem.generated.resources.admin_signin_loading
import com.samduka.dukacred.core.designsystem.generated.resources.admin_signin_password_label
import com.samduka.dukacred.core.designsystem.generated.resources.admin_signin_password_placeholder
import com.samduka.dukacred.core.designsystem.generated.resources.admin_signin_show_password
import com.samduka.dukacred.core.designsystem.generated.resources.admin_signin_subheading
import com.samduka.dukacred.core.designsystem.generated.resources.role_admin_title
import com.samduka.dukacred.feature.auth.presentation.action.AdminSignInAction
import com.samduka.dukacred.feature.auth.presentation.effect.AdminSignInEffect
import com.samduka.dukacred.feature.auth.presentation.viewmodel.AdminSignInViewModel
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AdminSignInScreen(
    onNavigateToAdminQueue: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AdminSignInViewModel = koinViewModel<AdminSignInViewModel>(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                AdminSignInEffect.NavigateToAdminQueue -> onNavigateToAdminQueue()
                AdminSignInEffect.NavigateBack         -> onNavigateBack()
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
        // Green glow top-right — admin identity color
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(300.dp)
                .align(Alignment.TopEnd)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            DukaCredColors.ForestGreen400.copy(alpha = 0.15f),
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
            IconButton(
                onClick  = { viewModel.onAction(AdminSignInAction.BackClicked) },
                modifier = Modifier.padding(start = 8.dp, top = 8.dp),
            ) {
                Icon(
                    imageVector        = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(Res.string.admin_signin_back),
                    tint               = DukaCredColors.Cream200,
                )
            }

            Spacer(Modifier.height(24.dp))

            Column(modifier = Modifier.padding(horizontal = 28.dp)) {

                RoleBadge(
                    text  = stringResource(Res.string.role_admin_title),
                    color = DukaCredColors.ForestGreen400,
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text  = stringResource(Res.string.admin_signin_headline),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        letterSpacing = (-0.5).sp,
                    ),
                    color = DukaCredColors.Cream100,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text  = stringResource(Res.string.admin_signin_subheading),
                    style = MaterialTheme.typography.bodyLarge,
                    color = DukaCredColors.Cream300,
                )

                Spacer(Modifier.height(48.dp))

                DukaCredTextField(
                    value         = state.email,
                    onValueChange = {
                        viewModel.onAction(AdminSignInAction.EmailChanged(it))
                    },
                    label         = stringResource(Res.string.admin_signin_email_label),
                    placeholder   = stringResource(Res.string.admin_signin_email_placeholder),
                    errorMessage  = state.emailError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                    ),
                )

                Spacer(Modifier.height(20.dp))

                DukaCredTextField(
                    value         = state.password,
                    onValueChange = {
                        viewModel.onAction(AdminSignInAction.PasswordChanged(it))
                    },
                    label                = stringResource(Res.string.admin_signin_password_label),
                    placeholder          = stringResource(Res.string.admin_signin_password_placeholder),
                    errorMessage         = state.passwordError,
                    visualTransformation = if (state.isPasswordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    keyboardOptions      = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                viewModel.onAction(AdminSignInAction.TogglePasswordVisibility)
                            }
                        ) {
                            Icon(
                                imageVector = if (state.isPasswordVisible)
                                    Icons.Rounded.VisibilityOff
                                else
                                    Icons.Rounded.Visibility,
                                contentDescription = stringResource(
                                    if (state.isPasswordVisible)
                                        Res.string.admin_signin_hide_password
                                    else
                                        Res.string.admin_signin_show_password
                                ),
                                tint = DukaCredColors.Cream300,
                            )
                        }
                    },
                )

                state.generalError?.let { error ->
                    Spacer(Modifier.height(16.dp))
                    ErrorBanner(message = error)
                }

                Spacer(Modifier.height(12.dp))

                // Forgot password — right aligned
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { /* TODO: forgot password flow */ }) {
                        Text(
                            text  = stringResource(Res.string.admin_signin_forgot_password),
                            style = MaterialTheme.typography.labelLarge,
                            color = DukaCredColors.ForestGreen400,
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                DukaCredPrimaryButton(
                    text      = if (state.isLoading)
                        stringResource(Res.string.admin_signin_loading)
                    else
                        stringResource(Res.string.admin_signin_cta),
                    onClick   = { viewModel.onAction(AdminSignInAction.SignInClicked) },
                    isLoading = state.isLoading,
                )

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}