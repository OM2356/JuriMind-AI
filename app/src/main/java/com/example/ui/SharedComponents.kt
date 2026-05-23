package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    borderColor: Color = GlassWhiteBorder,
    borderWidth: Dp = 1.dp,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    glowActive: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "borderGlow")
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderGlow"
    )

    val currentBorderColor = if (glowActive) {
        CyberCyan.copy(alpha = animatedAlpha)
    } else {
        borderColor
    }

    Card(
        modifier = modifier
            .clip(shape)
            .border(borderWidth, currentBorderColor, shape),
        colors = CardDefaults.cardColors(
            containerColor = CosmicCard.copy(alpha = 0.72f)
        ),
        shape = shape,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            content()
        }
    }
}

// Fixing typo prevented code compilation: "Modifier.fill some()"
// Let's use standard clean Compose properties!
@Composable
fun GlassmorphicCardFixed(
    modifier: Modifier = Modifier,
    borderColor: Color = GlassWhiteBorder,
    borderWidth: Dp = 1.6.dp,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    glowActive: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cardGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cardGlow"
    )

    val finalBorderColor = if (glowActive) {
        CyberCyan.copy(alpha = glowAlpha)
    } else {
        borderColor
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        CosmicCard.copy(alpha = 0.85f),
                        CosmicSurface.copy(alpha = 0.95f)
                    )
                )
            )
            .border(borderWidth, finalBorderColor, shape)
            .padding(16.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun GradientText(
    text: String,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(CyberCyan, CyberMagenta),
    fontSize: androidx.compose.ui.unit.TextUnit = 24.sp,
    fontWeight: FontWeight = FontWeight.Bold,
    textAlign: TextAlign = TextAlign.Start,
    fontFamily: FontFamily = FontFamily.SansSerif
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = fontSize,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        textAlign = textAlign,
        style = TextStyle(
            brush = Brush.linearGradient(colors = colors)
        )
    )
}

@Composable
fun FuturisticDialGauge(
    score: Int, // 0 to 100
    modifier: Modifier = Modifier
) {
    val progressAnim = animateFloatAsState(
        targetValue = score / 100f,
        animationSpec = tween(1500, easing = EaseOutBack),
        label = "gaugeProgress"
    )

    val colorBrush = when {
        score >= 70 -> Brush.sweepGradient(listOf(CyberYellow, CyberRed, CyberYellow))
        score >= 40 -> Brush.sweepGradient(listOf(CyberCyan, CyberYellow, CyberCyan))
        else -> Brush.sweepGradient(listOf(CyberGreen, CyberCyan, CyberGreen))
    }

    val riskText = when {
        score >= 70 -> "CRITICAL RISK"
        score >= 40 -> "MODERATE RISK"
        else -> "SECURE DRAFT"
    }

    val statusColor = when {
        score >= 70 -> CyberRed
        score >= 40 -> CyberYellow
        else -> CyberGreen
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(170.dp)) {
            val strokeWidth = 14.dp.toPx()
            val canvasSize = size
            val radius = (canvasSize.minDimension - strokeWidth) / 2

            // Background arc track
            drawArc(
                color = GlassWhite,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Live progress arc with beautiful gradients
            drawArc(
                brush = colorBrush,
                startAngle = 135f,
                sweepAngle = 270f * progressAnim.value,
                useCenter = false,
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${(progressAnim.value * 100).toInt()}%",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = riskText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

@Composable
fun UsageBarChart(
    data: List<Float>, // 5-7 floats for stats elements
    days: List<String>,
    modifier: Modifier = Modifier
) {
    val maxVal = (data.maxOrNull() ?: 100f).coerceAtLeast(10f)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEachIndexed { idx, value ->
            val scaleAnim = animateFloatAsState(
                targetValue = value / maxVal,
                animationSpec = tween(1200 + (idx * 150), easing = EaseOutQuint),
                label = "barScale"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.85f)
                        .width(26.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Gray trace line
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(GlassWhite)
                    )

                    // Color indicator bar
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(scaleAnim.value)
                            .width(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        CyberMagenta,
                                        CyberCyan
                                    )
                                )
                            )
                            .border(1.dp, GlassWhite, RoundedCornerShape(8.dp))
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = days.getOrNull(idx) ?: "",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
