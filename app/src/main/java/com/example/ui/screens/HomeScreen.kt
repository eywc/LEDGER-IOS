package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CryptoAsset
import com.example.ui.components.CryptoIcon
import com.example.ui.components.HalftoneDotMatrixBackground
import com.example.ui.components.IosPullToRefreshLayout
import com.example.ui.components.LedgerTopBar
import com.example.ui.components.MarketMoodCard
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LedgerCard
import com.example.ui.theme.LedgerCardBorder
import com.example.ui.theme.LedgerCardHover
import com.example.ui.theme.LedgerGreen
import com.example.ui.theme.LedgerPillDark
import com.example.ui.theme.LedgerRed
import com.example.ui.theme.LedgerTextMuted
import com.example.ui.theme.LedgerTextPrimary
import com.example.ui.theme.LedgerTextSecondary
import com.example.viewmodel.LedgerUiState
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    uiState: LedgerUiState,
    onRefresh: () -> Unit,
    onOpenAssetDetail: (String) -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenTransferSheet: () -> Unit,
    onOpenSwap: () -> Unit,
    onOpenBuy: () -> Unit,
    onOpenEditSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Halftone dot matrix texture & deep violet glow
        HalftoneDotMatrixBackground(showDots = true)

        IosPullToRefreshLayout(
            isRefreshing = uiState.isLivePriceLoading,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                // Top Bar with Profile, Compass, Search, Clock
                item {
                    LedgerTopBar(
                        onOpenHistory = onOpenHistory,
                        onOpenSearch = onOpenSearch,
                        onOpenEditSheet = onOpenEditSheet
                    )
                }

            // Total Balance Section (Matches Screenshot 1)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {},
                            onLongClick = onOpenEditSheet
                        )
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                        .testTag("total_balance_card"),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val formattedTotal = String.format(Locale.US, "%,.2f", uiState.totalBalanceInActiveCurrency)
                    val parts = formattedTotal.split(".")
                    val mainAmount = parts.getOrNull(0) ?: "0"
                    val decimalAmount = parts.getOrNull(1) ?: "00"

                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${uiState.activeCurrency.symbol}$mainAmount",
                            color = LedgerTextPrimary,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = InterFontFamily,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = ".$decimalAmount",
                            color = LedgerTextSecondary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = InterFontFamily,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // -3.33% • Today > badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(LedgerPillDark)
                            .combinedClickable(
                                onClick = {},
                                onLongClick = onOpenEditSheet
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val changeText = String.format(Locale.US, "%+.2f%%", uiState.dayChangePct)
                            val changeColor = if (uiState.dayChangePct >= 0) LedgerGreen else LedgerRed
                            Text(
                                text = changeText,
                                color = changeColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = InterFontFamily
                            )
                            Text(
                                text = "•",
                                color = LedgerTextMuted,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Today",
                                color = LedgerTextSecondary,
                                fontSize = 13.sp,
                                fontFamily = InterFontFamily
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "View Day Change",
                                tint = LedgerTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Quick Actions: Transfer, Swap, Buy (Matches Screenshot 1)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionButton(
                        icon = Icons.Default.SwapVert,
                        label = "Transfer",
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_action_transfer"),
                        onClick = onOpenTransferSheet
                    )
                    QuickActionButton(
                        icon = Icons.Default.SwapHoriz,
                        label = "Swap",
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_action_swap"),
                        onClick = onOpenSwap
                    )
                    QuickActionButton(
                        icon = Icons.Default.Add,
                        label = "Buy",
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_action_buy"),
                        onClick = onOpenBuy
                    )
                }
            }

            // Market Section Header (Matches Screenshot 1)
            item {
                Spacer(modifier = Modifier.height(28.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onOpenEditSheet() }
                    ) {
                        Text(
                            text = "Market",
                            color = LedgerTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = InterFontFamily
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Market",
                            tint = LedgerTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onOpenEditSheet() }
                    ) {
                        Text(
                            text = "Trending",
                            color = LedgerTextSecondary,
                            fontSize = 13.sp,
                            fontFamily = InterFontFamily
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Trending options",
                            tint = LedgerTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Market Cards Carousel (Mood gauge + Trending coins)
            item {
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Mood gauge card
                    item {
                        MarketMoodCard(
                            mood = uiState.marketMood,
                            onClick = onOpenEditSheet
                        )
                    }

                    // Trending coin cards
                    val trendingCoins = uiState.assets.filter { it.isTrending }
                    items(trendingCoins) { coin ->
                        TrendingCoinCard(
                            coin = coin,
                            onClick = { onOpenAssetDetail(coin.symbol) }
                        )
                    }
                }
            }

            // Crypto Section Header (Matches Screenshot 1)
            item {
                Spacer(modifier = Modifier.height(28.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Crypto",
                        color = LedgerTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFontFamily
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Crypto list",
                        tint = LedgerTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // User Crypto Holdings List
            items(uiState.assets) { coin ->
                CryptoAssetRow(
                    coin = coin,
                    currencySymbol = uiState.activeCurrency.symbol,
                    currencyRate = uiState.activeCurrency.rateToUsd,
                    onClick = { onOpenAssetDetail(coin.symbol) }
                )
            }
        }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(76.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(LedgerCard)
            .border(1.dp, LedgerCardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = LedgerTextPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                color = LedgerTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = InterFontFamily
            )
        }
    }
}

@Composable
fun TrendingCoinCard(
    coin: CryptoAsset,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(108.dp)
            .height(118.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(LedgerCard)
            .border(1.dp, LedgerCardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CryptoIcon(
                symbol = coin.symbol,
                size = 38.dp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = coin.symbol,
                color = LedgerTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = InterFontFamily
            )
            Spacer(modifier = Modifier.height(2.dp))
            val changeColor = if (coin.change24hPct >= 0) LedgerGreen else LedgerRed
            Text(
                text = String.format(Locale.US, "%+.2f%%", coin.change24hPct),
                color = changeColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = InterFontFamily
            )
        }
    }
}

@Composable
fun CryptoAssetRow(
    coin: CryptoAsset,
    currencySymbol: String,
    currencyRate: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 13.dp)
            .testTag("crypto_asset_row_${coin.symbol.lowercase()}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CryptoIcon(symbol = coin.symbol, size = 42.dp)
            Column {
                Text(
                    text = coin.name,
                    color = LedgerTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = InterFontFamily
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = coin.symbol,
                    color = LedgerTextSecondary,
                    fontSize = 13.sp,
                    fontFamily = InterFontFamily
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            val fiatVal = coin.totalFiatValue * currencyRate
            Text(
                text = String.format(Locale.US, "%s%,.2f", currencySymbol, fiatVal),
                color = LedgerTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = InterFontFamily
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = String.format(Locale.US, "%.4f %s", coin.balance, coin.symbol),
                color = LedgerTextSecondary,
                fontSize = 13.sp,
                fontFamily = InterFontFamily
            )
        }
    }
}
