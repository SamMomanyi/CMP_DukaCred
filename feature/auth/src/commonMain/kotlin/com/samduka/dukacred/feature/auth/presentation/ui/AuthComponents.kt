package com.samduka.dukacred.feature.auth.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.samduka.dukacred.core.designsystem.DukaCredColors

@Composable
internal fun RoleBadge(
    text: String,
    color: androidx.compose.ui.graphics.Color,
) {
    Box(
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.12f),
                shape = RoundedCornerShape(50.dp),
            )
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.3f),
                shape = RoundedCornerShape(50.dp),
            )
            .padding(horizontal = 14.dp, vertical = 6.dp),
    ) {
        Text(
            text  = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = color,
        )
    }
}

@Composable
internal fun ErrorBanner(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = DukaCredColors.Error.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
            )
            .border(
                width = 1.dp,
                color = DukaCredColors.Error.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector        = Icons.Rounded.ErrorOutline,
            contentDescription = null,
            tint               = DukaCredColors.Error,
            modifier           = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text  = message,
            style = MaterialTheme.typography.bodyMedium,
            color = DukaCredColors.Error,
        )
    }
}