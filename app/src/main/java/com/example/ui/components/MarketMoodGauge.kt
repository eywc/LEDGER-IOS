package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MarketMood
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LedgerCard
import com.example.ui.theme.LedgerCardBorder
import com.example.ui.theme.LedgerGreen
import com.example.ui.theme.LedgerRed
import com.example.ui.theme.LedgerTextPrimary
import com.example.ui.theme.LedgerTextSecondary

@Composable
fun MarketMoodCard(
    mood: MarketMood,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .width(108.dp)
            .height(118.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(LedgerCard)
            .border(1.dp, LedgerCardBorder, RoundedCornerShape(16.dp))
            .padding(top = 10.dp, bottom = 8.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Gauge with score
            Box(
                modifier = Modifier.size(54.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(50.dp)) {
                    val strokeW = 4.dp.toPx()
                    val arcSize = size.width - strokeW
                    val topLeft = Offset(strokeW / 2, strokeW / 2)

                    // Track background
                    drawArc(
                        color = Color(0xFF2B293A),
                        startAngle = 145f,
                        sweepAngle = 250f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(arcSize, arcSize),
                        style = Stroke(width = strokeW, cap = StrokeCap.Round)
                    )

                    // Active gradient arc
                    val sweepProgress = (mood.score / 100f).coerceIn(0f, 1f) * 250f
                    val arcBrush = Brush.sweepGradient(
                        colors = listOf(
                            LedgerRed,
                            Color(0xFFEAB308),
                            LedgerGreen
                        )
                    )

                    drawArc(
                        brush = arcBrush,
                        startAngle = 145f,
                        sweepAngle = sweepProgress,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(arcSize, arcSize),
                        style = Stroke(width = strokeW, cap = StrokeCap.Round)
                    )
                }

                Text(
                    text = "${mood.score}",
                    color = LedgerTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Mood",
                color = LedgerTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = InterFontFamily
            )

            val statusColor = when {
                mood.score < 45 -> LedgerRed
                mood.score > 55 -> LedgerGreen
                else -> LedgerTextSecondary
            }

            Text(
                text = mood.label,
                color = statusColor,
                fontSize = 11.sp,
                fontFamily = InterFontFamily
            )
        }
    }
}
