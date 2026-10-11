package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.InterFontFamily

@Composable
fun CryptoIcon(
    symbol: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    val resId = when (symbol.uppercase()) {
        "BTC" -> R.drawable.coin_btc
        "ETH" -> R.drawable.coin_eth
        "USDT" -> R.drawable.coin_usdt
        "SOL" -> R.drawable.coin_sol
        "DOGE" -> R.drawable.coin_doge
        "XRP" -> R.drawable.coin_xrp
        "ADA" -> R.drawable.coin_ada
        "BNB" -> R.drawable.coin_bnb
        else -> null
    }

    if (resId != null) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = "$symbol icon",
            modifier = modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Fit
        )
    } else {
        GenericCoinIcon(symbol = symbol, size = size, modifier = modifier)
    }
}

@Composable
fun GenericCoinIcon(symbol: String, size: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFF2C2B3C)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol.take(2).uppercase(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontFamily = InterFontFamily,
            fontSize = (size.value * 0.40f).sp
        )
    }
}
