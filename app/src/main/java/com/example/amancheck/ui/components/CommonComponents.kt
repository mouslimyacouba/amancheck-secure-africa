package com.example.amancheck.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.amancheck.ui.theme.DangerRed
import com.example.amancheck.ui.theme.DangerRedContainer
import com.example.amancheck.ui.theme.PrimaryGreen
import com.example.amancheck.ui.theme.PrimaryGreenContainer
import com.example.amancheck.ui.theme.WarningAmber
import com.example.amancheck.ui.theme.WarningAmberContainer

@Composable
fun RiskBadge(
    riskLevel: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (riskLevel.lowercase()) {
        "high", "danger", "dangereux" -> Triple(DangerRedContainer, DangerRed, "DANGER ÉLEVÉ")
        "suspicious", "suspect", "warning" -> Triple(WarningAmberContainer, WarningAmber, "SUSPECT")
        else -> Triple(PrimaryGreenContainer, PrimaryGreen, "SÛR")
    }

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
