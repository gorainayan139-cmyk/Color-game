package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.GameRound
import com.example.data.model.PredictColor
import com.example.ui.theme.CasinoCardBg
import com.example.ui.theme.CasinoCardBorder
import com.example.ui.theme.CasinoSurface
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LossRed
import com.example.ui.theme.PredictGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WinGreen
import java.util.Locale

@Composable
fun RoundResultDialog(
    round: GameRound?,
    isWon: Boolean,
    payout: Double,
    onDismiss: () -> Unit
) {
    if (round == null) return

    val colors = PredictColor.fromNumber(round.winningNumber)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = CasinoSurface,
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (isWon) GoldAccent else CasinoCardBorder
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header badge
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (isWon) GoldAccent.copy(alpha = 0.2f) else LossRed.copy(alpha = 0.2f))
                        .border(
                            1.dp,
                            if (isWon) GoldAccent else LossRed,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isWon) Icons.Default.EmojiEvents else Icons.Default.SentimentDissatisfied,
                        contentDescription = "Result Status",
                        tint = if (isWon) GoldAccent else LossRed,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isWon) "CONGRATULATIONS!" else "ROUND RESULT",
                    color = if (isWon) GoldAccent else TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "Period: ${round.periodId}",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Winning Number Box
                NumberBadge(
                    number = round.winningNumber,
                    size = 64.dp,
                    fontSize = 28
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Color and Size badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    colors.forEach { c ->
                        ChipBadge(
                            text = c.displayName.uppercase(Locale.US),
                            backgroundColor = Color(c.hexColor)
                        )
                    }

                    ChipBadge(
                        text = round.size,
                        backgroundColor = if (round.size == "BIG") Color(0xFF1E3A8A) else Color(0xFF14532D)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Payout result display
                if (isWon) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(WinGreen.copy(alpha = 0.15f))
                            .border(1.dp, WinGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "TOTAL PAYOUT",
                            color = WinGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "+₹${String.format(Locale.US, "%,.2f", payout)}",
                            color = WinGreen,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                } else {
                    Text(
                        text = "Better luck on the next prediction!",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = PredictGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dismiss_result_dialog_button")
                ) {
                    Text(
                        text = "Continue Playing",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
