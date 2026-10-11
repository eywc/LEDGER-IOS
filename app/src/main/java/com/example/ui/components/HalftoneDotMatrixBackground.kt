package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.LedgerBlack
import com.example.ui.theme.LedgerPurpleGlow

@Composable
fun HalftoneDotMatrixBackground(
    modifier: Modifier = Modifier,
    showDots: Boolean = true
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Base black background
        drawRect(color = LedgerBlack)

        // Radial purple glow at top-right
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    LedgerPurpleGlow.copy(alpha = 0.55f),
                    LedgerPurpleGlow.copy(alpha = 0.25f),
                    Color(0xFF1B0D2E).copy(alpha = 0.15f),
                    Color.Transparent
                ),
                center = Offset(width * 0.85f, height * 0.25f),
                radius = width * 0.95f
            ),
            radius = width * 0.95f,
            center = Offset(width * 0.85f, height * 0.25f)
        )

        // Second subtle ambient violet glow on center-left
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF281140).copy(alpha = 0.20f),
                    Color.Transparent
                ),
                center = Offset(width * 0.15f, height * 0.40f),
                radius = width * 0.60f
            ),
            radius = width * 0.60f,
            center = Offset(width * 0.15f, height * 0.40f)
        )

        // Dot matrix / halftone texture concentrated around the right side
        if (showDots) {
            val step = 14f
            val startY = height * 0.12f
            val endY = height * 0.45f
            val startX = width * 0.35f
            val endX = width * 1.05f

            var y = startY
            while (y < endY) {
                var x = startX
                while (x < endX) {
                    val dx = (x - width * 0.75f) / (width * 0.4f)
                    val dy = (y - height * 0.28f) / (height * 0.18f)
                    val distSq = dx * dx + dy * dy
                    val intensity = (1f - distSq).coerceIn(0f, 1f)

                    if (intensity > 0.05f) {
                        val dotRadius = 1.2f + (intensity * 2.2f)
                        val alpha = (intensity * 0.32f).coerceIn(0.04f, 0.45f)
                        drawCircle(
                            color = Color(0xFFC084FC).copy(alpha = alpha),
                            radius = dotRadius,
                            center = Offset(x, y)
                        )
                    }
                    x += step
                }
                y += step
            }
        }
    }
}
