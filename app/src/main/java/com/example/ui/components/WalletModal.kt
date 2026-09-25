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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.UserProfile
import com.example.ui.theme.CasinoCardBg
import com.example.ui.theme.CasinoCardBorder
import com.example.ui.theme.CasinoSurface
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LossRed
import com.example.ui.theme.PredictGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun WalletModal(
    profile: UserProfile?,
    onDeposit: (Double) -> Unit,
    onWithdraw: (Double) -> Unit,
    onClaimDaily: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Recharge", "Withdraw", "VIP & Perks")

    var selectedRechargeAmount by remember { mutableDoubleStateOf(500.0) }
    var withdrawAmountText by remember { mutableStateOf("") }
    var upiAddressText by remember { mutableStateOf("") }

    val quickRechargeAmounts = listOf(100.0, 500.0, 1000.0, 2000.0, 5000.0, 10000.0)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = CasinoSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, CasinoCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "Wallet",
                            tint = GoldAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PLAYER WALLET",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Balance summary banner
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CasinoCardBg)
                        .border(1.dp, CasinoCardBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "TOTAL AVAILABLE BALANCE",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "₹${String.format(Locale.US, "%,.2f", profile?.balance ?: 0.0)}",
                        color = GoldAccent,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "${profile?.displayName} • Wagered: ₹${String.format(Locale.US, "%,d", profile?.totalWagered?.toInt() ?: 0)}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = CasinoSurface,
                    contentColor = TextPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = GoldAccent,
                            height = 3.dp
                        )
                    },
                    divider = { HorizontalDivider(color = CasinoCardBorder, thickness = 1.dp) }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTab == index) GoldAccent else TextMuted
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Content
                when (selectedTab) {
                    0 -> { // Recharge
                        Text("Select Recharge Amount", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Amount grid (2 rows x 3 cols)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                quickRechargeAmounts.take(3).forEach { amt ->
                                    val isSelected = selectedRechargeAmount == amt
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) GoldAccent.copy(alpha = 0.2f) else CasinoCardBg)
                                            .border(1.dp, if (isSelected) GoldAccent else CasinoCardBorder, RoundedCornerShape(10.dp))
                                            .clickable { selectedRechargeAmount = amt }
                                            .padding(vertical = 10.dp)
                                    ) {
                                        Text(
                                            text = "₹${amt.toInt()}",
                                            color = if (isSelected) GoldAccent else TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                quickRechargeAmounts.drop(3).forEach { amt ->
                                    val isSelected = selectedRechargeAmount == amt
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) GoldAccent.copy(alpha = 0.2f) else CasinoCardBg)
                                            .border(1.dp, if (isSelected) GoldAccent else CasinoCardBorder, RoundedCornerShape(10.dp))
                                            .clickable { selectedRechargeAmount = amt }
                                            .padding(vertical = 10.dp)
                                    ) {
                                        Text(
                                            text = "₹${amt.toInt()}",
                                            color = if (isSelected) GoldAccent else TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                onDeposit(selectedRechargeAmount)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PredictGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("confirm_recharge_button")
                        ) {
                            Text(
                                text = "Instant Recharge ₹${selectedRechargeAmount.toInt()}",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    1 -> { // Withdraw
                        Text("Withdraw to Bank / UPI", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = withdrawAmountText,
                            onValueChange = { withdrawAmountText = it },
                            label = { Text("Amount (₹)", color = TextMuted) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_amount_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = CasinoCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = upiAddressText,
                            onValueChange = { upiAddressText = it },
                            label = { Text("UPI ID or Bank Account", color = TextMuted) },
                            singleLine = true,
                            placeholder = { Text("user@okhdfcbank", color = TextMuted) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_upi_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = CasinoCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        val withdrawAmt = withdrawAmountText.toDoubleOrNull() ?: 0.0
                        val isWithdrawValid = withdrawAmt > 0.0 && withdrawAmt <= (profile?.balance ?: 0.0)

                        Button(
                            onClick = {
                                onWithdraw(withdrawAmt)
                                onDismiss()
                            },
                            enabled = isWithdrawValid,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("confirm_withdraw_button")
                        ) {
                            Text(
                                text = "Request Withdrawal",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    2 -> { // VIP & Perks
                        val currentVip = profile?.vipLevel ?: 1
                        val currentExp = profile?.vipExp ?: 0
                        val nextExp = currentVip * 500
                        val vipProgress = (currentExp.toFloat() / nextExp.toFloat()).coerceIn(0f, 1f)

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
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = "VIP", tint = GoldAccent, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("VIP Level $currentVip", color = GoldAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                Text("Next: VIP ${currentVip + 1}", color = TextMuted, fontSize = 11.sp)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = { vipProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = GoldAccent,
                                trackColor = Color(0xFF0F172A)
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text("$currentExp / $nextExp VIP EXP", color = TextMuted, fontSize = 10.sp)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Daily Bonus Card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(PredictGreen.copy(alpha = 0.1f))
                                .border(1.dp, PredictGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CardGiftcard, contentDescription = "Bonus", tint = PredictGreen, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Daily Attendance Reward", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("Free +₹100 bonus chips every day", color = TextMuted, fontSize = 10.sp)
                                }
                            }

                            Button(
                                onClick = {
                                    onClaimDaily()
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PredictGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("claim_daily_bonus_button")
                            ) {
                                Text("Claim", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
