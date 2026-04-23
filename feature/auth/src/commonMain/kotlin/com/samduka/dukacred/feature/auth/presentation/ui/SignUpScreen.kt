package com.samduka.dukacred.feature.auth.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.samduka.dukacred.core.designsystem.DukaCredColors
import com.samduka.dukacred.core.designsystem.component.DukaCredPrimaryButton
import com.samduka.dukacred.core.designsystem.component.DukaCredTextField
import com.samduka.dukacred.core.domain.model.UserRole
import com.samduka.dukacred.feature.auth.presentation.action.SignUpAction
import com.samduka.dukacred.feature.auth.presentation.effect.MerchantSignInEffect
import com.samduka.dukacred.feature.auth.presentation.effect.SignUpEffect
import com.samduka.dukacred.feature.auth.presentation.viewmodel.SignUpViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignUpScreen(
    onNavigateToMerchantHome: () -> Unit,
    onNavigateToAdminQueue: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SignUpViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                SignUpEffect.NavigateToMerchantHome -> onNavigateToMerchantHome()
                SignUpEffect.NavigateToAdminQueue   -> onNavigateToAdminQueue()
                SignUpEffect.NavigateBack           -> onNavigateBack()

            }
        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DukaCredColors.ForestGreen900)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
        ) {
            Spacer(Modifier.height(16.dp))
            IconButton(onClick = { viewModel.onAction(SignUpAction.BackClicked) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = DukaCredColors.Cream100,
                )
            }
            Spacer(Modifier.height(24.dp))
            Text("Create Account", style = MaterialTheme.typography.headlineLarge, color = DukaCredColors.Cream100)
            Spacer(Modifier.height(8.dp))
            Text("Set up your DukaCred account", style = MaterialTheme.typography.bodyLarge, color = DukaCredColors.Cream300)
            Spacer(Modifier.height(32.dp))

            // Role toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                UserRole.entries.forEach { role ->
                    val selected = state.selectedRole == role
                    val accent = if (role == UserRole.MERCHANT) DukaCredColors.Ochre400 else DukaCredColors.ForestGreen400
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.onAction(SignUpAction.RoleSelected(role)) },
                        label = { Text(role.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accent.copy(alpha = 0.2f),
                            selectedLabelColor = accent,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            DukaCredTextField(
                value = state.email,
                onValueChange = { viewModel.onAction(SignUpAction.EmailChanged(it)) },
                label = "Email",
                errorMessage = state.emailError,
            )
            Spacer(Modifier.height(16.dp))
            DukaCredTextField(
                value = state.password,
                onValueChange = { viewModel.onAction(SignUpAction.PasswordChanged(it)) },
                label = "Password",
                errorMessage = state.passwordError,
                visualTransformation = if (state.isPasswordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { viewModel.onAction(SignUpAction.TogglePasswordVisibility) }) {
                        Icon(
                            imageVector = if (state.isPasswordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                            contentDescription = null,
                            tint = DukaCredColors.Cream300,
                        )
                    }
                },
            )
            Spacer(Modifier.height(16.dp))
            DukaCredTextField(
                value = state.confirmPassword,
                onValueChange = { viewModel.onAction(SignUpAction.ConfirmPasswordChanged(it)) },
                label = "Confirm Password",
                visualTransformation = if (state.isPasswordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
            )

            state.generalError?.let {
                Spacer(Modifier.height(12.dp))
                // reuse ErrorBanner from AuthComponents
                ErrorBanner(message = it)
            }

            Spacer(Modifier.height(32.dp))

            val accent = if (state.selectedRole == UserRole.MERCHANT) DukaCredColors.Ochre400 else DukaCredColors.ForestGreen400
            DukaCredPrimaryButton(
                text = "Create Account",
                onClick = { viewModel.onAction(SignUpAction.SignUpClicked) },
                isLoading = state.isLoading,
                containerColor = accent,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(40.dp))
        }
    }
}