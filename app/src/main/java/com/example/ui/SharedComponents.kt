package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.RoyalBlue

@Composable
fun GradientText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.ExtraBold
    )
) {
    Text(
        text = text,
        modifier = modifier,
        style = style.copy(
            brush = Brush.horizontalGradient(
                colors = listOf(CyberBlue, RoyalBlue)
            )
        )
    )
}

@Composable
fun CyberCard(
    modifier: Modifier = Modifier,
    borderColor: Color = CyberBlue.copy(alpha = 0.3f),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(12.dp),
        content = content
    )
}

@Composable
fun GlowBadge(
    text: String,
    glowColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = glowColor.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, glowColor.copy(alpha = 0.5f))
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                color = glowColor,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
