package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LocalCitizenTechColors

@Composable
fun CitizenTechTopBar(
    isApiMode: Boolean,
    onBrandClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCitizenTechColors.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.cardSurface,
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(
                            role = Role.Button,
                            onClick = onBrandClick,
                        )
                        .minimumInteractiveComponentSize()
                        .padding(end = 8.dp)
                        .testTag("topbar_brand_button")
                        .semantics { contentDescription = "CitizenTech, ir para início" },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    CitizenTechLogoMark(modifier = Modifier.size(40.dp))
                    Column {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = colors.navy)) {
                                    append(stringResource(R.string.brand_title_prefix))
                                }
                                withStyle(SpanStyle(color = colors.teal)) {
                                    append(stringResource(R.string.brand_title_suffix))
                                }
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.6).sp,
                        )
                        Text(
                            text = stringResource(R.string.brand_subtitle),
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.3.sp,
                            color = colors.muted,
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = if (isApiMode) colors.greenBg else colors.mint,
                        border = BorderStroke(1.dp, colors.border),
                    ) {
                        Text(
                            text = if (isApiMode) "API" else "DEMO",
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isApiMode) colors.green else colors.tealDark,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable(
                                role = Role.Button,
                                onClick = onProfileClick,
                            )
                            .testTag("topbar_profile_button")
                            .semantics { contentDescription = "Abrir perfil" },
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(colors.navy),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "A",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                            )
                        }
                    }
                }
            }
            HorizontalDivider(thickness = 1.dp, color = colors.border)
        }
    }
}

@Composable
fun CitizenTechLogoMark(modifier: Modifier = Modifier) {
    val colors = LocalCitizenTechColors.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(13.dp))
            .background(colors.mint),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(28.dp)) {
            val w = size.width
            val h = size.height
            val pinPath = Path().apply {
                moveTo(w * 0.50f, h * 0.11f)
                cubicTo(w * 0.30f, h * 0.11f, w * 0.17f, h * 0.25f, w * 0.17f, h * 0.42f)
                cubicTo(w * 0.17f, h * 0.65f, w * 0.50f, h * 0.90f, w * 0.50f, h * 0.90f)
                cubicTo(w * 0.50f, h * 0.90f, w * 0.83f, h * 0.65f, w * 0.83f, h * 0.42f)
                cubicTo(w * 0.83f, h * 0.25f, w * 0.70f, h * 0.11f, w * 0.50f, h * 0.11f)
                close()
            }
            drawPath(
                path = pinPath,
                color = colors.teal,
                style = Stroke(width = w * 0.085f, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
            drawCircle(
                color = colors.teal,
                radius = w * 0.13f,
                center = Offset(w * 0.50f, h * 0.425f),
                style = Stroke(width = w * 0.085f),
            )
            drawLine(
                color = colors.teal,
                start = Offset(w * 0.50f, h * 0.56f),
                end = Offset(w * 0.50f, h * 0.72f),
                strokeWidth = w * 0.085f,
                cap = StrokeCap.Round,
            )
        }
    }
}
