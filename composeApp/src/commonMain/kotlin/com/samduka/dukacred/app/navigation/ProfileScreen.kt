package com.samduka.dukacred.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.PersonOutline
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samduka.dukacred.core.designsystem.DukaCredColors
import com.samduka.dukacred.core.designsystem.component.DukaCredPrimaryButton

private data class ProfileMenuItem(
    val title: String,
    val icon: ImageVector,
)

private val profileMenuItems = listOf(
    ProfileMenuItem("Account Details", Icons.Rounded.Badge),
    ProfileMenuItem("Business Profile", Icons.Rounded.Storefront),
    ProfileMenuItem("Help & Support", Icons.Rounded.HelpOutline),
)

@Composable
fun ProfileScreen(
    merchantName: String = "Sam's Duka",
    phoneNumber: String = "+254 712 345 678",
    onAccountDetailsClick: () -> Unit = {},
    onBusinessProfileClick: () -> Unit = {},
    onHelpAndSupportClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
) {
    Scaffold(
        containerColor = DukaCredColors.ForestGreen900,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DukaCredColors.ForestGreen900)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                DukaCredPrimaryButton(
                    text = "Log Out",
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .border(
                            width = 1.5.dp,
                            color = DukaCredColors.Error,
                            shape = MaterialTheme.shapes.medium,
                        ),
                    containerColor = Color.Transparent,
                    contentColor = DukaCredColors.Error,
                )
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(DukaCredColors.ForestGreen900)
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                ProfileHeader(
                    merchantName = merchantName,
                    phoneNumber = phoneNumber,
                )
            }

            item {
                Text(
                    text = "Settings",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = DukaCredColors.Cream300,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(DukaCredColors.ForestGreen800),
                ) {
                    profileMenuItems.forEachIndexed { index, item ->
                        SettingsMenuRow(
                            item = item,
                            showDivider = index < profileMenuItems.lastIndex,
                            onClick = when (item.title) {
                                "Account Details" -> onAccountDetailsClick
                                "Business Profile" -> onBusinessProfileClick
                                else -> onHelpAndSupportClick
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    merchantName: String,
    phoneNumber: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(DukaCredColors.ForestGreen700),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.PersonOutline,
                contentDescription = "Merchant avatar",
                tint = DukaCredColors.Cream100,
                modifier = Modifier.size(38.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = merchantName,
                color = DukaCredColors.Cream100,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = phoneNumber,
                color = DukaCredColors.Cream300,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun SettingsMenuRow(
    item: ProfileMenuItem,
    showDivider: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(DukaCredColors.ForestGreen700),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = DukaCredColors.Ochre400,
                )
            }

            Text(
                text = item.title,
                modifier = Modifier.weight(1f),
                color = DukaCredColors.Cream100,
                style = MaterialTheme.typography.titleMedium,
            )

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = "${item.title} chevron",
                tint = DukaCredColors.Cream300,
            )
        }

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 18.dp),
                color = DukaCredColors.WhiteAlpha10,
                thickness = 1.dp,
            )
        }
    }
}
