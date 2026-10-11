package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.CryptoAsset
import com.example.model.CurrencyOption
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LedgerBlack
import com.example.ui.theme.LedgerCard
import com.example.ui.theme.LedgerCardBorder
import com.example.ui.theme.LedgerGreen
import com.example.ui.theme.LedgerPillDark
import com.example.ui.theme.LedgerPurpleAccent
import com.example.ui.theme.LedgerPurpleSlider
import com.example.ui.theme.LedgerTextMuted
import com.example.ui.theme.LedgerTextPrimary
import com.example.ui.theme.LedgerTextSecondary
import com.example.viewmodel.LedgerUiState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSimulatorSheet(
    uiState: LedgerUiState,
    onClose: () -> Unit,
    onUpdateBalance: (symbol: String, newBalance: Double) -> Unit,
    onUpdateAccountName: (symbol: String, newName: String) -> Unit,
    onUpdateMood: (score: Int) -> Unit,
    onSelectCurrency: (CurrencyOption) -> Unit,
    onResetDefaults: () -> Unit,
    onToggleLivePrices: (Boolean) -> Unit,
    onRefreshLivePrices: () -> Unit,
    onSimulateIncoming: (symbol: String, walletName: String, amount: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onClose() }

    var editingAsset by remember { mutableStateOf<CryptoAsset?>(null) }
    var editDialogText by remember { mutableStateOf("") }
    var editAccountNameText by remember { mutableStateOf("") }

    val currencies = listOf(
        CurrencyOption("USD", "$", 1.0),
        CurrencyOption("EUR", "€", 0.92),
        CurrencyOption("GBP", "£", 0.77),
        CurrencyOption("JPY", "¥", 152.0),
        CurrencyOption("CAD", "C$", 1.38),
        CurrencyOption("AUD", "A$", 1.50)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LedgerBlack)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 70.dp)
        ) {
            // Header Bar: LEDGER | Done
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Official LEDGER Branding
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_ledger_brackets_logo),
                            contentDescription = "Ledger Logo",
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )

                        Text(
                            text = "LEDGER",
                            color = LedgerTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = InterFontFamily,
                            letterSpacing = 1.2.sp
                        )
                    }

                    // Done button
                    Text(
                        text = "Done",
                        color = LedgerPurpleAccent,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFontFamily,
                        modifier = Modifier
                            .clickable(onClick = onClose)
                            .testTag("edit_done_button")
                    )
                }
            }

            // LIVE MARKET PRICES SECTION
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(LedgerCard)
                        .border(1.dp, LedgerCardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Live Prices on Real Balances",
                                    color = LedgerTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = InterFontFamily
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = uiState.livePriceStatus,
                                    color = if (uiState.isLivePricesEnabled) LedgerGreen else LedgerTextMuted,
                                    fontSize = 12.sp,
                                    fontFamily = InterFontFamily
                                )
                            }

                            Switch(
                                checked = uiState.isLivePricesEnabled,
                                onCheckedChange = onToggleLivePrices,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = LedgerPurpleAccent
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Refresh Button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(LedgerPillDark)
                                .clickable { onRefreshLivePrices() }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (uiState.isLivePriceLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = LedgerPurpleAccent
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh",
                                    tint = LedgerPurpleAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                            }
                            Text(
                                text = "Fetch Real-Time Coin Prices",
                                color = LedgerPurpleAccent,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = InterFontFamily
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // BALANCE Header + Reset Button (Matches Screenshot 3)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BALANCE",
                        color = LedgerTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFontFamily,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "Reset",
                        color = LedgerPurpleAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = InterFontFamily,
                        modifier = Modifier
                            .clickable(onClick = onResetDefaults)
                            .testTag("edit_reset_button")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Explanatory Paragraph Verbatim from Screenshot 3
                Text(
                    text = "The quick way. Drag a coin to the size of wallet you want, or tap a figure to type it: the amount of the coin, or what it is worth. This is the wallet's real balance, so a send or a receipt moves the thumb, and what you set here can be sent to any other wallet.",
                    color = LedgerTextMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontFamily = InterFontFamily,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Coin Sliders List (Matches Screenshot 3)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(LedgerCard)
                        .border(1.dp, LedgerCardBorder, RoundedCornerShape(18.dp))
                        .padding(vertical = 12.dp)
                ) {
                    Column {
                        uiState.assets.forEachIndexed { index, coin ->
                            CoinSliderItem(
                                coin = coin,
                                currencySymbol = uiState.activeCurrency.symbol,
                                currencyRate = uiState.activeCurrency.rateToUsd,
                                onBalanceChange = { newBal -> onUpdateBalance(coin.symbol, newBal) },
                                onManualEdit = {
                                    editingAsset = coin
                                    editDialogText = String.format(Locale.US, "%.6f", coin.balance)
                                    editAccountNameText = coin.accountName
                                }
                            )

                            if (index < uiState.assets.size - 1) {
                                Spacer(modifier = Modifier.height(14.dp))
                            }
                        }
                    }
                }
            }

            // Currency Customization
            item {
                Spacer(modifier = Modifier.height(28.dp))
                Text(
                    text = "CURRENCY",
                    color = LedgerTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currencies.forEach { curr ->
                        val isSelected = uiState.activeCurrency.code == curr.code
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) LedgerPurpleAccent else LedgerCard)
                                .clickable { onSelectCurrency(curr) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${curr.symbol} ${curr.code}",
                                color = if (isSelected) Color.Black else LedgerTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = InterFontFamily
                            )
                        }
                    }
                }
            }

            // Market Mood Customization
            item {
                Spacer(modifier = Modifier.height(28.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MARKET MOOD",
                        color = LedgerTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFontFamily,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "${uiState.marketMood.score} • ${uiState.marketMood.label}",
                        color = LedgerPurpleAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = InterFontFamily
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(LedgerCard)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Slider(
                        value = uiState.marketMood.score.toFloat(),
                        onValueChange = { onUpdateMood(it.toInt()) },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = LedgerPurpleSlider,
                            activeTrackColor = LedgerPurpleAccent,
                            inactiveTrackColor = Color(0xFF2C2A3C)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Inter-Wallet Simulator: Test Transfer In
            item {
                Spacer(modifier = Modifier.height(28.dp))
                Text(
                    text = "RECEIVE SIMULATOR (SEND FROM OTHER WALLETS)",
                    color = LedgerTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Simulate receiving funds from Phantom, Trust, or Exodus. Sends land here in real time with push banner!",
                    color = LedgerTextMuted,
                    fontSize = 12.sp,
                    fontFamily = InterFontFamily,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SimulateReceiveButton(
                        title = "+6.53651734 SOL from Phantom",
                        subtitle = "Matches screenshot transfer record",
                        onClick = { onSimulateIncoming("SOL", "Phantom", 6.53651734) }
                    )

                    SimulateReceiveButton(
                        title = "+0.87275266 SOL from Trust",
                        subtitle = "Instant confirmation into SOL Main",
                        onClick = { onSimulateIncoming("SOL", "Trust", 0.87275266) }
                    )

                    SimulateReceiveButton(
                        title = "+0.5 BTC from Exodus",
                        subtitle = "Instant Bitcoin deposit",
                        onClick = { onSimulateIncoming("BTC", "Exodus", 0.5) }
                    )

                    SimulateReceiveButton(
                        title = "+500 USDT from MetaMask",
                        subtitle = "Instant Tether deposit",
                        onClick = { onSimulateIncoming("USDT", "MetaMask", 500.0) }
                    )
                }
            }
        }

        // Manual Balance & Account Name Edit Dialog
        if (editingAsset != null) {
            val asset = editingAsset!!
            AlertDialog(
                onDismissRequest = { editingAsset = null },
                title = {
                    Text(
                        text = "Edit ${asset.name} (${asset.symbol})",
                        color = LedgerTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFontFamily
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Account Name:",
                            color = LedgerTextSecondary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = editAccountNameText,
                            onValueChange = { editAccountNameText = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Coin Balance (${asset.symbol}):",
                            color = LedgerTextSecondary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = editDialogText,
                            onValueChange = { editDialogText = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val parsed = editDialogText.toDoubleOrNull()
                            if (parsed != null) {
                                onUpdateBalance(asset.symbol, parsed)
                            }
                            if (editAccountNameText.isNotBlank()) {
                                onUpdateAccountName(asset.symbol, editAccountNameText.trim())
                            }
                            editingAsset = null
                        }
                    ) {
                        Text("Save", color = LedgerPurpleAccent, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingAsset = null }) {
                        Text("Cancel", color = LedgerTextSecondary)
                    }
                },
                containerColor = LedgerCard
            )
        }
    }
}

@Composable
fun CoinSliderItem(
    coin: CryptoAsset,
    currencySymbol: String,
    currencyRate: Double,
    onBalanceChange: (Double) -> Unit,
    onManualEdit: () -> Unit
) {
    val fiatVal = coin.totalFiatValue * currencyRate
    var sliderVal by remember(coin.balance) {
        mutableFloatStateOf(coin.balance.toFloat())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
    ) {
        // Top line: Symbol left, Balance right (Clickable to manual edit)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onManualEdit),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = coin.symbol,
                color = LedgerTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = InterFontFamily
            )

            Text(
                text = String.format(Locale.US, "%.4f %s", coin.balance, coin.symbol),
                color = LedgerTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = InterFontFamily
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Second line: Name left, Fiat equivalent right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = coin.name,
                color = LedgerTextMuted,
                fontSize = 13.sp,
                fontFamily = InterFontFamily
            )

            Text(
                text = String.format(Locale.US, "≈ %s%,.2f", currencySymbol, fiatVal),
                color = LedgerTextMuted,
                fontSize = 13.sp,
                fontFamily = InterFontFamily
            )
        }

        // Slider with purple thumb (Matches Screenshot 3)
        Slider(
            value = sliderVal,
            onValueChange = { newVal ->
                sliderVal = newVal
                onBalanceChange(newVal.toDouble())
            },
            valueRange = 0f..coin.maxSliderBalance.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = LedgerPurpleSlider,
                activeTrackColor = Color(0xFF38354A),
                inactiveTrackColor = Color(0xFF232130)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
        )
    }
}

@Composable
fun SimulateReceiveButton(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(LedgerCard)
            .border(1.dp, LedgerCardBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    color = LedgerTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = InterFontFamily
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = LedgerTextSecondary,
                    fontSize = 12.sp,
                    fontFamily = InterFontFamily
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(LedgerPillDark)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Simulate",
                    color = LedgerPurpleAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

