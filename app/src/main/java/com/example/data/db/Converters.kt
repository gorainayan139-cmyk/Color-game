package com.example.data.db

import androidx.room.TypeConverter
import com.example.data.model.BetType
import com.example.data.model.OutcomeMode

class Converters {
    @TypeConverter
    fun fromBetType(value: BetType): String = value.name

    @TypeConverter
    fun toBetType(value: String): BetType = try {
        BetType.valueOf(value)
    } catch (_: Exception) {
        BetType.COLOR
    }

    @TypeConverter
    fun fromOutcomeMode(value: OutcomeMode): String = value.name

    @TypeConverter
    fun toOutcomeMode(value: String): OutcomeMode = try {
        OutcomeMode.valueOf(value)
    } catch (_: Exception) {
        OutcomeMode.FAIR_RNG
    }
}
