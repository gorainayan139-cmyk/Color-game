package com.example.data.repository

import com.example.data.db.GameDao
import com.example.data.model.AdminSettings
import com.example.data.model.BetOrder
import com.example.data.model.BetType
import com.example.data.model.GameRound
import com.example.data.model.OutcomeMode
import com.example.data.model.PredictColor
import com.example.data.model.PredictSize
import com.example.data.model.TransactionRecord
import com.example.data.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class GameRepository(private val dao: GameDao) {

    val allProfiles: Flow<List<UserProfile>> = dao.getAllProfiles()
    val recentRounds: Flow<List<GameRound>> = dao.getRecentRounds(100)
    val adminSettings: Flow<AdminSettings?> = dao.getSettings()

    fun getBetsForUser(userId: String): Flow<List<BetOrder>> = dao.getBetsForUser(userId)
    fun getTransactionsForUser(userId: String): Flow<List<TransactionRecord>> = dao.getTransactionsForUser(userId)
    fun getProfile(userId: String): Flow<UserProfile?> = dao.getProfileById(userId)

    suspend fun initializeDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        // Initialize default profiles
        val p1 = dao.getProfileByIdSync("copy_1")
        if (p1 == null) {
            dao.insertProfile(
                UserProfile(
                    id = "copy_1",
                    displayName = "Alpha Trader (Copy 1)",
                    avatarTag = "ALPHA",
                    balance = 10000.0,
                    isActive = true
                )
            )
        }

        val p2 = dao.getProfileByIdSync("copy_2")
        if (p2 == null) {
            dao.insertProfile(
                UserProfile(
                    id = "copy_2",
                    displayName = "Beta Trader (Copy 2)",
                    avatarTag = "BETA",
                    balance = 5000.0,
                    isActive = false
                )
            )
        }

        // Initialize admin settings
        val settings = dao.getSettingsSync()
        if (settings == null) {
            dao.insertOrUpdateSettings(AdminSettings())
        }

        // Seed initial game rounds if empty
        val latestRound = dao.getLatestRoundSync()
        if (latestRound == null) {
            seedInitialRounds()
        }
    }

    private suspend fun seedInitialRounds() {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
        val todayStr = dateFormat.format(Date())
        val seedRounds = mutableListOf<GameRound>()
        val baseSeq = 1000L

        for (i in 20 downTo 1) {
            val num = Random.nextInt(0, 10)
            val colors = PredictColor.fromNumber(num)
            val colorStr = colors.joinToString("_") { it.name }
            val size = PredictSize.fromNumber(num).name
            val seq = baseSeq + i
            val period = "$todayStr${String.format(Locale.US, "%03d", seq % 1000)}"

            seedRounds.add(
                GameRound(
                    periodId = period,
                    roundSequence = seq,
                    winningNumber = num,
                    winningColors = colorStr,
                    size = size,
                    timestamp = System.currentTimeMillis() - (i * 30_000L),
                    totalBetsVolume = (Random.nextInt(50, 500) * 10).toDouble(),
                    totalPayoutVolume = (Random.nextInt(30, 450) * 10).toDouble()
                )
            )
        }
        dao.insertRounds(seedRounds)
    }

    suspend fun placeBet(
        userId: String,
        periodId: String,
        betType: BetType,
        target: String,
        baseAmount: Double,
        multiplier: Int
    ): Result<BetOrder> = withContext(Dispatchers.IO) {
        val totalAmount = baseAmount * multiplier
        val profile = dao.getProfileByIdSync(userId) ?: return@withContext Result.failure(Exception("User profile not found"))

        if (profile.balance < totalAmount) {
            return@withContext Result.failure(Exception("Insufficient balance"))
        }

        val settings = dao.getSettingsSync() ?: AdminSettings()
        if (totalAmount < settings.minBet) {
            return@withContext Result.failure(Exception("Minimum bet is ₹${settings.minBet.toInt()}"))
        }
        if (totalAmount > settings.maxBet) {
            return@withContext Result.failure(Exception("Maximum bet is ₹${settings.maxBet.toInt()}"))
        }

        val newBalance = profile.balance - totalAmount
        dao.updateProfile(
            profile.copy(
                balance = newBalance,
                totalWagered = profile.totalWagered + totalAmount,
                totalBetsCount = profile.totalBetsCount + 1,
                vipExp = profile.vipExp + (totalAmount / 10).toInt()
            )
        )

        dao.insertTransaction(
            TransactionRecord(
                userId = userId,
                type = "BET_PLACED",
                amount = -totalAmount,
                balanceAfter = newBalance,
                description = "Placed bet $target on Period $periodId"
            )
        )

        val bet = BetOrder(
            userId = userId,
            periodId = periodId,
            betType = betType,
            target = target,
            baseAmount = baseAmount,
            multiplier = multiplier,
            totalAmount = totalAmount,
            status = "PENDING"
        )
        val id = dao.insertBet(bet)
        Result.success(bet.copy(id = id))
    }

    suspend fun resolveRound(
        periodId: String,
        roundSequence: Long,
        activeUserId: String
    ): GameRound = withContext(Dispatchers.IO) {
        val settings = dao.getSettingsSync() ?: AdminSettings()
        val periodBets = dao.getBetsForPeriodSync(periodId)

        // Determine winning number based on Admin Outcome Mode
        val winningNumber: Int = when (settings.outcomeMode) {
            OutcomeMode.MANUAL -> {
                settings.manualNextNumber.coerceIn(0, 9)
            }
            OutcomeMode.FORCE_WIN -> {
                // Find active user's bet if any, and pick an outcome that pays them
                val activeBets = periodBets.filter { it.userId == activeUserId }
                if (activeBets.isNotEmpty()) {
                    val targetBet = activeBets.first()
                    when (targetBet.betType) {
                        BetType.NUMBER -> targetBet.target.toIntOrNull() ?: Random.nextInt(0, 10)
                        BetType.COLOR -> {
                            when (targetBet.target) {
                                "GREEN" -> listOf(1, 3, 7, 9).random()
                                "RED" -> listOf(2, 4, 6, 8).random()
                                "VIOLET" -> listOf(0, 5).random()
                                else -> Random.nextInt(0, 10)
                            }
                        }
                        BetType.SIZE -> {
                            if (targetBet.target == "BIG") (5..9).random() else (0..4).random()
                        }
                    }
                } else {
                    Random.nextInt(0, 10)
                }
            }
            OutcomeMode.LOW_PAYOUT -> {
                // Calculate potential payout for each number 0..9 and pick the one with lowest payout
                var lowestPayout = Double.MAX_VALUE
                var bestNumber = Random.nextInt(0, 10)

                for (cand in 0..9) {
                    val candColors = PredictColor.fromNumber(cand).map { it.name }
                    val candSize = PredictSize.fromNumber(cand).name
                    var candPayout = 0.0

                    for (bet in periodBets) {
                        when (bet.betType) {
                            BetType.NUMBER -> {
                                if (bet.target == cand.toString()) candPayout += bet.totalAmount * settings.numberMultiplier
                            }
                            BetType.COLOR -> {
                                if (candColors.contains(bet.target)) {
                                    val mult = if (bet.target == "VIOLET") settings.violetMultiplier else {
                                        if (cand == 0 || cand == 5) 1.5 else settings.colorMultiplier
                                    }
                                    candPayout += bet.totalAmount * mult
                                }
                            }
                            BetType.SIZE -> {
                                if (bet.target == candSize) candPayout += bet.totalAmount * settings.sizeMultiplier
                            }
                        }
                    }

                    if (candPayout < lowestPayout) {
                        lowestPayout = candPayout
                        bestNumber = cand
                    }
                }
                bestNumber
            }
            OutcomeMode.FAIR_RNG -> {
                Random.nextInt(0, 10)
            }
        }

        val winningColorsList = PredictColor.fromNumber(winningNumber)
        val winningColorsStr = winningColorsList.joinToString("_") { it.name }
        val winningSize = PredictSize.fromNumber(winningNumber).name

        var totalVolume = 0.0
        var totalPayout = 0.0

        val updatedBets = mutableListOf<BetOrder>()

        for (bet in periodBets) {
            totalVolume += bet.totalAmount
            var isWin = false
            var multiplier = 0.0

            when (bet.betType) {
                BetType.NUMBER -> {
                    if (bet.target == winningNumber.toString()) {
                        isWin = true
                        multiplier = settings.numberMultiplier
                    }
                }
                BetType.COLOR -> {
                    val matchesColor = winningColorsList.any { it.name == bet.target }
                    if (matchesColor) {
                        isWin = true
                        multiplier = if (bet.target == "VIOLET") {
                            settings.violetMultiplier
                        } else {
                            if (winningNumber == 0 || winningNumber == 5) 1.5 else settings.colorMultiplier
                        }
                    }
                }
                BetType.SIZE -> {
                    if (bet.target == winningSize) {
                        isWin = true
                        multiplier = settings.sizeMultiplier
                    }
                }
            }

            if (isWin) {
                val rawPayout = bet.totalAmount * multiplier
                val fee = rawPayout * (settings.houseFeePercent / 100.0)
                val netPayout = rawPayout - fee
                totalPayout += netPayout

                val updatedBet = bet.copy(
                    status = "WON",
                    payout = netPayout,
                    feeDeducted = fee
                )
                updatedBets.add(updatedBet)

                val user = dao.getProfileByIdSync(bet.userId)
                if (user != null) {
                    val updatedBal = user.balance + netPayout
                    dao.updateProfile(
                        user.copy(
                            balance = updatedBal,
                            totalWon = user.totalWon + netPayout,
                            winCount = user.winCount + 1
                        )
                    )
                    dao.insertTransaction(
                        TransactionRecord(
                            userId = bet.userId,
                            type = "WIN_PAYOUT",
                            amount = netPayout,
                            balanceAfter = updatedBal,
                            description = "Won Period $periodId (${bet.target}): +₹${String.format(Locale.US, "%.2f", netPayout)}"
                        )
                    )
                }
            } else {
                val updatedBet = bet.copy(
                    status = "LOST",
                    payout = 0.0
                )
                updatedBets.add(updatedBet)
            }
        }

        if (updatedBets.isNotEmpty()) {
            dao.updateBets(updatedBets)
        }

        val round = GameRound(
            periodId = periodId,
            roundSequence = roundSequence,
            winningNumber = winningNumber,
            winningColors = winningColorsStr,
            size = winningSize,
            timestamp = System.currentTimeMillis(),
            totalBetsVolume = totalVolume,
            totalPayoutVolume = totalPayout
        )
        dao.insertRound(round)
        round
    }

    suspend fun addFunds(userId: String, amount: Double, description: String = "Deposit via Wallet") = withContext(Dispatchers.IO) {
        val user = dao.getProfileByIdSync(userId) ?: return@withContext
        val newBalance = user.balance + amount
        dao.updateProfile(user.copy(balance = newBalance))
        dao.insertTransaction(
            TransactionRecord(
                userId = userId,
                type = "DEPOSIT",
                amount = amount,
                balanceAfter = newBalance,
                description = description
            )
        )
    }

    suspend fun withdrawFunds(userId: String, amount: Double): Result<Unit> = withContext(Dispatchers.IO) {
        val user = dao.getProfileByIdSync(userId) ?: return@withContext Result.failure(Exception("User not found"))
        if (user.balance < amount) {
            return@withContext Result.failure(Exception("Insufficient balance"))
        }
        val newBalance = user.balance - amount
        dao.updateProfile(user.copy(balance = newBalance))
        dao.insertTransaction(
            TransactionRecord(
                userId = userId,
                type = "WITHDRAWAL",
                amount = -amount,
                balanceAfter = newBalance,
                description = "Withdrawal to Bank/UPI account"
            )
        )
        Result.success(Unit)
    }

    suspend fun claimDailyBonus(userId: String): Result<Double> = withContext(Dispatchers.IO) {
        val bonus = 100.0
        val user = dao.getProfileByIdSync(userId) ?: return@withContext Result.failure(Exception("User not found"))
        val newBalance = user.balance + bonus
        dao.updateProfile(user.copy(balance = newBalance))
        dao.insertTransaction(
            TransactionRecord(
                userId = userId,
                type = "DAILY_REWARD",
                amount = bonus,
                balanceAfter = newBalance,
                description = "Daily Attendance Check-in Bonus"
            )
        )
        Result.success(bonus)
    }

    suspend fun updateAdminSettings(settings: AdminSettings) = withContext(Dispatchers.IO) {
        dao.insertOrUpdateSettings(settings)
    }

    suspend fun setUserBalance(userId: String, newBalance: Double) = withContext(Dispatchers.IO) {
        val user = dao.getProfileByIdSync(userId) ?: return@withContext
        dao.updateProfile(user.copy(balance = newBalance))
        dao.insertTransaction(
            TransactionRecord(
                userId = userId,
                type = "ADMIN_ADJUST",
                amount = newBalance - user.balance,
                balanceAfter = newBalance,
                description = "Admin manual balance adjustment"
            )
        )
    }

    suspend fun resetGameData() = withContext(Dispatchers.IO) {
        dao.clearAllRounds()
        dao.clearAllBets()
        dao.clearAllTransactions()
        seedInitialRounds()
    }
}
