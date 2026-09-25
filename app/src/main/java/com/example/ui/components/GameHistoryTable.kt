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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BetOrder
import com.example.data.model.GameRound
import com.example.data.model.PredictColor
import com.example.ui.theme.BlueNeon
import com.example.ui.theme.CasinoCardBg
import com.example.ui.theme.CasinoCardBorder
import com.example.ui.theme.CasinoDarkBg
import com.example.ui.theme.CasinoSurface
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LossRed
import com.example.ui.theme.PendingOrange
import com.example.ui.theme.PredictGreen
import com.example.ui.theme.PredictRed
import com.example.ui.theme.PredictViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WinGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GameHistorySection(
    rounds: List<GameRound>,
    userBets: List<BetOrder>,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Game Record", "Trend Chart", "My Orders")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CasinoSurface)
            .border(1.dp, CasinoCardBorder, RoundedCornerShape(16.dp))
            .padding(bottom = 8.dp)
    ) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CasinoSurface,
            contentColor = TextPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = PredictGreen,
                    height = 3.dp
                )
            },
            divider = {
                HorizontalDivider(color = CasinoCardBorder, thickness = 1.dp)
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    modifier = Modifier.testTag("history_tab_$index"),
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == index) PredictGreen else TextMuted
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> GameRecordList(rounds)
            1 -> TrendChartAnalysis(rounds)
            2 -> MyOrdersList(userBets)
        }
    }
}

@Composable
fun GameRecordList(rounds: List<GameRound>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CasinoCardBg)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Period", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f))
            Text("Number", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
            Text("Big/Small", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
            Text("Color Result", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        }

        HorizontalDivider(color = CasinoCardBorder, thickness = 0.5.dp)

        if (rounds.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                Text("No previous rounds yet. Waiting for first result...", color = TextMuted, fontSize = 13.sp)
            }
        } else {
            rounds.take(25).forEach { round ->
                val colors = PredictColor.fromNumber(round.winningNumber)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = round.periodId,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.weight(1.3f)
                    )

                    Box(modifier = Modifier.weight(0.9f)) {
                        NumberBadge(number = round.winningNumber, size = 26.dp, fontSize = 13)
                    }

                    Text(
                        text = round.size,
                        color = if (round.size == "BIG") BlueNeon else PredictGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.9f)
                    )

                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        colors.forEach { c ->
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color(c.hexColor))
                            )
                        }
                    }
                }
                HorizontalDivider(color = CasinoCardBorder.copy(alpha = 0.4f), thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun TrendChartAnalysis(rounds: List<GameRound>) {
    val totalCount = rounds.size.coerceAtLeast(1)
    val greenCount = rounds.count { PredictColor.fromNumber(it.winningNumber).any { c -> c == PredictColor.GREEN } }
    val redCount = rounds.count { PredictColor.fromNumber(it.winningNumber).any { c -> c == PredictColor.RED } }
    val violetCount = rounds.count { PredictColor.fromNumber(it.winningNumber).any { c -> c == PredictColor.VIOLET } }
    val bigCount = rounds.count { it.size == "BIG" }
    val smallCount = rounds.count { it.size == "SMALL" }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
    ) {
        Text(
            text = "STATISTICAL DISTRIBUTION (LAST ${rounds.size} ROUNDS)",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Ratio summary boxes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            StatBox(
                label = "Green",
                count = greenCount,
                percent = (greenCount * 100) / totalCount,
                color = PredictGreen,
                modifier = Modifier.weight(1f)
            )
            StatBox(
                label = "Violet",
                count = violetCount,
                percent = (violetCount * 100) / totalCount,
                color = PredictViolet,
                modifier = Modifier.weight(1f)
            )
            StatBox(
                label = "Red",
                count = redCount,
                percent = (redCount * 100) / totalCount,
                color = PredictRed,
                modifier = Modifier.weight(1f)
            )
            StatBox(
                label = "Big",
                count = bigCount,
                percent = (bigCount * 100) / totalCount,
                color = Color(0xFF3B82F6),
                modifier = Modifier.weight(1f)
            )
            StatBox(
                label = "Small",
                count = smallCount,
                percent = (smallCount * 100) / totalCount,
                color = Color(0xFF10B981),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "BEAD MATRIX / ROAD MAP",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Visual bead horizontal matrix
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(CasinoCardBg)
                .border(1.dp, CasinoCardBorder, RoundedCornerShape(10.dp))
                .horizontalScroll(rememberScrollState())
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            rounds.take(30).reversed().forEach { r ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    NumberBadge(number = r.winningNumber, size = 26.dp, fontSize = 12)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (r.size == "BIG") Color(0xFF1E3A8A) else Color(0xFF14532D))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = if (r.size == "BIG") "B" else "S",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(
    label: String,
    count: Int,
    percent: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(vertical = 6.dp, horizontal = 2.dp)
    ) {
        Text(text = label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(text = "$count", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Black)
        Text(text = "$percent%", color = TextMuted, fontSize = 9.sp)
    }
}

@Composable
fun MyOrdersList(userBets: List<BetOrder>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        if (userBets.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                Text(
                    text = "No bets placed yet on this account. Pick a color or number to start!",
                    color = TextMuted,
                    fontSize = 13.sp
                )
            }
        } else {
            val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
            userBets.take(20).forEach { bet ->
                val statusColor = when (bet.status) {
                    "WON" -> WinGreen
                    "LOST" -> LossRed
                    else -> PendingOrange
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CasinoCardBg)
                        .border(1.dp, CasinoCardBorder, RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Period ${bet.periodId}",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            ChipBadge(text = bet.target, backgroundColor = CasinoSurface)
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "${dateFormat.format(Date(bet.createdAt))} • ₹${bet.baseAmount.toInt()} x ${bet.multiplier}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(statusColor.copy(alpha = 0.2f))
                                .border(0.5.dp, statusColor, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = bet.status,
                                color = statusColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        if (bet.status == "WON") {
                            Text(
                                text = "+₹${String.format(Locale.US, "%.2f", bet.payout)}",
                                color = WinGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = "-₹${bet.totalAmount.toInt()}",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
