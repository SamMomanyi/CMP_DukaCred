package com.samduka.dukacred.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DukaCredPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
    val bgColor by animateColorAsState(
        targetValue = if (enabled && !isLoading) containerColor
        else containerColor.copy(alpha = 0.5f),
        animationSpec = tween(200),
        label = "btn_color",
    )

    Button(
        onClick    = { if (!isLoading) onClick() },
        enabled    = enabled && !isLoading,
        modifier   = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors     = ButtonDefaults.buttonColors(
            containerColor         = bgColor,
            contentColor           = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.4f),
            disabledContentColor   = contentColor.copy(alpha = 0.6f),
        ),
        shape         = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(horizontal = 24.dp),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color     = contentColor,
                strokeWidth = 2.dp,
                modifier  = Modifier.height(20.dp),
            )
        } else {
            Text(
                text  = text,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}