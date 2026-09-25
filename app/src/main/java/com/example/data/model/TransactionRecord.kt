package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_records")
data class TransactionRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val type: String, // "DEPOSIT", "WITHDRAWAL", "BET_PLACED", "WIN_PAYOUT", "VIP_BONUS"
    val amount: Double,
    val balanceAfter: Double,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
