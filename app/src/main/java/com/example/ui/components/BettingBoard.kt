package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BetType
import com.example.ui.theme.BlueNeon
import com.example.ui.theme.CasinoCardBg
import com.example.ui.theme.CasinoCardBorder
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PredictGreen
import com.example.ui.theme.PredictRed
import com.example.ui.theme.PredictViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun BettingBoard(
    isBettingLocked: Boolean,
    colorMultiplier: Double,
    violetMultiplier: Double,
    numberMultiplier: Double,
    sizeMultiplier: Double,
    onSelectBet: (betType: BetType, target: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val boardAlpha = if (isBettingLocked) 0.5f else 1.0f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .alpha(boardAlpha)
            .clip(RoundedCornerShape(16.dp))
            .background(CasinoCardBg)
            .border(1.dp, CasinoCardBorder, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        // --- 1. Colors Row (Green, Violet, Red) ---
        Text(
            text = "SELECT COLOR PREDICTION",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // GREEN
            ColorBetCard(
                name = "GREEN",
                payoutText = "${String.format("%.1f", colorMultiplier)}x",
                gradient = listOf(Color(0xFF059669), Color(0xFF10B981)),
                modifier = Modifier
                    .weight(1f)
                    .testTag("bet_button_green"),
                onClick = { if (!isBettingLocked) onSelectBet(BetType.COLOR, "GREEN") }
            )

            // VIOLET
            ColorBetCard(
                name = "VIOLET",
                payoutText = "${String.format("%.1f", violetMultiplier)}x",
                gradient = listOf(Color(0xFF7C3AED), Color(0xFF8B5CF6)),
                modifier = Modifier
                    .weight(1f)
                    .testTag("bet_button_violet"),
                onClick = { if (!isBettingLocked) onSelectBet(BetType.COLOR, "VIOLET") }
            )

            // RED
            ColorBetCard(
                name = "RED",
                payoutText = "${String.format("%.1f", colorMultiplier)}x",
                gradient = listOf(Color(0xFFDC2626), Color(0xFFEF4444)),
                modifier = Modifier
                    .weight(1f)
                    .testTag("bet_button_red"),
                onClick = { if (!isBettingLocked) onSelectBet(BetType.COLOR, "RED") }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- 2. Numbers Grid (0 to 9) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SELECT NUMBER",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "PAYOUT: ${String.format("%.1f", numberMultiplier)}X",
                color = GoldAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Row 1: 0, 1, 2, 3, 4
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (num in 0..4) {
                NumberBetButton(
                    number = num,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("bet_button_number_$num"),
                    onClick = { if (!isBettingLocked) onSelectBet(BetType.NUMBER, num.toString()) }
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Row 2: 5, 6, 7, 8, 9
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (num in 5..9) {
                NumberBetButton(
                    number = num,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("bet_button_number_$num"),
                    onClick = { if (!isBettingLocked) onSelectBet(BetType.NUMBER, num.toString()) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- 3. Big / Small Row ---
        Text(
            text = "SELECT SIZE PREDICTION",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SizeBetCard(
                title = "BIG",
                rangeDesc = "Numbers 5, 6, 7, 8, 9",
                payout = "${String.format("%.1f", sizeMultiplier)}x",
                bgColor = Color(0xFF1E3A8A),
                borderColor = Color(0xFF3B82F6),
                modifier = Modifier
                    .weight(1f)
                    .testTag("bet_button_big"),
                onClick = { if (!isBettingLocked) onSelectBet(BetType.SIZE, "BIG") }
            )

            SizeBetCard(
                title = "SMALL",
                rangeDesc = "Numbers 0, 1, 2, 3, 4",
                payout = "${String.format("%.1f", sizeMultiplier)}x",
                bgColor = Color(0xFF14532D),
                borderColor = Color(0xFF22C55E),
                modifier = Modifier
                    .weight(1f)
                    .testTag("bet_button_small"),
                onClick = { if (!isBettingLocked) onSelectBet(BetType.SIZE, "SMALL") }
            )
        }
    }
}

@Composable
fun ColorBetCard(
    name: String,
    payoutText: String,
    gradient: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.verticalGradient(gradient))
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 6.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = name,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = payoutText,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun NumberBetButton(
    number: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 0 is Red & Violet, 5 is Green & Violet
    val brush = when (number) {
        0 -> Brush.linearGradient(listOf(PredictRed, PredictViolet))
        5 -> Brush.linearGradient(listOf(PredictGreen, PredictViolet))
        1, 3, 7, 9 -> Brush.linearGradient(listOf(PredictGreen, PredictGreen))
        else -> Brush.linearGradient(listOf(PredictRed, PredictRed))
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(brush)
            .clickable { onClick() }
    ) {
        Text(
            text = number.toString(),
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun SizeBetCard(
    title: String,
    rangeDesc: String,
    payout: String,
    bgColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = rangeDesc,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = payout,
                color = GoldAccent,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
