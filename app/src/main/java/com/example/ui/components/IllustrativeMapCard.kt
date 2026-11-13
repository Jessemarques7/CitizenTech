package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.LocalAcessivel
import com.example.ui.theme.LocalCitizenTechColors

@Composable
fun IllustrativeMapCard(
    locais: List<LocalAcessivel>,
    onPinClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCitizenTechColors.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .semantics {
                contentDescription =
                    "Mapa ilustrativo da área demonstrativa com ${locais.size} pontos de interesse. Os pontos também aparecem na lista abaixo."
            },
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFFDFECE7),
        border = BorderStroke(1.dp, Color(0xFFD1E2DB)),
        shadowElevation = 2.dp,
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val mapW = maxWidth
            val mapH = maxHeight

            // Stylized street grid, blocks, and river matching styles.css
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(color = Color(0xFFE7F0EB))

                rotate(degrees = -7f, pivot = center) {
                    val streetColor = Color.White
                    val minorStreet = Color(0xFFD1E1D9)
                    // Major avenues
                    drawLine(
                        color = streetColor,
                        start = Offset(-40f, size.height * 0.30f),
                        end = Offset(size.width + 40f, size.height * 0.42f),
                        strokeWidth = 20f,
                    )
                    drawLine(
                        color = streetColor,
                        start = Offset(-40f, size.height * 0.68f),
                        end = Offset(size.width + 40f, size.height * 0.74f),
                        strokeWidth = 18f,
                    )
                    drawLine(
                        color = streetColor,
                        start = Offset(size.width * 0.34f, -40f),
                        end = Offset(size.width * 0.28f, size.height + 40f),
                        strokeWidth = 18f,
                    )
                    drawLine(
                        color = streetColor,
                        start = Offset(size.width * 0.72f, -40f),
                        end = Offset(size.width * 0.68f, size.height + 40f),
                        strokeWidth = 20f,
                    )
                    // Minor grid lines
                    drawLine(
                        color = minorStreet,
                        start = Offset(0f, size.height * 0.50f),
                        end = Offset(size.width, size.height * 0.50f),
                        strokeWidth = 5f,
                    )
                }

                // Stylized river diagonal band
                rotate(degrees = -18f, pivot = Offset(size.width * 0.5f, size.height * 0.52f)) {
                    drawRect(
                        color = Color(0xFFC6E3E4),
                        topLeft = Offset(-size.width * 0.1f, size.height * 0.49f),
                        size = Size(size.width * 1.25f, 18.dp.toPx()),
                    )
                }

                // City blocks
                val blockFill = Color(0xFFD4E4DC)
                drawRoundRect(
                    color = blockFill,
                    topLeft = Offset(size.width * 0.08f, size.height * 0.12f),
                    size = Size(72.dp.toPx(), 42.dp.toPx()),
                    cornerRadius = CornerRadius(8.dp.toPx()),
                )
                drawRoundRect(
                    color = blockFill,
                    topLeft = Offset(size.width * 0.70f, size.height * 0.12f),
                    size = Size(84.dp.toPx(), 54.dp.toPx()),
                    cornerRadius = CornerRadius(8.dp.toPx()),
                )
                drawRoundRect(
                    color = blockFill,
                    topLeft = Offset(size.width * 0.16f, size.height * 0.65f),
                    size = Size(68.dp.toPx(), 52.dp.toPx()),
                    cornerRadius = CornerRadius(8.dp.toPx()),
                )
                drawRoundRect(
                    color = blockFill,
                    topLeft = Offset(size.width * 0.65f, size.height * 0.66f),
                    size = Size(74.dp.toPx(), 44.dp.toPx()),
                    cornerRadius = CornerRadius(8.dp.toPx()),
                )
            }

            // Street labels
            Text(
                text = "R. DAS PALMEIRAS",
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF648079),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = mapW * 0.08f, top = mapH * 0.58f),
            )
            Text(
                text = "AV. INTEGRAÇÃO",
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF648079),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = mapW * 0.07f, top = mapH * 0.07f),
            )
            Text(
                text = "PRAÇA DAS ARTES",
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF648079),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = mapW * 0.08f, bottom = 42.dp),
            )

            // Interactive map pins with 48dp accessible hit targets
            MapPinMarker(
                categoria = "Serviço público",
                label = "Biblioteca Central",
                pinColor = colors.teal,
                onClick = { onPinClick("biblioteca") },
                modifier = Modifier
                    .offset(x = mapW * 0.24f, y = mapH * 0.22f)
                    .testTag("map_pin_biblioteca"),
            )
            MapPinMarker(
                categoria = "Transporte",
                label = "Terminal Central",
                pinColor = Color(0xFFC36F19),
                onClick = { onPinClick("terminal") },
                modifier = Modifier
                    .offset(x = mapW * 0.58f, y = mapH * 0.42f)
                    .testTag("map_pin_terminal"),
            )
            MapPinMarker(
                categoria = "Cultura e lazer",
                label = "Centro Cultural",
                pinColor = colors.navy,
                onClick = { onPinClick("centro-cultural") },
                modifier = Modifier
                    .offset(x = mapW * 0.76f, y = mapH * 0.16f)
                    .testTag("map_pin_centro_cultural"),
            )

            // Bottom map badges
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.94f),
                    shadowElevation = 2.dp,
                ) {
                    Text(
                        text = "${locais.size} locais cadastrados",
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.navy,
                    )
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.94f),
                    shadowElevation = 2.dp,
                ) {
                    Text(
                        text = "MAPA ILUSTRATIVO",
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.navy,
                    )
                }
            }
        }
    }
}

@Composable
private fun MapPinMarker(
    categoria: String,
    label: String,
    pinColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(CircleShape)
            .clickable(
                role = Role.Button,
                onClick = onClick,
            )
            .semantics { contentDescription = "Abrir detalhes de $label" },
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .size(38.dp)
                .shadow(4.dp, CircleShape),
            shape = CircleShape,
            color = pinColor,
            border = BorderStroke(2.5.dp, Color.White),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = categoryIconFor(categoria),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}
