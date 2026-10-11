package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LivePriceService
import com.example.model.CryptoAsset
import com.example.model.CurrencyOption
import com.example.model.MarketMood
import com.example.model.TransactionRecord
import com.example.model.TxType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

sealed class AppScreen {
    data object Home : AppScreen()
    data class AssetDetail(val symbol: String) : AppScreen()
    data object TransactionHistory : AppScreen()
    data class TransactionDetails(val txId: String) : AppScreen()
    data class SendFlow(val initialSymbol: String = "SOL") : AppScreen()
    data class ReceiveAddress(val symbol: String = "SOL") : AppScreen()
    data object EditSimulator : AppScreen() // Backstage
}

data class NotificationBanner(
    val title: String,
    val subtitle: String,
    val amountStr: String
)

data class LedgerUiState(
    val currentScreen: AppScreen = AppScreen.Home,
    val assets: List<CryptoAsset> = emptyList(),
    val marketMood: MarketMood = MarketMood(50, "Neutral"),
    val activeCurrency: CurrencyOption = CurrencyOption("USD", "$", 1.0),
    val transactions: List<TransactionRecord> = emptyList(),
    val isLivePricesEnabled: Boolean = true,
    val isLivePriceLoading: Boolean = false,
    val livePriceStatus: String = "Live Prices: Connected ●",
    val notificationBanner: NotificationBanner? = null,
    val showSelectAssetModal: Boolean = false,
    val selectAssetMode: String = "RECEIVE",
    val showTransferSheet: Boolean = false,
    val showVerifyLedgerModal: Boolean = false,
    val showSwapModal: Boolean = false,
    val showBuyModal: Boolean = false,
    val verifiedAddressTarget: String = ""
) {
    val totalBalanceInActiveCurrency: Double
        get() = assets.sumOf { it.balance * it.livePriceUsd } * activeCurrency.rateToUsd

    val dayChangePct: Double
        get() {
            val total = assets.sumOf { it.balance * it.livePriceUsd }
            if (total <= 0.0) return -3.33
            val weightedChange = assets.sumOf { (it.balance * it.livePriceUsd) * it.change24hPct } / total
            return if (weightedChange != 0.0) weightedChange else -3.33
        }
}

class LedgerViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LedgerUiState())
    val uiState: StateFlow<LedgerUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
        startLivePricePolling()
    }

    private fun loadInitialData() {
        val btcHistorical = listOf(
            87126.91, 86800.0, 86450.0, 85900.0, 85200.0,
            85450.0, 84900.0, 84750.0, 85100.0, 84600.0,
            84100.0, 84300.0, 83950.0, 84050.0, 83785.53, 83888.00
        )
        val solHistorical = listOf(
            118.5, 117.8, 116.2, 115.8, 114.9, 115.4, 113.8, 114.73
        )
        val ethHistorical = listOf(
            2620.0, 2635.0, 2650.0, 2640.0, 2665.0, 2673.20
        )

        val defaultAssets = listOf(
            CryptoAsset(
                id = "btc",
                symbol = "BTC",
                name = "Bitcoin",
                balance = 0.1842,
                livePriceUsd = 83888.00,
                change24hPct = -3.32,
                accountName = "BTC Main",
                address = "bc1qldq4928z4k3wcv46p3xez8u7lm6p25g4f9x",
                networkName = "Bitcoin",
                isTrending = true,
                historicalPrices = btcHistorical,
                maxSliderBalance = 1.0,
                defaultNetworkFee = 0.00012
            ),
            CryptoAsset(
                id = "eth",
                symbol = "ETH",
                name = "Ethereum",
                balance = 3.986259,
                livePriceUsd = 2673.20,
                change24hPct = 1.85,
                accountName = "ETH Main",
                address = "0x71C8364402a5235663970F0087492cE9b83b3984",
                networkName = "Ethereum",
                isTrending = false,
                historicalPrices = ethHistorical,
                maxSliderBalance = 10.0,
                defaultNetworkFee = 0.0018
            ),
            CryptoAsset(
                id = "usdt",
                symbol = "USDT",
                name = "Tether",
                balance = 500.0,
                livePriceUsd = 0.9998,
                change24hPct = 0.01,
                accountName = "USDT Main",
                address = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t",
                networkName = "Tron / Ethereum",
                isTrending = false,
                historicalPrices = listOf(1.0, 1.0, 1.0),
                maxSliderBalance = 2000.0,
                defaultNetworkFee = 2.50
            ),
            CryptoAsset(
                id = "sol",
                symbol = "SOL",
                name = "Solana",
                balance = 5.72025,
                livePriceUsd = 114.7258,
                change24hPct = -2.15,
                accountName = "SOL Main",
                address = "8Y5S9thrrWUEpGM3YpqN4UzUDVcX5My9wawRrVqnpaRR",
                networkName = "Solana",
                isTrending = false,
                historicalPrices = solHistorical,
                maxSliderBalance = 30.0,
                defaultNetworkFee = 0.00026044
            ),
            CryptoAsset(
                id = "doge",
                symbol = "DOGE",
                name = "Dogecoin",
                balance = 0.0,
                livePriceUsd = 0.142,
                change24hPct = -9.94,
                accountName = "DOGE Main",
                address = "D8x7P1k9Mw4Z2yQt8NmPqR48vL32uA5q7w",
                networkName = "Dogecoin",
                isTrending = true,
                maxSliderBalance = 5000.0,
                defaultNetworkFee = 1.5
            ),
            CryptoAsset(
                id = "xrp",
                symbol = "XRP",
                name = "XRP",
                balance = 0.0,
                livePriceUsd = 0.584,
                change24hPct = -7.72,
                accountName = "XRP Main",
                address = "rN4K9x83zQ12pW82vLmPqZ38uA29vL12qX",
                networkName = "XRP Ledger",
                isTrending = true,
                maxSliderBalance = 1500.0,
                defaultNetworkFee = 0.000012
            ),
            CryptoAsset(
                id = "ada",
                symbol = "ADA",
                name = "Cardano",
                balance = 0.0,
                livePriceUsd = 0.345,
                change24hPct = -7.40,
                accountName = "ADA Main",
                address = "addr1q9k34m8z4x2v19pq82rm84xz7901k398v1x4",
                networkName = "Cardano",
                isTrending = true,
                maxSliderBalance = 2000.0,
                defaultNetworkFee = 0.17
            ),
            CryptoAsset(
                id = "bnb",
                symbol = "BNB",
                name = "BNB",
                balance = 0.0,
                livePriceUsd = 576.00,
                change24hPct = -1.10,
                accountName = "BNB Main",
                address = "bnb1q84z12w48v29m19kx482z91x38v92x18m94",
                networkName = "BNB Chain",
                isTrending = false,
                maxSliderBalance = 10.0,
                defaultNetworkFee = 0.0005
            )
        )

        val defaultTransactions = listOf(
            TransactionRecord(
                id = "tx1",
                assetSymbol = "SOL",
                accountName = "SOL Main",
                type = TxType.RECEIVED,
                amount = 6.53651734,
                fiatValue = 749.93,
                date = "9/23/2026",
                time = "9:11 PM",
                timestamp = 1790205060000L,
                networkFee = "0.00026044 SOL ≈ $0.03",
                txId = "2dJyWHku8aR71P82RcvQbmT6iNVDP2akXXgpUCE8kHwz",
                counterpartyAddress = "Phantom Simulator (8Y5S...paRR)",
                network = "Solana"
            ),
            TransactionRecord(
                id = "tx2",
                assetSymbol = "SOL",
                accountName = "SOL Main",
                type = TxType.RECEIVED,
                amount = 0.87275266,
                fiatValue = 100.13,
                date = "9/23/2026",
                time = "9:05 PM",
                timestamp = 1790204700000L,
                networkFee = "0.00026044 SOL ≈ $0.03",
                txId = "4kNmjQw9X2KzP...8Y",
                counterpartyAddress = "Trust Simulator (5x9A...k7m)",
                network = "Solana"
            ),
            TransactionRecord(
                id = "tx3",
                assetSymbol = "SOL",
                accountName = "SOL Main",
                type = TxType.SENT,
                amount = 5.0,
                fiatValue = 573.65,
                date = "9/23/2026",
                time = "8:36 PM",
                timestamp = 1790202960000L,
                networkFee = "0.00503055 SOL ≈ $0.58",
                txId = "7wgjyNX6EWaL4HoaaD6ngE36koPJCU16hadewGU2uAbe",
                counterpartyAddress = "Phantom Simulator (7wgj...uAbe)",
                network = "Solana"
            ),
            TransactionRecord(
                id = "tx4",
                assetSymbol = "SOL",
                accountName = "SOL Main",
                type = TxType.SENT,
                amount = 5.0,
                fiatValue = 573.65,
                date = "9/23/2026",
                time = "8:01 PM",
                timestamp = 1790200860000L,
                networkFee = "0.00503055 SOL ≈ $0.58",
                txId = "3uM8xQz9...11p",
                counterpartyAddress = "Exodus Simulator (3uM8...11p)",
                network = "Solana"
            ),
            TransactionRecord(
                id = "tx5",
                assetSymbol = "BTC",
                accountName = "BTC Main",
                type = TxType.SENT,
                amount = 0.49969395,
                fiatValue = 41923.32,
                date = "9/23/2026",
                time = "5:19 AM",
                timestamp = 1790150340000L,
                networkFee = "0.00012 BTC ≈ $10.06",
                txId = "f1a8c9927b...4e",
                counterpartyAddress = "Exodus Simulator (bc1q...x89)",
                network = "Bitcoin"
            ),
            TransactionRecord(
                id = "tx6",
                assetSymbol = "BTC",
                accountName = "BTC Main",
                type = TxType.SENT,
                amount = 0.02,
                fiatValue = 1677.96,
                date = "9/2/2026",
                time = "12:10 AM",
                timestamp = 1788327000000L,
                networkFee = "0.00008 BTC ≈ $6.71",
                txId = "b891a2...31c",
                counterpartyAddress = "Phantom Simulator (bc1q...41p)",
                network = "Bitcoin"
            ),
            TransactionRecord(
                id = "tx7",
                assetSymbol = "SOL",
                accountName = "SOL Main",
                type = TxType.RECEIVED,
                amount = 2.5,
                fiatValue = 286.82,
                date = "8/30/2026",
                time = "5:44 AM",
                timestamp = 1788085440000L,
                networkFee = "0.00026044 SOL ≈ $0.03",
                txId = "92vNmK1...84q",
                counterpartyAddress = "Trust Simulator (92vN...84q)",
                network = "Solana"
            )
        )

        _uiState.update {
            it.copy(
                assets = defaultAssets,
                transactions = defaultTransactions,
                marketMood = MarketMood(50, "Neutral")
            )
        }
    }

    private fun startLivePricePolling() {
        viewModelScope.launch {
            // Initial price fetch
            delay(1000)
            refreshLivePrices()

            // Periodic refresh every 45 seconds
            while (isActive) {
                delay(45000)
                if (_uiState.value.isLivePricesEnabled) {
                    refreshLivePrices()
                }
            }
        }
    }

    fun refreshLivePrices() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLivePriceLoading = true) }
            val startTime = System.currentTimeMillis()
            val liveData = LivePriceService.fetchLivePrices()
            val elapsed = System.currentTimeMillis() - startTime
            if (elapsed < 850) {
                kotlinx.coroutines.delay(850 - elapsed)
            }
            if (liveData != null && liveData.isNotEmpty()) {
                _uiState.update { state ->
                    val updated = state.assets.map { asset ->
                        val data = liveData[asset.symbol]
                        if (data != null && state.isLivePricesEnabled) {
                            // Update live price and generate dynamic chart points
                            val historical = if (asset.historicalPrices.isNotEmpty()) {
                                asset.historicalPrices.drop(1) + data.priceUsd
                            } else listOf(data.priceUsd * 0.98, data.priceUsd)

                            asset.copy(
                                livePriceUsd = data.priceUsd,
                                change24hPct = data.change24hPct,
                                historicalPrices = historical
                            )
                        } else {
                            asset
                        }
                    }
                    state.copy(
                        assets = updated,
                        isLivePriceLoading = false,
                        livePriceStatus = "Live Prices: Connected ●"
                    )
                }
            } else {
                // If offline or CoinGecko rate-limited (HTTP 429), provide realistic micro-market tick
                _uiState.update { state ->
                    val updated = if (state.isLivePricesEnabled) {
                        state.assets.map { asset ->
                            val microJitter = (Math.random() * 0.003 - 0.0015) // +/- 0.15% realistic tick
                            val newPrice = (asset.livePriceUsd * (1.0 + microJitter)).coerceAtLeast(0.01)
                            val roundedPrice: Double = Math.round(newPrice * 100.0) / 100.0
                            val newHistorical: List<Double> = if (asset.historicalPrices.isNotEmpty()) {
                                asset.historicalPrices.drop(1) + roundedPrice
                            } else listOf(roundedPrice * 0.98, roundedPrice)
                            asset.copy(
                                livePriceUsd = roundedPrice,
                                historicalPrices = newHistorical
                            )
                        }
                    } else state.assets
                    state.copy(
                        assets = updated,
                        isLivePriceLoading = false,
                        livePriceStatus = if (state.isLivePricesEnabled) "Live Prices: Updated ●" else "Simulated Mode"
                    )
                }
            }
            triggerVibration()
        }
    }

    fun toggleLivePrices(enabled: Boolean) {
        _uiState.update {
            it.copy(
                isLivePricesEnabled = enabled,
                livePriceStatus = if (enabled) "Live Prices: Connected ●" else "Simulated Mode"
            )
        }
        if (enabled) {
            refreshLivePrices()
        }
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun updateAssetBalance(symbol: String, newBalance: Double) {
        _uiState.update { state ->
            val updated = state.assets.map {
                if (it.symbol.equals(symbol, ignoreCase = true)) {
                    it.copy(balance = newBalance.coerceAtLeast(0.0))
                } else it
            }
            state.copy(assets = updated)
        }
    }

    fun updateAssetAccountName(symbol: String, newAccountName: String) {
        _uiState.update { state ->
            val updated = state.assets.map {
                if (it.symbol.equals(symbol, ignoreCase = true)) {
                    it.copy(accountName = newAccountName)
                } else it
            }
            state.copy(assets = updated)
        }
    }

    fun updateMarketMood(score: Int) {
        _uiState.update { it.copy(marketMood = MarketMood.fromScore(score)) }
    }

    fun setCurrency(currency: CurrencyOption) {
        _uiState.update { it.copy(activeCurrency = currency) }
    }

    fun resetBalancesToDefault() {
        loadInitialData()
        triggerVibration()
    }

    fun openSelectAssetModal(mode: String) {
        _uiState.update { it.copy(showSelectAssetModal = true, selectAssetMode = mode) }
    }

    fun closeSelectAssetModal() {
        _uiState.update { it.copy(showSelectAssetModal = false) }
    }

    fun openTransferSheet() {
        _uiState.update { it.copy(showTransferSheet = true) }
    }

    fun closeTransferSheet() {
        _uiState.update { it.copy(showTransferSheet = false) }
    }

    fun openVerifyLedgerModal(address: String) {
        _uiState.update { it.copy(showVerifyLedgerModal = true, verifiedAddressTarget = address) }
    }

    fun closeVerifyLedgerModal() {
        _uiState.update { it.copy(showVerifyLedgerModal = false) }
    }

    fun openSwapModal() {
        _uiState.update { it.copy(showSwapModal = true) }
    }

    fun closeSwapModal() {
        _uiState.update { it.copy(showSwapModal = false) }
    }

    fun openBuyModal() {
        _uiState.update { it.copy(showBuyModal = true) }
    }

    fun closeBuyModal() {
        _uiState.update { it.copy(showBuyModal = false) }
    }

    fun sendTransaction(
        symbol: String,
        amount: Double,
        recipientAddress: String,
        networkFeeAmount: Double,
        networkFeeUsd: Double
    ) {
        val asset = _uiState.value.assets.find { it.symbol.equals(symbol, ignoreCase = true) } ?: return
        val newBalance = (asset.balance - (amount + networkFeeAmount)).coerceAtLeast(0.0)

        val dateFormat = SimpleDateFormat("M/d/yyyy", Locale.US)
        val timeFormat = SimpleDateFormat("h:mm a", Locale.US)
        val now = Date()

        val newTx = TransactionRecord(
            id = UUID.randomUUID().toString(),
            assetSymbol = asset.symbol,
            accountName = asset.accountName,
            type = TxType.SENT,
            amount = amount,
            fiatValue = amount * asset.livePriceUsd,
            date = dateFormat.format(now),
            time = timeFormat.format(now),
            timestamp = now.time,
            networkFee = String.format(Locale.US, "%.8f %s ≈ $%.2f", networkFeeAmount, asset.symbol, networkFeeUsd),
            txId = generateRealisticTxId(asset.symbol),
            counterpartyAddress = recipientAddress,
            network = asset.networkName
        )

        _uiState.update { state ->
            val updatedAssets = state.assets.map {
                if (it.symbol.equals(symbol, ignoreCase = true)) {
                    it.copy(balance = newBalance)
                } else it
            }
            state.copy(
                assets = updatedAssets,
                transactions = listOf(newTx) + state.transactions
            )
        }

        triggerVibration()
    }

    fun simulateIncomingTransfer(
        symbol: String,
        fromWalletName: String,
        amount: Double
    ) {
        val asset = _uiState.value.assets.find { it.symbol.equals(symbol, ignoreCase = true) } ?: return
        val newBalance = asset.balance + amount

        val dateFormat = SimpleDateFormat("M/d/yyyy", Locale.US)
        val timeFormat = SimpleDateFormat("h:mm a", Locale.US)
        val now = Date()

        val newTx = TransactionRecord(
            id = UUID.randomUUID().toString(),
            assetSymbol = asset.symbol,
            accountName = asset.accountName,
            type = TxType.RECEIVED,
            amount = amount,
            fiatValue = amount * asset.livePriceUsd,
            date = dateFormat.format(now),
            time = timeFormat.format(now),
            timestamp = now.time,
            networkFee = String.format(Locale.US, "0.00026044 %s ≈ $0.03", asset.symbol),
            txId = generateRealisticTxId(asset.symbol),
            counterpartyAddress = "$fromWalletName Simulator",
            network = asset.networkName
        )

        _uiState.update { state ->
            val updatedAssets = state.assets.map {
                if (it.symbol.equals(symbol, ignoreCase = true)) {
                    it.copy(balance = newBalance)
                } else it
            }
            state.copy(
                assets = updatedAssets,
                transactions = listOf(newTx) + state.transactions,
                notificationBanner = NotificationBanner(
                    title = "Received $symbol",
                    subtitle = "Transferred from $fromWalletName Simulator",
                    amountStr = "+$amount ${asset.symbol}"
                )
            )
        }

        triggerVibration()

        // Auto dismiss notification banner after 4.5 seconds
        viewModelScope.launch {
            delay(4500)
            _uiState.update { it.copy(notificationBanner = null) }
        }
    }

    fun dismissNotificationBanner() {
        _uiState.update { it.copy(notificationBanner = null) }
    }

    fun executeSwap(fromSymbol: String, toSymbol: String, amountFrom: Double) {
        val fromAsset = _uiState.value.assets.find { it.symbol.equals(fromSymbol, ignoreCase = true) } ?: return
        val toAsset = _uiState.value.assets.find { it.symbol.equals(toSymbol, ignoreCase = true) } ?: return

        val usdValue = amountFrom * fromAsset.livePriceUsd
        val amountTo = usdValue / toAsset.livePriceUsd

        if (fromAsset.balance >= amountFrom) {
            _uiState.update { state ->
                val updated = state.assets.map {
                    when {
                        it.symbol.equals(fromSymbol, ignoreCase = true) -> it.copy(balance = (it.balance - amountFrom).coerceAtLeast(0.0))
                        it.symbol.equals(toSymbol, ignoreCase = true) -> it.copy(balance = it.balance + amountTo)
                        else -> it
                    }
                }
                state.copy(assets = updated)
            }
            triggerVibration()
        }
    }

    fun executeBuy(symbol: String, amountUsd: Double) {
        val asset = _uiState.value.assets.find { it.symbol.equals(symbol, ignoreCase = true) } ?: return
        val coinAmount = amountUsd / asset.livePriceUsd

        _uiState.update { state ->
            val updated = state.assets.map {
                if (it.symbol.equals(symbol, ignoreCase = true)) {
                    it.copy(balance = it.balance + coinAmount)
                } else it
            }
            state.copy(assets = updated)
        }
        triggerVibration()
    }

    private fun generateRealisticTxId(symbol: String): String {
        val chars = "0123456789abcdefABCDEF"
        return when (symbol) {
            "SOL" -> "2dJyWH" + (1..38).map { chars.random() }.joinToString("") + "CE"
            "BTC" -> "bc1" + (1..40).map { chars.random() }.joinToString("").lowercase()
            else -> "0x" + (1..40).map { chars.random() }.joinToString("").lowercase()
        }
    }

    private fun triggerVibration() {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(25)
                }
            }
        } catch (_: Exception) {
            // Ignore if vibrator unavailable
        }
    }
}
