package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PredictColor
import com.example.ui.theme.PredictGreen
import com.example.ui.theme.PredictRed
import com.example.ui.theme.PredictViolet

@Composable
fun ColorIndicatorDot(
    colors: List<PredictColor>,
    size: Dp = 12.dp,
    modifier: Modifier = Modifier
) {
    if (colors.size == 2) {
        // Dual color split (e.g. Red & Violet, or Green & Violet)
        val c1 = Color(colors[0].hexColor)
        val c2 = Color(colors[1].hexColor)
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(c1, c2)
                    )
                )
        )
    } else if (colors.isNotEmpty()) {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(Color(colors[0].hexColor))
        )
    }
}

@Composable
fun NumberBadge(
    number: Int,
    size: Dp = 28.dp,
    fontSize: Int = 14,
    modifier: Modifier = Modifier
) {
    val colors = PredictColor.fromNumber(number)
    val backgroundBrush = if (colors.size == 2) {
        Brush.linearGradient(
            listOf(Color(colors[0].hexColor), Color(colors[1].hexColor))
        )
    } else {
        val c = Color(colors[0].hexColor)
        Brush.linearGradient(listOf(c, c))
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundBrush)
    ) {
        Text(
            text = number.toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize.sp
        )
    }
}

@Composable
fun ChipBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
