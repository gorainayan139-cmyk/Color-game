package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BetType
import com.example.ui.components.BetConfirmModal
import com.example.ui.components.BettingBoard
import com.example.ui.components.ChipSelector
import com.example.ui.components.DualAccountSwitcherModal
import com.example.ui.components.GameHistorySection
import com.example.ui.components.RoundResultDialog
import com.example.ui.components.RoundTimerBar
import com.example.ui.components.TopHeaderBar
import com.example.ui.components.WalletModal
import com.example.ui.theme.CasinoDarkBg
import com.example.ui.theme.CasinoSurface
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PredictGreen
import com.example.ui.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onNavigateToAdmin: () -> Unit
) {
    val activeProfile by viewModel.activeProfile.collectAsState()
    val allProfiles by viewModel.allProfiles.collectAsState()
    val activeUserId by viewModel.activeUserId.collectAsState()
    val recentRounds by viewModel.recentRounds.collectAsState()
    val userBets by viewModel.userBets.collectAsState()
    val adminSettings by viewModel.adminSettings.collectAsState()

    val currentPeriodId by viewModel.currentPeriodId.collectAsState()
    val remainingSeconds by viewModel.remainingSeconds.collectAsState()
    val totalSeconds by viewModel.totalDurationSeconds.collectAsState()
    val isBettingLocked by viewModel.isBettingLocked.collectAsState()

    val showResultDialog by viewModel.showResultDialog.collectAsState()
    val lastResultRound by viewModel.lastResultRound.collectAsState()
    val userRoundWon by viewModel.userRoundWon.collectAsState()
    val userRoundPayout by viewModel.userRoundPayout.collectAsState()

    val selectedChip by viewModel.selectedChipAmount.collectAsState()
    val selectedMultiplier by viewModel.selectedMultiplier.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Dialog control states
    var showDualSwitcher by remember { mutableStateOf(false) }
    var showWalletModal by remember { mutableStateOf(false) }
    var pendingBet by remember { mutableStateOf<Pair<BetType, String>?>(null) }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        containerColor = CasinoDarkBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Sticky Top Bar with Dual Copy switch and Balance
            TopHeaderBar(
                activeProfile = activeProfile,
                onSwitchCopyClick = { showDualSwitcher = true },
                onWalletClick = { showWalletModal = true },
                onAdminClick = onNavigateToAdmin
            )

            // Scrollable Game Arena
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                // Arena Hero Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.banner_color_win),
                        contentDescription = "Color Win Arena",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        CasinoDarkBg.copy(alpha = 0.85f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .padding(14.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(PredictGreen)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "FAST COLOR PARITY",
                                    color = Color.Black,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Win Go 30s Arena",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Predict Green, Violet, Red or Numbers 0-9",
                                color = GoldAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Round Timer & Period Info
                RoundTimerBar(
                    periodId = currentPeriodId,
                    remainingSeconds = remainingSeconds,
                    totalSeconds = totalSeconds,
                    isBettingLocked = isBettingLocked
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Betting Board
                BettingBoard(
                    isBettingLocked = isBettingLocked,
                    colorMultiplier = adminSettings.colorMultiplier,
                    violetMultiplier = adminSettings.violetMultiplier,
                    numberMultiplier = adminSettings.numberMultiplier,
                    sizeMultiplier = adminSettings.sizeMultiplier,
                    onSelectBet = { type, target ->
                        pendingBet = Pair(type, target)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Chip & Multiplier Selector
                ChipSelector(
                    selectedAmount = selectedChip,
                    selectedMultiplier = selectedMultiplier,
                    onAmountChange = { viewModel.setChipAmount(it) },
                    onMultiplierChange = { viewModel.setMultiplier(it) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // History, Trends & Orders
                GameHistorySection(
                    rounds = recentRounds,
                    userBets = userBets
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Modal: Bet Confirmation
    pendingBet?.let { bet ->
        BetConfirmModal(
            betType = bet.first,
            target = bet.second,
            currentChip = selectedChip,
            currentMultiplier = selectedMultiplier,
            userBalance = activeProfile?.balance ?: 0.0,
            onDismiss = { pendingBet = null },
            onConfirm = {
                viewModel.placeBet(bet.first, bet.second)
                pendingBet = null
            }
        )
    }

    // Modal: Round Result Celebration
    if (showResultDialog) {
        RoundResultDialog(
            round = lastResultRound,
            isWon = userRoundWon,
            payout = userRoundPayout,
            onDismiss = { viewModel.dismissResultDialog() }
        )
    }

    // Modal: Dual Account Copy Switcher
    if (showDualSwitcher) {
        DualAccountSwitcherModal(
            profiles = allProfiles,
            activeProfileId = activeUserId,
            onSelectProfile = { viewModel.setActiveProfile(it) },
            onDismiss = { showDualSwitcher = false }
        )
    }

    // Modal: Wallet & VIP
    if (showWalletModal) {
        WalletModal(
            profile = activeProfile,
            onDeposit = { viewModel.depositFunds(it) },
            onWithdraw = { viewModel.withdrawFunds(it) },
            onClaimDaily = { viewModel.claimDailyBonus() },
            onDismiss = { showWalletModal = false }
        )
    }
}
