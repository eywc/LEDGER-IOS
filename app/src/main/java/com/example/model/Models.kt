package com.example.model

enum class TxType {
    RECEIVED, SENT
}

data class CryptoAsset(
    val id: String,
    val symbol: String,
    val name: String,
    val balance: Double,
    val livePriceUsd: Double,
    val change24hPct: Double,
    val accountName: String,
    val address: String,
    val networkName: String,
    val isTrending: Boolean = false,
    val historicalPrices: List<Double> = emptyList(),
    val maxSliderBalance: Double = 10.0,
    val defaultNetworkFee: Double = 0.0001
) {
    val totalFiatValue: Double
        get() = balance * livePriceUsd
}

data class TransactionRecord(
    val id: String,
    val assetSymbol: String,
    val accountName: String,
    val type: TxType,
    val amount: Double,
    val fiatValue: Double,
    val date: String,
    val time: String,
    val timestamp: Long,
    val networkFee: String,
    val txId: String,
    val counterpartyAddress: String,
    val network: String
)

data class CurrencyOption(
    val code: String,
    val symbol: String,
    val rateToUsd: Double
)

data class MarketMood(
    val score: Int = 50,
    val label: String = "Neutral"
) {
    companion object {
        fun fromScore(score: Int): MarketMood {
            val s = score.coerceIn(0, 100)
            val lbl = when {
                s < 20 -> "Extreme Fear"
                s < 45 -> "Fear"
                s < 55 -> "Neutral"
                s < 80 -> "Greed"
                else -> "Extreme Greed"
            }
            return MarketMood(s, lbl)
        }
    }
}
