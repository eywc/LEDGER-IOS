package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class CoinPriceData(
    val priceUsd: Double,
    val change24hPct: Double
)

object LivePriceService {

    private const val API_URL =
        "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin,ethereum,tether,solana,dogecoin,ripple,cardano,binancecoin&vs_currencies=usd&include_24hr_change=true"

    suspend fun fetchLivePrices(): Map<String, CoinPriceData>? = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(API_URL)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 6000
                readTimeout = 6000
                setRequestProperty("User-Agent", "LedgerWalletSimulator/1.0")
                setRequestProperty("Accept", "application/json")
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()

                val json = JSONObject(response)
                val results = mutableMapOf<String, CoinPriceData>()

                val mapping = mapOf(
                    "BTC" to "bitcoin",
                    "ETH" to "ethereum",
                    "USDT" to "tether",
                    "SOL" to "solana",
                    "DOGE" to "dogecoin",
                    "XRP" to "ripple",
                    "ADA" to "cardano",
                    "BNB" to "binancecoin"
                )

                for ((symbol, id) in mapping) {
                    if (json.has(id)) {
                        val obj = json.getJSONObject(id)
                        val price = obj.optDouble("usd", 0.0)
                        val change = obj.optDouble("usd_24h_change", 0.0)
                        if (price > 0.0) {
                            results[symbol] = CoinPriceData(price, change)
                        }
                    }
                }

                results
            } else {
                null
            }
        } catch (_: Exception) {
            null
        } finally {
            connection?.disconnect()
        }
    }
}
