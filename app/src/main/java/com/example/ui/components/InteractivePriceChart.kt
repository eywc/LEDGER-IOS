package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LedgerGreen
import com.example.ui.theme.LedgerRed
import com.example.ui.theme.LedgerTextMuted
import java.util.Locale

@Composable
fun InteractivePriceChart(
    prices: List<Double>,
    isPositive: Boolean,
    modifier: Modifier = Modifier,
    currencySymbol: String = "$",
    onScrub: ((Double?) -> Unit)? = null
) {
    if (prices.isEmpty()) return

    val minPrice = prices.minOrNull() ?: 0.0
    val maxPrice = prices.maxOrNull() ?: 1.0
    val priceRange = (maxPrice - minPrice).coerceAtLeast(0.0001)

    val lineColor = if (isPositive) LedgerGreen else LedgerRed
    val gradientStart = if (isPositive) LedgerGreen.copy(alpha = 0.22f) else LedgerRed.copy(alpha = 0.22f)

    var touchX by remember { mutableFloatStateOf(-1f) }
    var isTouching by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp)
            .pointerInput(prices) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isTouching = true
                        touchX = offset.x
                        val idx = ((offset.x / size.width) * (prices.size - 1))
                            .toInt()
                            .coerceIn(0, prices.size - 1)
                        onScrub?.invoke(prices[idx])
                    },
                    onDragEnd = {
                        isTouching = false
                        touchX = -1f
                        onScrub?.invoke(null)
                    },
                    onDragCancel = {
                        isTouching = false
                        touchX = -1f
                        onScrub?.invoke(null)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        touchX = change.position.x
                        val idx = ((change.position.x / size.width) * (prices.size - 1))
                            .toInt()
                            .coerceIn(0, prices.size - 1)
                        onScrub?.invoke(prices[idx])
                    }
                )
            }
    ) {
        // High price label at top-left
        Text(
            text = String.format(Locale.US, "%s%,.2f", currencySymbol, maxPrice),
            color = LedgerTextMuted,
            fontSize = 11.sp,
            fontFamily = InterFontFamily,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 6.dp, top = 2.dp)
        )

        // Low price label at bottom-right
        Text(
            text = String.format(Locale.US, "%s%,.2f", currencySymbol, minPrice),
            color = LedgerTextMuted,
            fontSize = 11.sp,
            fontFamily = InterFontFamily,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 6.dp, bottom = 4.dp)
        )

        Canvas(modifier = Modifier.fillMaxSize().padding(vertical = 18.dp)) {
            val w = size.width
            val h = size.height

            val stepX = w / (prices.size - 1).coerceAtLeast(1)
            val points = prices.mapIndexed { index, price ->
                val x = index * stepX
                val normalized = ((price - minPrice) / priceRange).toFloat()
                val y = h - (normalized * (h * 0.85f)) - (h * 0.08f)
                Offset(x, y)
            }

            // Path for smooth line
            val linePath = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points[0].x, points[0].y)
                    for (i in 0 until points.size - 1) {
                        val p0 = points[i]
                        val p1 = points[i + 1]
                        val midX = (p0.x + p1.x) / 2
                        cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
                    }
                }
            }

            // Area path for gradient fill below line
            val fillPath = Path().apply {
                addPath(linePath)
                lineTo(points.last().x, h)
                lineTo(points.first().x, h)
                close()
            }

            // Draw gradient fill
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(gradientStart, Color.Transparent),
                    startY = 0f,
                    endY = h
                )
            )

            // Draw primary curve
            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Scrub vertical line and indicator dot
            if (isTouching && touchX in 0f..w) {
                val clampedX = touchX.coerceIn(0f, w)
                val idx = ((clampedX / w) * (prices.size - 1)).toInt().coerceIn(0, prices.size - 1)
                val pt = points[idx]

                drawLine(
                    color = Color.White.copy(alpha = 0.35f),
                    start = Offset(pt.x, 0f),
                    end = Offset(pt.x, h),
                    strokeWidth = 1.dp.toPx()
                )

                drawCircle(
                    color = Color.White,
                    radius = 4.5.dp.toPx(),
                    center = pt
                )
                drawCircle(
                    color = lineColor,
                    radius = 2.5.dp.toPx(),
                    center = pt
                )
            }
        }
    }
}
