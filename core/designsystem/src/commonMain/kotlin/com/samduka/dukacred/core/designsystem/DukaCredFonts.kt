package com.samduka.dukacred.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.samduka.dukacred.core.designsystem.generated.resources.Res
import com.samduka.dukacred.core.designsystem.generated.resources.dm_sans_medium
import com.samduka.dukacred.core.designsystem.generated.resources.dm_sans_regular
import com.samduka.dukacred.core.designsystem.generated.resources.dm_sans_semibold
import com.samduka.dukacred.core.designsystem.generated.resources.sora_bold
import com.samduka.dukacred.core.designsystem.generated.resources.sora_medium
import com.samduka.dukacred.core.designsystem.generated.resources.sora_regular
import com.samduka.dukacred.core.designsystem.generated.resources.sora_semibold
import org.jetbrains.compose.resources.Font

object DukaCredFonts {

    @Composable
    fun soraFamily() = FontFamily(
        Font(Res.font.sora_regular,   weight = FontWeight.Normal),
        Font(Res.font.sora_medium,    weight = FontWeight.Medium),
        Font(Res.font.sora_semibold,  weight = FontWeight.SemiBold),
        Font(Res.font.sora_bold,      weight = FontWeight.Bold),
    )

    @Composable
    fun dmSansFamily() = FontFamily(
        Font(Res.font.dm_sans_regular,  weight = FontWeight.Normal),
        Font(Res.font.dm_sans_medium,   weight = FontWeight.Medium),
        Font(Res.font.dm_sans_semibold, weight = FontWeight.SemiBold),
    )
}