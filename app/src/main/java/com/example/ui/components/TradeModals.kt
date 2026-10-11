package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CryptoAsset
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LedgerCard
import com.example.ui.theme.LedgerCardBorder
import com.example.ui.theme.LedgerPillDark
import com.example.ui.theme.LedgerPurpleAccent
import com.example.ui.theme.LedgerSurface
import com.example.ui.theme.LedgerTextPrimary
import com.example.ui.theme.LedgerTextSecondary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwapBottomSheet(
    assets: List<CryptoAsset>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onExecuteSwap: (fromSymbol: String, toSymbol: String, amount: Double) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var fromAsset by remember { mutableStateOf(assets.firstOrNull { it.symbol == "SOL" } ?: assets.first()) }
    var toAsset by remember { mutableStateOf(assets.firstOrNull { it.symbol == "USDT" } ?: assets.last()) }
    var swapAmountText by remember { mutableStateOf("1.0") }

    val fromAmount = swapAmountText.toDoubleOrNull() ?: 0.0
    val toAmount = (fromAmount * fromAsset.livePriceUsd) / toAsset.livePriceUsd

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = LedgerSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .padding(bottom = 30.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Swap",
                    color = LedgerTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = LedgerTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // You Pay Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(LedgerCard)
                    .border(1.dp, LedgerCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(text = "You pay", color = LedgerTextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = swapAmountText,
                            onValueChange = { swapAmountText = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = LedgerTextPrimary,
                                unfocusedTextColor = LedgerTextPrimary,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(LedgerPillDark)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            CryptoIcon(symbol = fromAsset.symbol, size = 22.dp)
                            Text(text = fromAsset.symbol, color = LedgerTextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        text = String.format(Locale.US, "Balance: %.4f %s", fromAsset.balance, fromAsset.symbol),
                        color = LedgerTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            // Swap Switch Icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(LedgerPillDark)
                        .clickable {
                            val temp = fromAsset
                            fromAsset = toAsset
                            toAsset = temp
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.SwapVert, contentDescription = "Switch", tint = LedgerPurpleAccent)
                }
            }

            // You Receive Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(LedgerCard)
                    .border(1.dp, LedgerCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(text = "You receive (estimated)", color = LedgerTextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = String.format(Locale.US, "%.6f", toAmount),
                            color = LedgerTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(LedgerPillDark)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            CryptoIcon(symbol = toAsset.symbol, size = 22.dp)
                            Text(text = toAsset.symbol, color = LedgerTextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Confirm Swap CTA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color.White)
                    .clickable {
                        onExecuteSwap(fromAsset.symbol, toAsset.symbol, fromAmount)
                        onDismiss()
                    }
                    .testTag("confirm_swap_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Swap ${fromAsset.symbol} to ${toAsset.symbol}", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyBottomSheet(
    assets: List<CryptoAsset>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onExecuteBuy: (symbol: String, amountUsd: Double) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var selectedAsset by remember { mutableStateOf(assets.firstOrNull { it.symbol == "BTC" } ?: assets.first()) }
    var buyAmountUsdText by remember { mutableStateOf("100") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = LedgerSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .padding(bottom = 30.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Buy Crypto",
                    color = LedgerTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = LedgerTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Asset selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                assets.take(4).forEach { coin ->
                    val isSel = coin.symbol == selectedAsset.symbol
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) LedgerPurpleAccent else LedgerCard)
                            .clickable { selectedAsset = coin }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = coin.symbol,
                            color = if (isSel) Color.Black else LedgerTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Amount input
            Text(text = "Amount ($currencySymbol)", color = LedgerTextSecondary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = buyAmountUsdText,
                onValueChange = { buyAmountUsdText = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color.White)
                    .clickable {
                        val amt = buyAmountUsdText.toDoubleOrNull() ?: 100.0
                        onExecuteBuy(selectedAsset.symbol, amt)
                        onDismiss()
                    }
                    .testTag("confirm_buy_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Buy $currencySymbol$buyAmountUsdText of ${selectedAsset.symbol}", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}
