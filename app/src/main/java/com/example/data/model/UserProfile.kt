package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey
    val id: String, // "copy_1", "copy_2", etc.
    val displayName: String,
    val avatarTag: String = "ALPHA",
    val balance: Double = 10000.0,
    val totalWagered: Double = 0.0,
    val totalWon: Double = 0.0,
    val totalBetsCount: Int = 0,
    val winCount: Int = 0,
    val vipLevel: Int = 1,
    val vipExp: Int = 0,
    val isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val winRatePercent: Double
        get() = if (totalBetsCount > 0) (winCount.toDouble() / totalBetsCount) * 100.0 else 0.0

    val netProfit: Double
        get() = totalWon - totalWagered
}
