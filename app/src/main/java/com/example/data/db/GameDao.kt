package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AdminSettings
import com.example.data.model.BetOrder
import com.example.data.model.GameRound
import com.example.data.model.TransactionRecord
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    // --- User Profiles ---
    @Query("SELECT * FROM user_profiles ORDER BY id ASC")
    fun getAllProfiles(): Flow<List<UserProfile>>

    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    fun getProfileById(id: String): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileByIdSync(id: String): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfile)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profiles: List<UserProfile>)

    @Update
    suspend fun updateProfile(profile: UserProfile)

    @Query("UPDATE user_profiles SET balance = :newBalance WHERE id = :id")
    suspend fun updateBalance(id: String, newBalance: Double)

    // --- Game Rounds ---
    @Query("SELECT * FROM game_rounds ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentRounds(limit: Int = 100): Flow<List<GameRound>>

    @Query("SELECT * FROM game_rounds ORDER BY timestamp DESC LIMIT 1")
    fun getLatestRound(): Flow<GameRound?>

    @Query("SELECT * FROM game_rounds ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestRoundSync(): GameRound?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRound(round: GameRound)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRounds(rounds: List<GameRound>)

    @Query("DELETE FROM game_rounds")
    suspend fun clearAllRounds()

    // --- Bet Orders ---
    @Query("SELECT * FROM bet_orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun getBetsForUser(userId: String): Flow<List<BetOrder>>

    @Query("SELECT * FROM bet_orders WHERE periodId = :periodId")
    fun getBetsForPeriod(periodId: String): Flow<List<BetOrder>>

    @Query("SELECT * FROM bet_orders WHERE periodId = :periodId")
    suspend fun getBetsForPeriodSync(periodId: String): List<BetOrder>

    @Query("SELECT * FROM bet_orders ORDER BY createdAt DESC LIMIT :limit")
    fun getAllRecentBets(limit: Int = 100): Flow<List<BetOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBet(bet: BetOrder): Long

    @Update
    suspend fun updateBet(bet: BetOrder)

    @Update
    suspend fun updateBets(bets: List<BetOrder>)

    @Query("DELETE FROM bet_orders")
    suspend fun clearAllBets()

    // --- Admin Settings ---
    @Query("SELECT * FROM admin_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<AdminSettings?>

    @Query("SELECT * FROM admin_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsSync(): AdminSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: AdminSettings)

    // --- Transactions ---
    @Query("SELECT * FROM transaction_records WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsForUser(userId: String): Flow<List<TransactionRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: TransactionRecord)

    @Query("DELETE FROM transaction_records")
    suspend fun clearAllTransactions()
}
