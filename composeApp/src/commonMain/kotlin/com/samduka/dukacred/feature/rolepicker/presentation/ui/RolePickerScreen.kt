package com.samduka.dukacred.feature.rolepicker.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RolePickerScreen(
    onMerchantSelected: () -> Unit,
    onAdminSelected: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "DukaCred",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "Choose the experience you want to open.",
            modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
        )
        Button(
            onClick = onMerchantSelected,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Merchant app")
        }
        Button(
            onClick = onAdminSelected,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
        ) {
            Text("Admin dashboard")
        }
    }
}
