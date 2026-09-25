package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bet_orders")
data class BetOrder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String, // "copy_1", "copy_2"
    val periodId: String,
    val betType: BetType,
    val target: String, // "GREEN", "RED", "VIOLET", "BIG", "SMALL", or "0".."9"
    val baseAmount: Double,
    val multiplier: Int,
    val totalAmount: Double,
    val status: String = "PENDING", // "PENDING", "WON", "LOST"
    val payout: Double = 0.0,
    val feeDeducted: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)
