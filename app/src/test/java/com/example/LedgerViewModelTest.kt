package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.model.TxType
import com.example.viewmodel.LedgerViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LedgerViewModelTest {

    @Test
    fun testInitialLedgerState() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = LedgerViewModel(app)
        val state = viewModel.uiState.value

        // Assets should have BTC, ETH, USDT, SOL
        assertTrue(state.assets.isNotEmpty())
        val btc = state.assets.find { it.symbol == "BTC" }
        assertNotNull(btc)
        assertEquals(0.1842, btc!!.balance, 0.0001)

        // Total initial balance should be around ~$27,264
        val total = state.totalBalanceInActiveCurrency
        assertTrue("Total balance was $total", total in 27000.0..28000.0)

        // Transactions list populated with authentic transfers
        assertTrue(state.transactions.isNotEmpty())
        val firstTx = state.transactions.first()
        assertEquals(TxType.RECEIVED, firstTx.type)
        assertEquals("SOL", firstTx.assetSymbol)
    }

    @Test
    fun testSendTransactionDeductsBalance() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = LedgerViewModel(app)
        val initialSol = viewModel.uiState.value.assets.find { it.symbol == "SOL" }!!.balance

        viewModel.sendTransaction(
            symbol = "SOL",
            amount = 1.0,
            recipientAddress = "Phantom Simulator (8Y5S...paRR)",
            networkFeeAmount = 0.005,
            networkFeeUsd = 0.58
        )

        val updatedSol = viewModel.uiState.value.assets.find { it.symbol == "SOL" }!!.balance
        assertEquals(initialSol - 1.005, updatedSol, 0.0001)

        val latestTx = viewModel.uiState.value.transactions.first()
        assertEquals(TxType.SENT, latestTx.type)
        assertEquals(1.0, latestTx.amount, 0.0001)
    }

    @Test
    fun testSimulateIncomingTransferIncreasesBalance() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = LedgerViewModel(app)
        val initialSol = viewModel.uiState.value.assets.find { it.symbol == "SOL" }!!.balance

        viewModel.simulateIncomingTransfer("SOL", "Phantom", 6.53651734)

        val updatedSol = viewModel.uiState.value.assets.find { it.symbol == "SOL" }!!.balance
        assertEquals(initialSol + 6.53651734, updatedSol, 0.0001)

        val banner = viewModel.uiState.value.notificationBanner
        assertNotNull(banner)
        assertEquals("Received SOL", banner!!.title)
    }
}
