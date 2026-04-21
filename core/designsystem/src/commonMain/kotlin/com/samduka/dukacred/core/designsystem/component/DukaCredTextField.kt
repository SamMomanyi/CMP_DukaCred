package com.samduka.dukacred.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.samduka.dukacred.core.designsystem.DukaCredColors

@Composable
fun DukaCredTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    errorMessage: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text  = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value             = value,
            onValueChange     = onValueChange,
            modifier          = Modifier.fillMaxWidth(),
            placeholder       = {
                Text(
                    text  = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                )
            },
            trailingIcon      = trailingIcon,
            isError           = errorMessage != null,
            visualTransformation = visualTransformation,
            keyboardOptions   = keyboardOptions,
            keyboardActions   = keyboardActions,
            singleLine        = singleLine,
            textStyle         = MaterialTheme.typography.bodyLarge,
            shape             = MaterialTheme.shapes.medium,
            colors            = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor     = DukaCredColors.Error,
                focusedTextColor     = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor   = MaterialTheme.colorScheme.onSurface,
                cursorColor          = MaterialTheme.colorScheme.primary,
                focusedContainerColor   = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            ),
        )
        AnimatedVisibility(
            visible = errorMessage != null,
            enter   = expandVertically(),
            exit    = shrinkVertically(),
        ) {
            errorMessage?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = DukaCredColors.Error,
                )
            }
        }
    }
}