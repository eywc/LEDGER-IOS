package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun QrCodeView(
    data: String,
    modifier: Modifier = Modifier,
    size: Dp = 190.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size - 24.dp)) {
            val gridSize = 25
            val cellSize = this.size.width / gridSize

            // Generate deterministic pseudo-random QR matrix based on hash of data
            val hash = abs(data.hashCode())
            val seedChars = data.toByteArray()

            fun isModuleBlack(r: Int, c: Int): Boolean {
                // Top-Left Finder pattern
                if (r < 7 && c < 7) {
                    return (r == 0 || r == 6 || c == 0 || c == 6) || (r in 2..4 && c in 2..4)
                }
                // Top-Right Finder pattern
                if (r < 7 && c >= gridSize - 7) {
                    val sc = c - (gridSize - 7)
                    return (r == 0 || r == 6 || sc == 0 || sc == 6) || (r in 2..4 && sc in 2..4)
                }
                // Bottom-Left Finder pattern
                if (r >= gridSize - 7 && c < 7) {
                    val sr = r - (gridSize - 7)
                    return (sr == 0 || sr == 6 || c == 0 || c == 6) || (sr in 2..4 && c in 2..4)
                }

                // Timing patterns
                if (r == 6 && c % 2 == 0) return true
                if (c == 6 && r % 2 == 0) return true

                // Data area
                val byteIndex = (r * gridSize + c) % seedChars.size
                val b = seedChars[byteIndex].toInt()
                val bit = ((b xor (hash shr (r % 16))) shr (c % 8)) and 1
                return bit == 1
            }

            for (r in 0 until gridSize) {
                for (c in 0 until gridSize) {
                    if (isModuleBlack(r, c)) {
                        drawRect(
                            color = Color.Black,
                            topLeft = Offset(c * cellSize, r * cellSize),
                            size = Size(cellSize + 0.5f, cellSize + 0.5f)
                        )
                    }
                }
            }
        }
    }
}
