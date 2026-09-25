package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.data.model.AdminSettings
import com.example.data.model.OutcomeMode
import com.example.data.model.UserProfile
import com.example.ui.components.NumberBadge
import com.example.ui.theme.BlueNeon
import com.example.ui.theme.CasinoCardBg
import com.example.ui.theme.CasinoCardBorder
import com.example.ui.theme.CasinoDarkBg
import com.example.ui.theme.CasinoSurface
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PredictGreen
import com.example.ui.theme.PredictRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    settings: AdminSettings,
    profiles: List<UserProfile>,
    onUpdateSettings: (AdminSettings) -> Unit,
    onSetUserBalance: (userId: String, newBalance: Double) -> Unit,
    onResetData: () -> Unit,
    onBackClick: () -> Unit
) {
    var adminTab by remember { mutableIntStateOf(0) } // 0: Game Settings, 1: User Progress & Accounts, 2: Maintenance
    val tabs = listOf("Game Engine", "User Accounts", "Maintenance")

    // State copies for settings editing
    var currentDuration by remember(settings) { mutableIntStateOf(settings.roundDurationSeconds) }
    var currentOutcomeMode by remember(settings) { mutableStateOf(settings.outcomeMode) }
    var currentManualNumber by remember(settings) { mutableIntStateOf(settings.manualNextNumber) }
    var currentColorMultiplier by remember(settings) { mutableDoubleStateOf(settings.colorMultiplier) }
    var currentVioletMultiplier by remember(settings) { mutableDoubleStateOf(settings.violetMultiplier) }
    var currentNumberMultiplier by remember(settings) { mutableDoubleStateOf(settings.numberMultiplier) }
    var currentFeePercent by remember(settings) { mutableDoubleStateOf(settings.houseFeePercent) }

    Scaffold(
        containerColor = CasinoDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin",
                            tint = PredictRed,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ADMIN CONTROL PANEL",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("admin_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Game",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CasinoSurface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = adminTab,
                containerColor = CasinoSurface,
                contentColor = TextPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[adminTab]),
                        color = PredictRed,
                        height = 3.dp
                    )
                },
                divider = { HorizontalDivider(color = CasinoCardBorder, thickness = 1.dp) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = adminTab == index,
                        onClick = { adminTab = index },
                        modifier = Modifier.testTag("admin_tab_$index"),
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (adminTab == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (adminTab == index) PredictRed else TextMuted
                            )
                        }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                when (adminTab) {
                    0 -> { // Game Engine Settings
                        // 1. Next Round Outcome Rigging / Mode
                        AdminSectionCard(title = "NEXT ROUND OUTCOME ALGORITHM", icon = Icons.Default.Casino) {
                            OutcomeMode.values().forEach { mode ->
                                val isSelected = currentOutcomeMode == mode
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) PredictRed.copy(alpha = 0.15f) else CasinoSurface)
                                        .border(1.dp, if (isSelected) PredictRed else CasinoCardBorder, RoundedCornerShape(10.dp))
                                        .clickable {
                                            currentOutcomeMode = mode
                                            onUpdateSettings(
                                                settings.copy(
                                                    outcomeMode = mode,
                                                    roundDurationSeconds = currentDuration,
                                                    manualNextNumber = currentManualNumber
                                                )
                                            )
                                        }
                                        .padding(12.dp)
                                        .testTag("outcome_mode_${mode.name}"),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = mode.displayName,
                                            color = if (isSelected) PredictRed else TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = mode.description,
                                            color = TextMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = PredictRed,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            // If MANUAL mode selected, show number picker (0-9)
                            if (currentOutcomeMode == OutcomeMode.MANUAL) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "SELECT RIGGED NEXT WINNING NUMBER:",
                                    color = GoldAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    for (n in 0..9) {
                                        val isPick = currentManualNumber == n
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isPick) GoldAccent else CasinoSurface)
                                                .border(1.dp, if (isPick) Color.White else CasinoCardBorder, RoundedCornerShape(8.dp))
                                                .clickable {
                                                    currentManualNumber = n
                                                    onUpdateSettings(
                                                        settings.copy(
                                                            outcomeMode = OutcomeMode.MANUAL,
                                                            manualNextNumber = n,
                                                            roundDurationSeconds = currentDuration
                                                        )
                                                    )
                                                }
                                                .padding(vertical = 8.dp)
                                                .testTag("admin_manual_num_$n")
                                        ) {
                                            Text(
                                                text = n.toString(),
                                                color = if (isPick) Color.Black else TextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 2. Round Duration
                        AdminSectionCard(title = "ROUND TIMER SPEED", icon = Icons.Default.Tune) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(15, 30, 60).forEach { sec ->
                                    val isSelected = currentDuration == sec
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) PredictGreen.copy(alpha = 0.2f) else CasinoSurface)
                                            .border(1.dp, if (isSelected) PredictGreen else CasinoCardBorder, RoundedCornerShape(10.dp))
                                            .clickable {
                                                currentDuration = sec
                                                onUpdateSettings(
                                                    settings.copy(
                                                        roundDurationSeconds = sec,
                                                        outcomeMode = currentOutcomeMode,
                                                        manualNextNumber = currentManualNumber
                                                    )
                                                )
                                            }
                                            .padding(vertical = 12.dp)
                                            .testTag("admin_timer_speed_${sec}s")
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "${sec}s",
                                                color = if (isSelected) PredictGreen else TextPrimary,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = if (sec == 15) "Turbo" else if (sec == 30) "Standard" else "Slow",
                                                color = TextMuted,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 3. Multipliers & Commission Rate
                        AdminSectionCard(title = "PAYOUT MULTIPLIERS & HOUSE COMMISSIONS", icon = Icons.Default.Tune) {
                            MultiplierRow("Color Bet Payout", "${currentColorMultiplier}x")
                            MultiplierRow("Violet Bet Payout", "${currentVioletMultiplier}x")
                            MultiplierRow("Number Bet Payout", "${currentNumberMultiplier}x")
                            MultiplierRow("Big / Small Payout", "${settings.sizeMultiplier}x")
                            MultiplierRow("House Platform Fee", "${currentFeePercent}%")
                            MultiplierRow("Min / Max Bet Limits", "₹${settings.minBet.toInt()} - ₹${settings.maxBet.toInt()}")
                        }
                    }

                    1 -> { // User Progress & Account Management
                        Text(
                            text = "MANAGE APP COPIES & USER PROGRESS",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        profiles.forEach { user ->
                            UserAccountCard(
                                user = user,
                                onAddBalance = { amountToAdd ->
                                    onSetUserBalance(user.id, user.balance + amountToAdd)
                                },
                                onSetExactBalance = { exactAmount ->
                                    onSetUserBalance(user.id, exactAmount)
                                }
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }

                    2 -> { // Maintenance & Reset
                        AdminSectionCard(title = "SYSTEM DATA RESET & INITIALIZATION", icon = Icons.Default.Refresh) {
                            Text(
                                text = "Clear all active rounds, completed bets, and transactions, then seed 20 fresh authentic game rounds into local database.",
                                color = TextMuted,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = onResetData,
                                colors = ButtonDefaults.buttonColors(containerColor = PredictRed),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_reset_game_data_button")
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reset Game Data & Seed Fresh Rounds", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CasinoCardBg)
            .border(1.dp, CasinoCardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = title, tint = TextMuted, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
fun MultiplierRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 12.sp)
        Text(text = value, color = GoldAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun UserAccountCard(
    user: UserProfile,
    onAddBalance: (Double) -> Unit,
    onSetExactBalance: (Double) -> Unit
) {
    var customAmountText by remember { mutableStateOf("") }
    val isCopy1 = user.id == "copy_1"
    val badgeColor = if (isCopy1) PredictGreen else BlueNeon

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CasinoCardBg)
            .border(1.dp, CasinoCardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        // User identity row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(badgeColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = user.displayName,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(GoldAccent.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "VIP ${user.vipLevel}",
                    color = GoldAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Metrics grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CasinoSurface)
                    .padding(8.dp)
            ) {
                Text("Balance", color = TextMuted, fontSize = 10.sp)
                Text("₹${String.format(Locale.US, "%,.2f", user.balance)}", color = GoldAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CasinoSurface)
                    .padding(8.dp)
            ) {
                Text("Win Rate", color = TextMuted, fontSize = 10.sp)
                Text("${String.format(Locale.US, "%.1f", user.winRatePercent)}%", color = PredictGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CasinoSurface)
                    .padding(8.dp)
            ) {
                Text("Total Bets", color = TextMuted, fontSize = 10.sp)
                Text("${user.totalBetsCount}", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick adjust buttons
        Text("Quick Adjust Balance:", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(500.0, 1000.0, 5000.0, 10000.0).forEach { amt ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CasinoSurface)
                        .border(1.dp, CasinoCardBorder, RoundedCornerShape(8.dp))
                        .clickable { onAddBalance(amt) }
                        .padding(vertical = 8.dp)
                        .testTag("admin_add_${amt.toInt()}_${user.id}")
                ) {
                    Text(
                        text = "+₹${amt.toInt()}",
                        color = PredictGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Exact balance input
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = customAmountText,
                onValueChange = { customAmountText = it },
                label = { Text("Set Exact Balance (₹)", color = TextMuted, fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = CasinoCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Button(
                onClick = {
                    val amount = customAmountText.toDoubleOrNull()
                    if (amount != null) {
                        onSetExactBalance(amount)
                        customAmountText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("set_balance_button_${user.id}")
            ) {
                Text("Set", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
