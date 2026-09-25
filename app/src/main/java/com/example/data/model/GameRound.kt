package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_rounds")
data class GameRound(
    @PrimaryKey
    val periodId: String, // e.g. "20260925001"
    val roundSequence: Long,
    val winningNumber: Int, // 0-9
    val winningColors: String, // e.g. "GREEN", "RED_VIOLET", etc.
    val size: String, // "BIG" or "SMALL"
    val timestamp: Long = System.currentTimeMillis(),
    val totalBetsVolume: Double = 0.0,
    val totalPayoutVolume: Double = 0.0
)
