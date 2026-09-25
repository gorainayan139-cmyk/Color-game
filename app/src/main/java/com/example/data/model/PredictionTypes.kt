package com.example.data.model

enum class PredictColor(val displayName: String, val hexColor: Long, val defaultMultiplier: Double) {
    GREEN("Green", 0xFF10B981, 2.0),
    VIOLET("Violet", 0xFF8B5CF6, 4.5),
    RED("Red", 0xFFEF4444, 2.0);

    companion object {
        fun fromNumber(number: Int): List<PredictColor> {
            return when (number) {
                0 -> listOf(RED, VIOLET)
                5 -> listOf(GREEN, VIOLET)
                1, 3, 7, 9 -> listOf(GREEN)
                2, 4, 6, 8 -> listOf(RED)
                else -> listOf(GREEN)
            }
        }
    }
}

enum class PredictSize(val displayName: String, val defaultMultiplier: Double) {
    BIG("Big (5-9)", 2.0),
    SMALL("Small (0-4)", 2.0);

    companion object {
        fun fromNumber(number: Int): PredictSize {
            return if (number >= 5) BIG else SMALL
        }
    }
}

enum class BetType {
    COLOR,
    NUMBER,
    SIZE
}

enum class OutcomeMode(val displayName: String, val description: String) {
    FAIR_RNG("Fair RNG", "Random outcome using pseudo-random generator"),
    LOW_PAYOUT("House Edge", "Automatically picks outcome that pays minimum to users"),
    FORCE_WIN("High Win Rate", "Favors the active player's bets for testing"),
    MANUAL("Manual Override", "Rig the exact next winning number")
}
