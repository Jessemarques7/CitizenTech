package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.TheaterComedy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberMeter
import com.example.ui.theme.LocalCitizenTechColors
import com.example.ui.theme.MintMeter

@Composable
fun EyebrowText(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCitizenTechColors.current
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = colors.teal,
        modifier = modifier,
    )
}

@Composable
fun HeroPill(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCitizenTechColors.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = colors.mint,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = colors.tealDark,
            maxLines = 1,
        )
    }
}

enum class StatusTone {
    GREEN, AMBER, RED, NEUTRAL
}

fun statusToneFor(status: String): StatusTone = when (status) {
    "Acessível", "Resolvida" -> StatusTone.GREEN
    "Não confirmada", "Encaminhada" -> StatusTone.NEUTRAL
    "Crítico", "Indisponível" -> StatusTone.RED
    else -> StatusTone.AMBER
}

@Composable
fun StatusTag(
    text: String,
    tone: StatusTone = statusToneFor(text),
    modifier: Modifier = Modifier,
) {
    val colors = LocalCitizenTechColors.current
    val (bg, fg) = when (tone) {
        StatusTone.GREEN -> colors.greenBg to colors.green
        StatusTone.AMBER -> colors.amberBg to colors.amber
        StatusTone.RED -> colors.redBg to colors.red
        StatusTone.NEUTRAL -> Color(0xFFEDF1F0) to Color(0xFF52696B)
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = bg,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            fontSize = 10.5.sp,
            fontWeight = FontWeight.ExtraBold,
            color = fg,
            maxLines = 1,
        )
    }
}

@Composable
fun MiniTag(
    text: String,
    isWarning: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCitizenTechColors.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(7.dp),
        color = if (isWarning) colors.amberBg else colors.greenBg,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isWarning) colors.amber else colors.green,
            maxLines = 1,
        )
    }
}

@Composable
fun DemoLabelPill(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCitizenTechColors.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = colors.amberBg,
        border = BorderStroke(1.dp, colors.amberBorder),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 9.5.sp,
            fontWeight = FontWeight.ExtraBold,
            color = colors.amber,
        )
    }
}

@Composable
fun InfoNoteBox(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCitizenTechColors.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = colors.infoNoteBg,
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = colors.muted,
                modifier = Modifier.size(16.dp).padding(top = 1.dp),
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = colors.muted,
            )
        }
    }
}

@Composable
fun CitizenTechCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = LocalCitizenTechColors.current
    val strokeWidth: Dp = if (colors.isHighContrast) 2.dp else 1.dp
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = colors.cardSurface,
        border = BorderStroke(strokeWidth, colors.border),
        shadowElevation = if (colors.isHighContrast) 0.dp else 1.dp,
        content = content,
    )
}

@Composable
fun OccupancyCard(
    ocupacao: Int,
    nivelOcupacao: String,
    ultimaLeitura: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCitizenTechColors.current
    val isHigh = nivelOcupacao.equals("Alta", ignoreCase = true)
    val targetProgress = ((ocupacao * 2f) / 100f).coerceIn(0.08f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 500),
        label = "occupancy_meter",
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = colors.navy,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "OCUPAÇÃO ESTIMADA",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = Color(0xFFC2D8D5),
                )
                DemoLabelPill(text = "DADO SIMULADO")
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = ocupacao.toString(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    lineHeight = 34.sp,
                )
                Text(
                    text = "pessoas no ambiente",
                    fontSize = 12.sp,
                    color = Color(0xFFC2D8D5),
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF41606A)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isHigh) AmberMeter else MintMeter),
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Nível ${nivelOcupacao.lowercase()} · última leitura: $ultimaLeitura",
                fontSize = 11.sp,
                color = Color(0xFFC2D8D5),
            )
        }
    }
}

fun categoryIconFor(categoria: String): ImageVector = when (categoria) {
    "Serviço público" -> Icons.Outlined.AccountBalance
    "Transporte" -> Icons.Outlined.DirectionsBus
    "Cultura e lazer" -> Icons.Outlined.TheaterComedy
    else -> Icons.Outlined.LocationOn
}

@Composable
fun PlaceCategoryBadge(
    categoria: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCitizenTechColors.current
    Box(
        modifier = modifier
            .size(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(colors.mint),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = categoryIconFor(categoria),
            contentDescription = categoria,
            tint = colors.teal,
            modifier = Modifier.size(26.dp),
        )
    }
}
