package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_settings")
data class AdminSettings(
    @PrimaryKey
    val id: Int = 1,
    val roundDurationSeconds: Int = 30,
    val outcomeMode: OutcomeMode = OutcomeMode.FAIR_RNG,
    val manualNextNumber: Int = 7,
    val colorMultiplier: Double = 2.0,
    val violetMultiplier: Double = 4.5,
    val numberMultiplier: Double = 9.0,
    val sizeMultiplier: Double = 2.0,
    val houseFeePercent: Double = 2.0,
    val minBet: Double = 10.0,
    val maxBet: Double = 50000.0,
    val adminPin: String = "1234"
)
