package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CryptoAsset
import com.example.ui.components.CryptoIcon
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LedgerCard
import com.example.ui.theme.LedgerPillDark
import com.example.ui.theme.LedgerPurpleAccent
import com.example.ui.theme.LedgerSurface
import com.example.ui.theme.LedgerTextMuted
import com.example.ui.theme.LedgerTextPrimary
import com.example.ui.theme.LedgerTextSecondary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectAssetBottomSheet(
    assets: List<CryptoAsset>,
    currencySymbol: String,
    currencyRate: Double,
    title: String = "Select asset",
    onDismiss: () -> Unit,
    onSelectAsset: (CryptoAsset) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }

    val filteredAssets = assets.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.symbol.contains(searchQuery, ignoreCase = true)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = LedgerSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 14.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF38354A))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            // Header: Title + Close (X)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = LedgerTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = LedgerTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar: Q Search (Matches Screenshot 8)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LedgerPillDark)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = LedgerTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = TextStyle(
                            color = LedgerTextPrimary,
                            fontSize = 15.sp,
                            fontFamily = InterFontFamily
                        ),
                        cursorBrush = SolidColor(LedgerPurpleAccent),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("select_asset_search_input"),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search",
                                    color = LedgerTextMuted,
                                    fontSize = 15.sp,
                                    fontFamily = InterFontFamily
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Assets List (Matches Screenshot 8)
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 30.dp)
            ) {
                items(filteredAssets) { coin ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectAsset(coin) }
                            .padding(horizontal = 20.dp, vertical = 13.dp)
                            .testTag("select_coin_${coin.symbol.lowercase()}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            CryptoIcon(symbol = coin.symbol, size = 40.dp)
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
            }
        }
    }
}
