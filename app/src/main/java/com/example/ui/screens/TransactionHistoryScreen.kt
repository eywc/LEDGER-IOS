package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TransactionRecord
import com.example.model.TxType
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LedgerBlack
import com.example.ui.theme.LedgerCard
import com.example.ui.theme.LedgerGreen
import com.example.ui.theme.LedgerPillDark
import com.example.ui.theme.LedgerTextPrimary
import com.example.ui.theme.LedgerTextSecondary
import java.util.Locale

@Composable
fun TransactionHistoryScreen(
    transactions: List<TransactionRecord>,
    currencySymbol: String,
    onBack: () -> Unit,
    onSelectTx: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    // Group transactions by date
    val groupedByDate = transactions.groupBy { it.date }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LedgerBlack)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Top Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("tx_history_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = LedgerTextPrimary
                        )
                    }

                    Text(
                        text = "Transaction history",
                        color = LedgerTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFontFamily
                    )

                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = LedgerTextPrimary
                        )
                    }
                }
            }

            // Grouped transaction lists
            groupedByDate.forEach { (date, txList) ->
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(LedgerCard)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = date,
                            color = LedgerTextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = InterFontFamily
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                items(txList) { tx ->
                    TransactionItemRow(
                        tx = tx,
                        currencySymbol = currencySymbol,
                        onClick = { onSelectTx(tx.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItemRow(
    tx: TransactionRecord,
    currencySymbol: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isReceived = tx.type == TxType.RECEIVED
    val sign = if (isReceived) "+" else "-"
    val amountColor = if (isReceived) LedgerGreen else LedgerTextPrimary
    val fiatColor = if (isReceived) LedgerGreen else LedgerTextSecondary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("tx_item_${tx.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Icon (circle with down/up arrow)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(LedgerPillDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isReceived) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                    contentDescription = if (isReceived) "Received" else "Sent",
                    tint = if (isReceived) LedgerGreen else LedgerTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = tx.accountName,
                    color = LedgerTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = InterFontFamily
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${if (isReceived) "Received" else "Sent"} ${tx.time}",
                    color = LedgerTextSecondary,
                    fontSize = 13.sp,
                    fontFamily = InterFontFamily
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$sign${tx.amount} ${tx.assetSymbol}",
                color = amountColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = InterFontFamily
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = String.format(Locale.US, "%s%,.2f", currencySymbol, tx.fiatValue),
                color = fiatColor,
                fontSize = 13.sp,
                fontFamily = InterFontFamily
            )
        }
    }
}
