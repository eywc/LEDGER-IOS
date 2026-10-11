package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Authentic iOS-style Activity Indicator (UIActivityIndicatorView).
 * Uses 12 radiating rounded pill spokes with progressive reveal during pull
 * and iconic stepped rotational fade animation while refreshing.
 * No circular background card, pure iOS Cupertino aesthetic.
 */
@Composable
fun IosActivityIndicator(
    progress: Float,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    tint: Color = Color(0xFFC7C7CD)
) {
    val numSpokes = 12

    val infiniteTransition = rememberInfiniteTransition(label = "ios_spinner_transition")
    val spinnerStep by if (isRefreshing) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = numSpokes.toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 960, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "ios_spinner_step"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val center = Offset(w / 2f, h / 2f)

        // Geometric proportion matching iOS UIActivityIndicatorView
        val spokeWidth = w * 0.088f
        val spokeHeight = h * 0.27f
        val spokeCorner = spokeWidth / 2f

        for (i in 0 until numSpokes) {
            val angle = i * (360f / numSpokes)

            val alpha = if (isRefreshing) {
                // Classic iOS stepped decay through the 12 spokes
                val stepOffset = ((spinnerStep.toInt() - i + numSpokes) % numSpokes)
                val decay = 1f - (stepOffset.toFloat() / numSpokes) * 0.80f
                decay.coerceIn(0.18f, 1.0f)
            } else {
                // Progressive clockwise spoke appearance as user pulls down
                val spokeThreshold = i.toFloat() / numSpokes
                if (progress >= spokeThreshold) {
                    ((progress - spokeThreshold) * numSpokes).coerceIn(0.18f, 1.0f)
                } else {
                    0f
                }
            }

            if (alpha > 0f) {
                rotate(degrees = angle, pivot = center) {
                    drawRoundRect(
                        color = tint.copy(alpha = alpha),
                        topLeft = Offset(center.x - spokeWidth / 2f, center.y - h * 0.48f),
                        size = Size(spokeWidth, spokeHeight),
                        cornerRadius = CornerRadius(spokeCorner, spokeCorner)
                    )
                }
            }
        }
    }
}

/**
 * iOS Pull-To-Refresh Layout.
 * Provides damped rubber-band physics, haptic feedback tick upon passing the threshold,
 * smooth spring physics snap, and the standalone iOS activity indicator.
 */
@Composable
fun IosPullToRefreshLayout(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    refreshTriggerDistance: Dp = 64.dp,
    refreshingIndicatorHeight: Dp = 50.dp,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()

    val triggerPx = with(density) { refreshTriggerDistance.toPx() }
    val indicatorHeightPx = with(density) { refreshingIndicatorHeight.toPx() }

    val pullOffsetY = remember { Animatable(0f) }
    var hasHapticPlayed by remember { mutableStateOf(false) }

    val handleRelease: suspend () -> Unit = {
        if (pullOffsetY.value >= triggerPx && !isRefreshing) {
            pullOffsetY.animateTo(indicatorHeightPx, spring(dampingRatio = 0.82f, stiffness = 400f))
            onRefresh()
        } else if (!isRefreshing) {
            pullOffsetY.animateTo(0f, spring(dampingRatio = 0.85f, stiffness = 450f))
        }
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            pullOffsetY.animateTo(indicatorHeightPx, spring(dampingRatio = 0.82f, stiffness = 400f))
        } else {
            hasHapticPlayed = false
            pullOffsetY.animateTo(0f, spring(dampingRatio = 0.88f, stiffness = 450f))
        }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // Consume scroll up if currently pulled down
                if (available.y < 0 && pullOffsetY.value > 0f && !isRefreshing) {
                    val newOffset = (pullOffsetY.value + available.y).coerceAtLeast(0f)
                    val consumed = newOffset - pullOffsetY.value
                    scope.launch { pullOffsetY.snapTo(newOffset) }
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (available.y > 0 && !isRefreshing) {
                    // Damped rubber-band resistance formula matching iOS
                    val dragFactor = 0.50f * (1f - (pullOffsetY.value / (triggerPx * 2.8f)).coerceIn(0f, 0.75f))
                    val newOffset = pullOffsetY.value + (available.y * dragFactor)
                    scope.launch { pullOffsetY.snapTo(newOffset) }

                    // Trigger haptic tick on passing the threshold
                    if (newOffset >= triggerPx && !hasHapticPlayed) {
                        hasHapticPlayed = true
                        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    } else if (newOffset < triggerPx && hasHapticPlayed) {
                        hasHapticPlayed = false
                    }

                    return Offset(0f, available.y)
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (pullOffsetY.value > 0f && !isRefreshing) {
                    handleRelease()
                    return available
                }
                return Velocity.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (pullOffsetY.value > 0f && !isRefreshing) {
                    handleRelease()
                }
                return Velocity.Zero
            }
        }
    }

    Box(
        modifier = modifier
            .nestedScroll(nestedScrollConnection)
            .pointerInput(isRefreshing) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Final)
                        val allUp = event.changes.all { !it.pressed }
                        if (allUp && pullOffsetY.value > 0f && !isRefreshing) {
                            scope.launch { handleRelease() }
                        }
                    }
                }
            }
    ) {
        val pullProgress = (pullOffsetY.value / triggerPx).coerceIn(0f, 1f)
        val showIndicator = pullOffsetY.value > 4f || isRefreshing

        if (showIndicator) {
            val indicatorAlpha = if (isRefreshing) 1f else (pullOffsetY.value / (triggerPx * 0.35f)).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(refreshingIndicatorHeight)
                    .align(Alignment.TopCenter)
                    .offset {
                        val extraOffset = ((pullOffsetY.value - indicatorHeightPx) * 0.35f).coerceAtLeast(0f)
                        IntOffset(0, extraOffset.roundToInt())
                    },
                contentAlignment = Alignment.Center
            ) {
                IosActivityIndicator(
                    progress = pullProgress,
                    isRefreshing = isRefreshing,
                    size = 28.dp,
                    tint = Color(0xFFC7C7CD),
                    modifier = Modifier.alpha(indicatorAlpha)
                )
            }
        }

        // Main scrollable content translated down with the pull
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, pullOffsetY.value.roundToInt()) }
        ) {
            content()
        }
    }
}
