package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.CryptoAsset
import com.example.ui.components.BuyBottomSheet
import com.example.ui.components.NotificationBannerView
import com.example.ui.components.SwapBottomSheet
import com.example.ui.components.TransferActionSheet
import com.example.ui.components.VerifyLedgerHardwareDialog
import com.example.ui.screens.AssetDetailScreen
import com.example.ui.screens.EditSimulatorSheet
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ReceiveAddressScreen
import com.example.ui.screens.SelectAssetBottomSheet
import com.example.ui.screens.SendFlowScreen
import com.example.ui.screens.TransactionDetailsScreen
import com.example.ui.screens.TransactionHistoryScreen
import com.example.ui.theme.LedgerTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.LedgerViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LedgerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LedgerTheme {
                LedgerApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun LedgerApp(viewModel: LedgerViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        // Main Screen Transitions
        Crossfade(targetState = uiState.currentScreen, label = "screen_crossfade") { screen ->
            when (screen) {
                is AppScreen.Home -> {
                    HomeScreen(
                        uiState = uiState,
                        onRefresh = { viewModel.refreshLivePrices() },
                        onOpenAssetDetail = { symbol ->
                            viewModel.navigateTo(AppScreen.AssetDetail(symbol))
                        },
                        onOpenHistory = {
                            viewModel.navigateTo(AppScreen.TransactionHistory)
                        },
                        onOpenSearch = {
                            viewModel.openSelectAssetModal("RECEIVE")
                        },
                        onOpenTransferSheet = {
                            viewModel.openTransferSheet()
                        },
                        onOpenSwap = {
                            viewModel.openSwapModal()
                        },
                        onOpenBuy = {
                            viewModel.openBuyModal()
                        },
                        onOpenEditSheet = {
                            viewModel.navigateTo(AppScreen.EditSimulator)
                        }
                    )
                }

                is AppScreen.AssetDetail -> {
                    val asset = uiState.assets.find { it.symbol.equals(screen.symbol, ignoreCase = true) }
                        ?: uiState.assets.first()
                    AssetDetailScreen(
                        asset = asset,
                        currencySymbol = uiState.activeCurrency.symbol,
                        currencyRate = uiState.activeCurrency.rateToUsd,
                        onBack = { viewModel.navigateTo(AppScreen.Home) },
                        onSend = { symbol ->
                            viewModel.navigateTo(AppScreen.SendFlow(symbol))
                        },
                        onReceive = { symbol ->
                            viewModel.navigateTo(AppScreen.ReceiveAddress(symbol))
                        },
                        onBuy = { viewModel.openBuyModal() },
                        onSwap = { viewModel.openSwapModal() },
                        onRefresh = { viewModel.refreshLivePrices() },
                        isRefreshing = uiState.isLivePriceLoading
                    )
                }

                is AppScreen.TransactionHistory -> {
                    TransactionHistoryScreen(
                        transactions = uiState.transactions,
                        currencySymbol = uiState.activeCurrency.symbol,
                        onBack = { viewModel.navigateTo(AppScreen.Home) },
                        onSelectTx = { txId ->
                            viewModel.navigateTo(AppScreen.TransactionDetails(txId))
                        }
                    )
                }

                is AppScreen.TransactionDetails -> {
                    val tx = uiState.transactions.find { it.id == screen.txId }
                        ?: uiState.transactions.first()
                    TransactionDetailsScreen(
                        tx = tx,
                        currencySymbol = uiState.activeCurrency.symbol,
                        onBack = { viewModel.navigateTo(AppScreen.TransactionHistory) }
                    )
                }

                is AppScreen.SendFlow -> {
                    val asset = uiState.assets.find { it.symbol.equals(screen.initialSymbol, ignoreCase = true) }
                        ?: uiState.assets.first()
                    SendFlowScreen(
                        asset = asset,
                        currencySymbol = uiState.activeCurrency.symbol,
                        currencyRate = uiState.activeCurrency.rateToUsd,
                        onClose = { viewModel.navigateTo(AppScreen.Home) },
                        onConfirmSend = { symbol, amount, recipient, feeAmount, feeUsd ->
                            viewModel.sendTransaction(symbol, amount, recipient, feeAmount, feeUsd)
                            viewModel.navigateTo(AppScreen.TransactionHistory)
                        }
                    )
                }

                is AppScreen.ReceiveAddress -> {
                    val asset = uiState.assets.find { it.symbol.equals(screen.symbol, ignoreCase = true) }
                        ?: uiState.assets.first()
                    ReceiveAddressScreen(
                        asset = asset,
                        onClose = { viewModel.navigateTo(AppScreen.Home) },
                        onVerifyAddress = { address ->
                            viewModel.openVerifyLedgerModal(address)
                        }
                    )
                }

                is AppScreen.EditSimulator -> {
                    EditSimulatorSheet(
                        uiState = uiState,
                        onClose = { viewModel.navigateTo(AppScreen.Home) },
                        onUpdateBalance = { symbol, newBal ->
                            viewModel.updateAssetBalance(symbol, newBal)
                        },
                        onUpdateAccountName = { symbol, newName ->
                            viewModel.updateAssetAccountName(symbol, newName)
                        },
                        onUpdateMood = { score ->
                            viewModel.updateMarketMood(score)
                        },
                        onSelectCurrency = { curr ->
                            viewModel.setCurrency(curr)
                        },
                        onResetDefaults = {
                            viewModel.resetBalancesToDefault()
                        },
                        onToggleLivePrices = { enabled ->
                            viewModel.toggleLivePrices(enabled)
                        },
                        onRefreshLivePrices = {
                            viewModel.refreshLivePrices()
                        },
                        onSimulateIncoming = { symbol, wallet, amount ->
                            viewModel.simulateIncomingTransfer(symbol, wallet, amount)
                        }
                    )
                }
            }
        }

        // Top Floating Notification Banner (Transfers Arrived)
        NotificationBannerView(
            banner = uiState.notificationBanner,
            onDismiss = { viewModel.dismissNotificationBanner() },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Select Asset Modal
        if (uiState.showSelectAssetModal) {
            SelectAssetBottomSheet(
                assets = uiState.assets,
                currencySymbol = uiState.activeCurrency.symbol,
                currencyRate = uiState.activeCurrency.rateToUsd,
                title = if (uiState.selectAssetMode == "SEND") "Send asset" else "Receive asset",
                onDismiss = { viewModel.closeSelectAssetModal() },
                onSelectAsset = { selectedCoin: CryptoAsset ->
                    viewModel.closeSelectAssetModal()
                    if (uiState.selectAssetMode == "SEND") {
                        viewModel.navigateTo(AppScreen.SendFlow(selectedCoin.symbol))
                    } else {
                        viewModel.navigateTo(AppScreen.ReceiveAddress(selectedCoin.symbol))
                    }
                }
            )
        }

        // Transfer Action Sheet (Send or Receive)
        if (uiState.showTransferSheet) {
            TransferActionSheet(
                onDismiss = { viewModel.closeTransferSheet() },
                onSelectSend = {
                    viewModel.openSelectAssetModal("SEND")
                },
                onSelectReceive = {
                    viewModel.openSelectAssetModal("RECEIVE")
                }
            )
        }

        // Verify Address on Ledger Hardware Modal
        if (uiState.showVerifyLedgerModal) {
            VerifyLedgerHardwareDialog(
                address = uiState.verifiedAddressTarget,
                onDismiss = { viewModel.closeVerifyLedgerModal() }
            )
        }

        // Swap Modal
        if (uiState.showSwapModal) {
            SwapBottomSheet(
                assets = uiState.assets,
                currencySymbol = uiState.activeCurrency.symbol,
                onDismiss = { viewModel.closeSwapModal() },
                onExecuteSwap = { fromSymbol, toSymbol, amount ->
                    viewModel.executeSwap(fromSymbol, toSymbol, amount)
                }
            )
        }

        // Buy Modal
        if (uiState.showBuyModal) {
            BuyBottomSheet(
                assets = uiState.assets,
                currencySymbol = uiState.activeCurrency.symbol,
                onDismiss = { viewModel.closeBuyModal() },
                onExecuteBuy = { symbol, amountUsd ->
                    viewModel.executeBuy(symbol, amountUsd)
                }
            )
        }
    }
}
