package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.AdminSettings
import com.example.data.model.BetOrder
import com.example.data.model.BetType
import com.example.data.model.GameRound
import com.example.data.model.OutcomeMode
import com.example.data.model.TransactionRecord
import com.example.data.model.UserProfile
import com.example.data.repository.GameRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository

    init {
        val db = AppDatabase.getInstance(application)
        repository = GameRepository(db.gameDao())
        viewModelScope.launch {
            repository.initializeDatabaseIfEmpty()
            startGameTimerLoop()
        }
    }

    // --- State ---
    val allProfiles: StateFlow<List<UserProfile>> = repository.allProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeUserId = MutableStateFlow("copy_1")
    val activeUserId: StateFlow<String> = _activeUserId.asStateFlow()

    val activeProfile: StateFlow<UserProfile?> = _activeUserId
        .flatMapLatest { id -> repository.getProfile(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val recentRounds: StateFlow<List<GameRound>> = repository.recentRounds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userBets: StateFlow<List<BetOrder>> = _activeUserId
        .flatMapLatest { id -> repository.getBetsForUser(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userTransactions: StateFlow<List<TransactionRecord>> = _activeUserId
        .flatMapLatest { id -> repository.getTransactionsForUser(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminSettings: StateFlow<AdminSettings> = repository.adminSettings
        .flatMapLatest { settings ->
            MutableStateFlow(settings ?: AdminSettings())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminSettings())

    // --- Round & Timer State ---
    private val _currentPeriodId = MutableStateFlow(generatePeriodId(1001L))
    val currentPeriodId: StateFlow<String> = _currentPeriodId.asStateFlow()

    private val _currentRoundSeq = MutableStateFlow(1001L)
    val currentRoundSeq: StateFlow<Long> = _currentRoundSeq.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(30)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    private val _totalDurationSeconds = MutableStateFlow(30)
    val totalDurationSeconds: StateFlow<Int> = _totalDurationSeconds.asStateFlow()

    private val _isBettingLocked = MutableStateFlow(false)
    val isBettingLocked: StateFlow<Boolean> = _isBettingLocked.asStateFlow()

    // --- Result Announcement ---
    private val _lastResultRound = MutableStateFlow<GameRound?>(null)
    val lastResultRound: StateFlow<GameRound?> = _lastResultRound.asStateFlow()

    private val _showResultDialog = MutableStateFlow(false)
    val showResultDialog: StateFlow<Boolean> = _showResultDialog.asStateFlow()

    private val _userRoundPayout = MutableStateFlow(0.0)
    val userRoundPayout: StateFlow<Double> = _userRoundPayout.asStateFlow()

    private val _userRoundWon = MutableStateFlow(false)
    val userRoundWon: StateFlow<Boolean> = _userRoundWon.asStateFlow()

    // --- Betting UI Selection ---
    private val _selectedChipAmount = MutableStateFlow(10.0)
    val selectedChipAmount: StateFlow<Double> = _selectedChipAmount.asStateFlow()

    private val _selectedMultiplier = MutableStateFlow(1)
    val selectedMultiplier: StateFlow<Int> = _selectedMultiplier.asStateFlow()

    // UI Navigation Tab
    private val _activeTab = MutableStateFlow(0) // 0: Game Records, 1: Trend Chart, 2: My Orders
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    // Notification toast / snackbar
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private var timerJob: Job? = null

    private fun generatePeriodId(seq: Long): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
        val dateStr = dateFormat.format(Date())
        return "$dateStr${String.format(Locale.US, "%03d", seq % 1000)}"
    }

    private fun startGameTimerLoop() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            // Find highest sequence from existing rounds
            val latest = repository.recentRounds.firstOrNull()?.firstOrNull()
            var currentSeq = (latest?.roundSequence ?: 1000L) + 1L
            _currentRoundSeq.value = currentSeq
            _currentPeriodId.value = generatePeriodId(currentSeq)

            while (true) {
                val duration = adminSettings.value.roundDurationSeconds.coerceAtLeast(10)
                _totalDurationSeconds.value = duration
                _remainingSeconds.value = duration
                _isBettingLocked.value = false

                // Countdown loop
                for (sec in duration downTo 1) {
                    _remainingSeconds.value = sec
                    if (sec <= 5) {
                        _isBettingLocked.value = true
                    }
                    delay(1000L)
                }

                // Period Over! Settle round
                _remainingSeconds.value = 0
                val settledPeriod = _currentPeriodId.value
                val settledRound = repository.resolveRound(
                    periodId = settledPeriod,
                    roundSequence = currentSeq,
                    activeUserId = _activeUserId.value
                )

                // Check active user's outcome for celebration dialog
                val userBetsInRound = repository.getBetsForUser(_activeUserId.value).firstOrNull()
                    ?.filter { it.periodId == settledPeriod } ?: emptyList()

                val totalWon = userBetsInRound.filter { it.status == "WON" }.sumOf { it.payout }
                val hasPlacedBets = userBetsInRound.isNotEmpty()

                _lastResultRound.value = settledRound
                _userRoundPayout.value = totalWon
                _userRoundWon.value = totalWon > 0.0

                if (hasPlacedBets) {
                    _showResultDialog.value = true
                }

                // Increment to next period
                currentSeq++
                _currentRoundSeq.value = currentSeq
                _currentPeriodId.value = generatePeriodId(currentSeq)

                delay(1500L) // brief breather before next round starts
            }
        }
    }

    fun dismissResultDialog() {
        _showResultDialog.value = false
    }

    fun setActiveProfile(profileId: String) {
        _activeUserId.value = profileId
        _snackbarMessage.value = "Switched to ${if (profileId == "copy_1") "App Copy 1 (Alpha)" else "App Copy 2 (Beta)"}"
    }

    fun setChipAmount(amount: Double) {
        _selectedChipAmount.value = amount
    }

    fun setMultiplier(mult: Int) {
        _selectedMultiplier.value = mult
    }

    fun setActiveTab(tabIndex: Int) {
        _activeTab.value = tabIndex
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun placeBet(betType: BetType, target: String) {
        if (_isBettingLocked.value) {
            _snackbarMessage.value = "Betting is locked for current round!"
            return
        }

        val baseAmount = _selectedChipAmount.value
        val mult = _selectedMultiplier.value
        val totalAmount = baseAmount * mult

        val profile = activeProfile.value
        if (profile == null || profile.balance < totalAmount) {
            _snackbarMessage.value = "Insufficient balance! Please recharge wallet."
            return
        }

        viewModelScope.launch {
            val result = repository.placeBet(
                userId = _activeUserId.value,
                periodId = _currentPeriodId.value,
                betType = betType,
                target = target,
                baseAmount = baseAmount,
                multiplier = mult
            )
            result.onSuccess {
                _snackbarMessage.value = "Bet placed: $target for ₹${totalAmount.toInt()}"
            }.onFailure { err ->
                _snackbarMessage.value = err.message ?: "Failed to place bet"
            }
        }
    }

    fun depositFunds(amount: Double) {
        viewModelScope.launch {
            repository.addFunds(_activeUserId.value, amount, "Wallet Deposit (+₹${amount.toInt()})")
            _snackbarMessage.value = "Recharged ₹${amount.toInt()} successfully!"
        }
    }

    fun withdrawFunds(amount: Double) {
        viewModelScope.launch {
            val res = repository.withdrawFunds(_activeUserId.value, amount)
            res.onSuccess {
                _snackbarMessage.value = "Withdrawal request of ₹${amount.toInt()} approved!"
            }.onFailure {
                _snackbarMessage.value = it.message ?: "Withdrawal failed"
            }
        }
    }

    fun claimDailyBonus() {
        viewModelScope.launch {
            val res = repository.claimDailyBonus(_activeUserId.value)
            res.onSuccess { bonus ->
                _snackbarMessage.value = "Claimed daily attendance reward: +₹${bonus.toInt()}!"
            }
        }
    }

    // --- Admin Functions ---
    fun updateAdminSettings(newSettings: AdminSettings) {
        viewModelScope.launch {
            repository.updateAdminSettings(newSettings)
            _snackbarMessage.value = "Admin settings updated successfully!"
        }
    }

    fun adminSetUserBalance(userId: String, newBalance: Double) {
        viewModelScope.launch {
            repository.setUserBalance(userId, newBalance)
            _snackbarMessage.value = "Updated balance for $userId to ₹${newBalance.toInt()}"
        }
    }

    fun adminResetGameData() {
        viewModelScope.launch {
            repository.resetGameData()
            _snackbarMessage.value = "Game records reset with fresh initial rounds!"
        }
    }
}
