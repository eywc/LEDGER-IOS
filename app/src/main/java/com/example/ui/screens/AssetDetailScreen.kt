package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CryptoAsset
import com.example.ui.components.CryptoIcon
import com.example.ui.components.InteractivePriceChart
import com.example.ui.components.IosPullToRefreshLayout
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LedgerBlack
import com.example.ui.theme.LedgerCard
import com.example.ui.theme.LedgerCardBorder
import com.example.ui.theme.LedgerGreen
import com.example.ui.theme.LedgerPillDark
import com.example.ui.theme.LedgerRed
import com.example.ui.theme.LedgerTextMuted
import com.example.ui.theme.LedgerTextPrimary
import com.example.ui.theme.LedgerTextSecondary
import com.example.ui.theme.MonoFontFamily
import java.util.Locale

@Composable
fun AssetDetailScreen(
    asset: CryptoAsset,
    currencySymbol: String,
    currencyRate: Double,
    onBack: () -> Unit,
    onSend: (String) -> Unit,
    onReceive: (String) -> Unit,
    onBuy: () -> Unit,
    onSwap: () -> Unit,
    onRefresh: () -> Unit = {},
    isRefreshing: Boolean = false,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedTimeframe by remember { mutableStateOf("1D") }
    val timeframes = listOf("1D", "1W", "1M", "1Y", "ALL")

    var scrubbedPrice by remember { mutableStateOf<Double?>(null) }
    val currentPrice = scrubbedPrice ?: asset.livePriceUsd

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LedgerBlack)
    ) {
        IosPullToRefreshLayout(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
            // Top Bar: Back, Coin Pill, 3 dots
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
                        modifier = Modifier.testTag("asset_detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = LedgerTextPrimary
                        )
                    }

                    // Centered (Icon) Symbol Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(LedgerPillDark)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CryptoIcon(symbol = asset.symbol, size = 20.dp)
                            Text(
                                text = asset.symbol,
                                color = LedgerTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = InterFontFamily
                            )
                        }
                    }

                    IconButton(
                        onClick = { /* Menu */ },
                        modifier = Modifier.testTag("asset_detail_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = LedgerTextPrimary
                        )
                    }
                }
            }

            // Market Price Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Market price",
                        color = LedgerTextSecondary,
                        fontSize = 13.sp,
                        fontFamily = InterFontFamily
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format(Locale.US, "%s%,.2f", currencySymbol, currentPrice * currencyRate),
                        color = LedgerTextPrimary,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFontFamily,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Change badge e.g. ▼ 3.32% -$2,876.55 • 24 hours
                    val isNegative = asset.change24hPct < 0
                    val changeColor = if (isNegative) LedgerRed else LedgerGreen
                    val arrowSymbol = if (isNegative) "▼" else "▲"
                    val diffUsd = (currentPrice * (asset.change24hPct / 100.0)) * currencyRate

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = String.format(Locale.US, "%s %.2f%%", arrowSymbol, kotlin.math.abs(asset.change24hPct)),
                            color = changeColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = InterFontFamily
                        )
                        Text(
                            text = String.format(Locale.US, "%s%,.2f", if (diffUsd < 0) "-$currencySymbol" else "+$currencySymbol", kotlin.math.abs(diffUsd)),
                            color = changeColor,
                            fontSize = 13.sp,
                            fontFamily = InterFontFamily
                        )
                        Text(
                            text = "• 24 hours",
                            color = LedgerTextSecondary,
                            fontSize = 13.sp,
                            fontFamily = InterFontFamily
                        )
                    }
                }
            }

            // Interactive Price Chart
            item {
                Spacer(modifier = Modifier.height(8.dp))
                InteractivePriceChart(
                    prices = if (asset.historicalPrices.isNotEmpty()) asset.historicalPrices else listOf(asset.livePriceUsd * 0.95, asset.livePriceUsd),
                    isPositive = asset.change24hPct >= 0,
                    currencySymbol = currencySymbol,
                    onScrub = { scrubbedPrice = it }
                )
            }

            // Timeframe Selector Tabs
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        timeframes.forEach { tf ->
                            val isSelected = selectedTimeframe == tf
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) LedgerCard else Color.Transparent)
                                    .border(
                                        width = if (isSelected) 1.dp else 0.dp,
                                        color = if (isSelected) LedgerCardBorder else Color.Transparent,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedTimeframe = tf }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tf,
                                    color = if (isSelected) LedgerTextPrimary else LedgerTextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontFamily = InterFontFamily
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(LedgerCard)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Chart Settings",
                            tint = LedgerTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Send / Receive Action Buttons Row
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(26.dp))
                            .background(LedgerCard)
                            .border(1.dp, LedgerCardBorder, RoundedCornerShape(26.dp))
                            .clickable { onSend(asset.symbol) }
                            .testTag("asset_detail_send_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Send",
                                tint = LedgerTextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Send",
                                color = LedgerTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = InterFontFamily
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(26.dp))
                            .background(LedgerCard)
                            .border(1.dp, LedgerCardBorder, RoundedCornerShape(26.dp))
                            .clickable { onReceive(asset.symbol) }
                            .testTag("asset_detail_receive_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Receive",
                                tint = LedgerTextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Receive",
                                color = LedgerTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = InterFontFamily
                            )
                        }
                    }
                }
            }

            // "Your balance" Card (Matches Screenshot 5)
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(LedgerCard)
                        .border(1.dp, LedgerCardBorder, RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "Your balance",
                                    color = LedgerTextSecondary,
                                    fontSize = 13.sp,
                                    fontFamily = InterFontFamily
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                val fiatBalance = asset.totalFiatValue * currencyRate
                                Text(
                                    text = String.format(Locale.US, "%s%,.2f", currencySymbol, fiatBalance),
                                    color = LedgerTextPrimary,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = InterFontFamily
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = String.format(Locale.US, "%.4f %s", asset.balance, asset.symbol),
                                    color = LedgerTextSecondary,
                                    fontSize = 14.sp,
                                    fontFamily = InterFontFamily
                                )
                            }

                            CryptoIcon(symbol = asset.symbol, size = 44.dp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Account Pill: "BTC Main  bc1qld...3xez"
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F0E16))
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = asset.accountName,
                                    color = LedgerTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = InterFontFamily
                                )

                                val shortAddr = if (asset.address.length > 14) {
                                    "${asset.address.take(6)}...${asset.address.takeLast(4)}"
                                } else asset.address

                                Text(
                                    text = shortAddr,
                                    color = LedgerTextMuted,
                                    fontSize = 12.sp,
                                    fontFamily = MonoFontFamily
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Actions: Buy, Swap, ...
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Buy (Dark pill)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .background(LedgerCard)
                            .border(1.dp, LedgerCardBorder, RoundedCornerShape(25.dp))
                            .clickable { onBuy() }
                            .testTag("asset_detail_buy_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Buy",
                            color = LedgerTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = InterFontFamily
                        )
                    }

                    // Swap (White pill with black text!)
                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .height(50.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .background(Color.White)
                            .clickable { onSwap() }
                            .testTag("asset_detail_swap_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Swap",
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = InterFontFamily
                        )
                    }

                    // More (...)
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(LedgerCard)
                            .border(1.dp, LedgerCardBorder, CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "More",
                            tint = LedgerTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        }
    }
}
