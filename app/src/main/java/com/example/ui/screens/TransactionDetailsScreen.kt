package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TransactionRecord
import com.example.model.TxType
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LedgerBlack
import com.example.ui.theme.LedgerCardBorder
import com.example.ui.theme.LedgerGreen
import com.example.ui.theme.LedgerPillDark
import com.example.ui.theme.LedgerTextPrimary
import com.example.ui.theme.LedgerTextSecondary
import com.example.ui.theme.MonoFontFamily
import java.util.Locale

@Composable
fun TransactionDetailsScreen(
    tx: TransactionRecord,
    currencySymbol: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val isReceived = tx.type == TxType.RECEIVED
    val sign = if (isReceived) "+" else "-"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LedgerBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 40.dp)
        ) {
            // Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .testTag("tx_details_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = LedgerTextPrimary
                    )
                }

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Transaction details",
                        color = LedgerTextSecondary,
                        fontSize = 12.sp,
                        fontFamily = InterFontFamily
                    )
                    Text(
                        text = if (isReceived) "Received" else "Sent",
                        color = LedgerTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = InterFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Main Circle Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(LedgerPillDark)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isReceived) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = if (isReceived) LedgerGreen else LedgerTextPrimary,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Large Amount: +6.53651734 SOL
            val mainAmountColor = if (isReceived) LedgerGreen else LedgerTextPrimary
            Text(
                text = "$sign${tx.amount} ${tx.assetSymbol}",
                color = mainAmountColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = InterFontFamily,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Fiat Value + Info Icon
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = String.format(Locale.US, "%s%,.2f", currencySymbol, tx.fiatValue),
                    color = LedgerTextSecondary,
                    fontSize = 15.sp,
                    fontFamily = InterFontFamily
                )
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Details",
                    tint = LedgerTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // • Confirmed Badge
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(LedgerGreen)
                )
                Text(
                    text = "Confirmed",
                    color = LedgerGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = InterFontFamily
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Details List (Matches Screenshot 2)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                TxDetailRow(label = "Account", value = tx.accountName)
                TxDivider()

                TxDetailRow(label = "Network", value = tx.network)
                TxDivider()

                TxDetailRow(label = "Date", value = "${tx.date} - ${tx.time}")
                TxDivider()

                TxDetailRow(label = "Network fees", value = tx.networkFee)
                TxDivider()

                // Transaction ID with click to copy
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            clipboardManager.setText(AnnotatedString(tx.txId))
                            Toast.makeText(context, "Transaction ID copied", Toast.LENGTH_SHORT).show()
                        }
                        .padding(vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Transaction ID",
                            color = LedgerTextSecondary,
                            fontSize = 14.sp,
                            fontFamily = InterFontFamily
                        )
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Copy TX ID",
                            tint = LedgerTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tx.txId,
                        color = LedgerTextPrimary,
                        fontSize = 13.sp,
                        fontFamily = MonoFontFamily,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
fun TxDetailRow(label: String, value: String) {
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

@Composable
fun TxDivider() {
    HorizontalDivider(
        color = LedgerCardBorder.copy(alpha = 0.5f),
        thickness = 0.8.dp
    )
}
