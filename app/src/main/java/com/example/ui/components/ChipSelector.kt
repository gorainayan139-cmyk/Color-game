package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BlueNeon
import com.example.ui.theme.CasinoCardBg
import com.example.ui.theme.CasinoCardBorder
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PredictGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun ChipSelector(
    selectedAmount: Double,
    selectedMultiplier: Int,
    onAmountChange: (Double) -> Unit,
    onMultiplierChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val chipAmounts = listOf(10.0, 50.0, 100.0, 500.0, 1000.0)
    val multipliers = listOf(1, 2, 5, 10, 20, 50, 100)
    val totalAmount = selectedAmount * selectedMultiplier

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CasinoCardBg)
            .border(1.dp, CasinoCardBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        // Row 1: Chips title and current bet calculation preview
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BET AMOUNT (CHIPS)",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "TOTAL: ",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "₹${String.format(Locale.US, "%,d", totalAmount.toInt())}",
                    color = GoldAccent,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chips row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            chipAmounts.forEach { amount ->
                val isSelected = selectedAmount == amount
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) {
                                Brush.radialGradient(listOf(GoldAccent, Color(0xFFD97706)))
                            } else {
                                Brush.radialGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
                            }
                        )
                        .border(
                            2.dp,
                            if (isSelected) Color.White else CasinoCardBorder,
                            CircleShape
                        )
                        .clickable { onAmountChange(amount) }
                        .testTag("chip_amount_${amount.toInt()}")
                ) {
                    Text(
                        text = "₹${amount.toInt()}",
                        color = if (isSelected) Color.Black else TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Multipliers row
        Text(
            text = "MULTIPLIER",
            color = TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            multipliers.forEach { mult ->
                val isSelected = selectedMultiplier == mult
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) BlueNeon else Color(0xFF0F172A))
                        .border(
                            1.dp,
                            if (isSelected) BlueNeon else CasinoCardBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onMultiplierChange(mult) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("multiplier_x$mult")
                ) {
                    Text(
                        text = "X$mult",
                        color = if (isSelected) Color.White else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
