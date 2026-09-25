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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BetType
import com.example.ui.theme.CasinoCardBg
import com.example.ui.theme.CasinoCardBorder
import com.example.ui.theme.CasinoSurface
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PredictGreen
import com.example.ui.theme.PredictRed
import com.example.ui.theme.PredictViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun BetConfirmModal(
    betType: BetType,
    target: String,
    currentChip: Double,
    currentMultiplier: Int,
    userBalance: Double,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val totalAmount = currentChip * currentMultiplier
    val canAfford = userBalance >= totalAmount

    val badgeColor = when {
        target == "GREEN" -> PredictGreen
        target == "RED" -> PredictRed
        target == "VIOLET" -> PredictViolet
        target == "BIG" -> Color(0xFF3B82F6)
        target == "SMALL" -> Color(0xFF22C55E)
        else -> GoldAccent
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CasinoSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, CasinoCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CONFIRM PREDICTION",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeColor)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = target,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Breakdown Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CasinoCardBg)
                        .border(1.dp, CasinoCardBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Contract Base", color = TextMuted, fontSize = 13.sp)
                        Text("₹${currentChip.toInt()}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Multiplier", color = TextMuted, fontSize = 13.sp)
                        Text("X$currentMultiplier", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Amount", color = TextMuted, fontSize = 13.sp)
                        Text("₹${totalAmount.toInt()}", color = GoldAccent, fontSize = 16.sp, fontWeight = FontWeight.Black)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Current Balance", color = TextMuted, fontSize = 12.sp)
                        Text("₹${String.format(Locale.US, "%,.2f", userBalance)}", color = TextSecondary, fontSize = 12.sp)
                    }
                }

                if (!canAfford) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Insufficient funds! Please add money to wallet.",
                        color = PredictRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", color = TextSecondary)
                    }

                    Button(
                        onClick = onConfirm,
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(containerColor = PredictGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("confirm_bet_action_button")
                    ) {
                        Text(
                            text = "Confirm ₹${totalAmount.toInt()}",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
