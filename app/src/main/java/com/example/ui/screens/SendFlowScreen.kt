package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CryptoAsset
import com.example.ui.components.CryptoIcon
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LedgerBlack
import com.example.ui.theme.LedgerCard
import com.example.ui.theme.LedgerCardBorder
import com.example.ui.theme.LedgerPillDark
import com.example.ui.theme.LedgerPurpleAccent
import com.example.ui.theme.LedgerStepIndicatorActive
import com.example.ui.theme.LedgerTextMuted
import com.example.ui.theme.LedgerTextPrimary
import com.example.ui.theme.LedgerTextSecondary
import com.example.ui.theme.MonoFontFamily
import java.util.Locale

@Composable
fun SendFlowScreen(
    asset: CryptoAsset,
    currencySymbol: String,
    currencyRate: Double,
    onClose: () -> Unit,
    onConfirmSend: (symbol: String, amount: Double, recipient: String, feeAmount: Double, feeUsd: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(1) } // 1: Recipient, 2: Amount, 3: Summary
    val context = LocalContext.current

    var recipientAddress by remember {
        mutableStateOf(
            if (asset.symbol == "SOL") "7wgjyNX6EWaL4HoaaD6ngE36koPJCU16hadewGU2uAbe"
            else "Phantom Simulator (8Y5S...paRR)"
        )
    }

    var sendAmountText by remember {
        mutableStateOf(
            when (asset.symbol) {
                "SOL" -> "5"
                "BTC" -> "0.05"
                "ETH" -> "0.5"
                "USDT" -> "100"
                else -> "10"
            }
        )
    }

    val feeAmount = asset.defaultNetworkFee
    val feeUsd = feeAmount * asset.livePriceUsd * currencyRate

    BackHandler {
        if (step > 1) step -= 1 else onClose()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LedgerBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header: Title & Close / Back
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (step > 1) {
                    IconButton(
                        onClick = { step -= 1 },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = LedgerTextPrimary
                        )
                    }
                }

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Send ${asset.symbol}",
                        color = LedgerTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFontFamily
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    val stepTitle = when (step) {
                        1 -> "Recipient"
                        2 -> "Amount"
                        else -> "Summary"
                    }
                    Text(
                        text = stepTitle,
                        color = LedgerTextSecondary,
                        fontSize = 13.sp,
                        fontFamily = InterFontFamily
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = LedgerTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 3-Segment Step Progress Indicator (Matches Screenshots 7 & 9)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (i in 1..3) {
                    val isActive = i <= step
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .clip(RoundedCornerShape(1.5.dp))
                            .background(if (isActive) LedgerStepIndicatorActive else Color(0xFF262433))
                    )
                }
            }

            AnimatedContent(targetState = step, label = "send_step") { targetStep ->
                when (targetStep) {
                    1 -> SendStepRecipient(
                        asset = asset,
                        recipient = recipientAddress,
                        onRecipientChange = { recipientAddress = it },
                        onContinue = {
                            if (recipientAddress.isBlank()) {
                                Toast.makeText(context, "Please enter recipient address", Toast.LENGTH_SHORT).show()
                            } else {
                                step = 2
                            }
                        }
                    )

                    2 -> SendStepAmount(
                        asset = asset,
                        amountText = sendAmountText,
                        onAmountChange = { sendAmountText = it },
                        feeAmount = feeAmount,
                        feeUsd = feeUsd,
                        currencySymbol = currencySymbol,
                        currencyRate = currencyRate,
                        onContinue = {
                            val parsed = sendAmountText.toDoubleOrNull() ?: 0.0
                            if (parsed <= 0) {
                                Toast.makeText(context, "Please enter valid amount", Toast.LENGTH_SHORT).show()
                            } else if (parsed > asset.balance) {
                                Toast.makeText(context, "Insufficient balance", Toast.LENGTH_SHORT).show()
                            } else {
                                step = 3
                            }
                        }
                    )

                    else -> SendStepSummary(
                        asset = asset,
                        amount = sendAmountText.toDoubleOrNull() ?: 0.0,
                        recipient = recipientAddress,
                        feeAmount = feeAmount,
                        feeUsd = feeUsd,
                        currencySymbol = currencySymbol,
                        currencyRate = currencyRate,
                        onConfirm = {
                            val parsed = sendAmountText.toDoubleOrNull() ?: 0.0
                            onConfirmSend(asset.symbol, parsed, recipientAddress, feeAmount, feeUsd)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SendStepRecipient(
    asset: CryptoAsset,
    recipient: String,
    onRecipientChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Selected Asset Card (Matches Screenshot 9)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(LedgerCard)
                .border(1.dp, LedgerCardBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CryptoIcon(symbol = asset.symbol, size = 38.dp)
                    Column {
                        Text(
                            text = asset.accountName,
                            color = LedgerTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = InterFontFamily
                        )
                        Text(
                            text = asset.networkName,
                            color = LedgerTextSecondary,
                            fontSize = 13.sp,
                            fontFamily = InterFontFamily
                        )
                    }
                }

                Text(
                    text = String.format(Locale.US, "%.6f %s", asset.balance, asset.symbol),
                    color = LedgerTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = InterFontFamily
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recipient address",
                color = LedgerTextSecondary,
                fontSize = 13.sp,
                fontFamily = InterFontFamily
            )

            // Direct Paste button from Clipboard
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(LedgerPillDark)
                    .clickable {
                        val clipText = clipboardManager.getText()?.text
                        if (!clipText.isNullOrBlank()) {
                            onRecipientChange(clipText.trim())
                            Toast.makeText(context, "Address pasted from clipboard", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentPaste,
                    contentDescription = "Paste",
                    tint = LedgerPurpleAccent,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Paste",
                    color = LedgerPurpleAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Recipient Address Input Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(LedgerCard)
                .border(1.dp, LedgerCardBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = recipient,
                    onValueChange = onRecipientChange,
                    textStyle = TextStyle(
                        color = LedgerTextPrimary,
                        fontSize = 14.sp,
                        fontFamily = MonoFontFamily,
                        lineHeight = 20.sp
                    ),
                    cursorBrush = SolidColor(LedgerPurpleAccent),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("send_recipient_input"),
                    decorationBox = { innerTextField ->
                        if (recipient.isEmpty()) {
                            Text(
                                text = "Paste or type any ${asset.symbol} address",
                                color = LedgerTextMuted,
                                fontSize = 14.sp,
                                fontFamily = MonoFontFamily
                            )
                        }
                        innerTextField()
                    }
                )

                if (recipient.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(LedgerPillDark)
                            .clickable { onRecipientChange("") }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("send_clear_recipient_button")
                    ) {
                        Text(
                            text = "Clear",
                            color = LedgerTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = InterFontFamily
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Simulator Presets
        Text(
            text = "Simulated Targets (Real arrival in other simulators):",
            color = LedgerTextMuted,
            fontSize = 12.sp,
            fontFamily = InterFontFamily
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Phantom", "Trust", "Exodus", "MetaMask").forEach { walletName ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(LedgerPillDark)
                        .clickable {
                            onRecipientChange("$walletName Simulator (${asset.symbol} Main)")
                        }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = walletName,
                        color = LedgerTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = InterFontFamily
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))
        Spacer(modifier = Modifier.height(40.dp))

        // Continue Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(27.dp))
                .background(Color.White)
                .clickable(onClick = onContinue)
                .testTag("send_step1_continue_button"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Continue",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = InterFontFamily
            )
        }
    }
}

@Composable
fun SendStepAmount(
    asset: CryptoAsset,
    amountText: String,
    onAmountChange: (String) -> Unit,
    feeAmount: Double,
    feeUsd: Double,
    currencySymbol: String,
    currencyRate: Double,
    onContinue: () -> Unit
) {
    val amountDouble = amountText.toDoubleOrNull() ?: 0.0
    val fiatAmount = amountDouble * asset.livePriceUsd * currencyRate

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Large Centered Amount Display (Matches Screenshot 7)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            BasicTextField(
                value = amountText,
                onValueChange = onAmountChange,
                textStyle = TextStyle(
                    color = LedgerTextPrimary,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                cursorBrush = SolidColor(LedgerPurpleAccent),
                modifier = Modifier.testTag("send_amount_input")
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = asset.symbol,
                color = LedgerTextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = InterFontFamily
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Fiat Conversion: ⇅ $573.15
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(LedgerPillDark)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SwapVert,
                contentDescription = "Convert",
                tint = LedgerTextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = String.format(Locale.US, "%s%,.2f", currencySymbol, fiatAmount),
                color = LedgerTextSecondary,
                fontSize = 14.sp,
                fontFamily = InterFontFamily
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Available balance card + Max button (Matches Screenshot 7)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(LedgerCard)
                .border(1.dp, LedgerCardBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format(Locale.US, "Available %.6f %s", asset.balance, asset.symbol),
                    color = LedgerTextSecondary,
                    fontSize = 14.sp,
                    fontFamily = InterFontFamily
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(LedgerPillDark)
                        .clickable {
                            val maxSend = (asset.balance - feeAmount).coerceAtLeast(0.0)
                            onAmountChange(String.format(Locale.US, "%.6f", maxSend))
                        }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("send_max_amount_button")
                ) {
                    Text(
                        text = "Max",
                        color = LedgerTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFontFamily
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Real Network fees
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Network fees",
                color = LedgerTextSecondary,
                fontSize = 13.sp,
                fontFamily = InterFontFamily
            )
            Text(
                text = String.format(Locale.US, "%.8f %s ≈ %s%.2f", feeAmount, asset.symbol, currencySymbol, feeUsd),
                color = LedgerTextSecondary,
                fontSize = 13.sp,
                fontFamily = InterFontFamily
            )
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))
        Spacer(modifier = Modifier.height(40.dp))

        // Continue Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(27.dp))
                .background(Color.White)
                .clickable(onClick = onContinue)
                .testTag("send_step2_continue_button"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Continue",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = InterFontFamily
            )
        }
    }
}

@Composable
fun SendStepSummary(
    asset: CryptoAsset,
    amount: Double,
    recipient: String,
    feeAmount: Double,
    feeUsd: Double,
    currencySymbol: String,
    currencyRate: Double,
    onConfirm: () -> Unit
) {
    val fiatAmount = amount * asset.livePriceUsd * currencyRate

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Up arrow circle icon (Matches Screenshot 7)
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(LedgerPillDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = null,
                tint = LedgerTextPrimary,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large Amount: 5 SOL
        Text(
            text = "$amount ${asset.symbol}",
            color = LedgerTextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = InterFontFamily
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = String.format(Locale.US, "%s%,.2f", currencySymbol, fiatAmount),
            color = LedgerTextSecondary,
            fontSize = 15.sp,
            fontFamily = InterFontFamily
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Details List (Matches Screenshot 7)
        Column(modifier = Modifier.fillMaxWidth()) {
            TxSummaryRow(label = "Account", value = asset.accountName)
            HorizontalDivider(color = LedgerCardBorder, thickness = 0.8.dp)

            TxSummaryRow(
                label = "To",
                value = if (recipient.length > 20) "${recipient.take(8)}...${recipient.takeLast(6)}" else recipient
            )
            HorizontalDivider(color = LedgerCardBorder, thickness = 0.8.dp)

            TxSummaryRow(label = "Network", value = asset.networkName)
            HorizontalDivider(color = LedgerCardBorder, thickness = 0.8.dp)

            TxSummaryRow(
                label = "Network fees",
                value = String.format(Locale.US, "%.8f %s ≈ %s%.2f", feeAmount, asset.symbol, currencySymbol, feeUsd)
            )
            HorizontalDivider(color = LedgerCardBorder, thickness = 0.8.dp)

            val totalDebit = amount + feeAmount
            TxSummaryRow(label = "Total debit", value = String.format(Locale.US, "%.8f %s", totalDebit, asset.symbol))
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))
        Spacer(modifier = Modifier.height(40.dp))

        // Confirm Button (White pill button)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(27.dp))
                .background(Color.White)
                .clickable(onClick = onConfirm)
                .testTag("send_confirm_button"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Confirm",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = InterFontFamily
            )
        }
    }
}

@Composable
fun TxSummaryRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = label,
            color = LedgerTextSecondary,
            fontSize = 13.sp,
            fontFamily = InterFontFamily
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = LedgerTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = InterFontFamily
        )
    }
}

