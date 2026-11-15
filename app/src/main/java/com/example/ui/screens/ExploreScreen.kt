package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AccessibilityFilters
import com.example.domain.model.Alerta
import com.example.domain.model.LocalAcessivel
import com.example.ui.components.CitizenTechCard
import com.example.ui.components.EyebrowText
import com.example.ui.components.HeroPill
import com.example.ui.components.IllustrativeMapCard
import com.example.ui.components.InfoNoteBox
import com.example.ui.components.MiniTag
import com.example.ui.components.PlaceCategoryBadge
import com.example.ui.state.CitizenTechUiState
import com.example.ui.theme.LocalCitizenTechColors
import kotlin.math.roundToInt

@Composable
fun ExploreScreen(
    uiState: CitizenTechUiState,
    onSearchChange: (String) -> Unit,
    onFilterSelect: (String) -> Unit,
    onPlaceClick: (String) -> Unit,
    onAlertsClick: () -> Unit,
    onNewReportClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCitizenTechColors.current
    val filteredLocais = uiState.filteredLocais
    val topAlert = uiState.alertas.firstOrNull()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp)
                .testTag("explore_list"),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        EyebrowText(text = "CIDADE DEMONSTRATIVA")
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Olá, ${uiState.apiConfig.usuarioNome.substringBefore(' ').ifBlank { "Alex" }}.",
                            style = MaterialTheme.typography.headlineMedium,
                            color = colors.navy,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = colors.teal,
                                modifier = Modifier.size(14.dp),
                            )
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = colors.navy)) {
                                        append("Centro")
                                    }
                                    append(" · área de demonstração")
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.muted,
                            )
                        }
                    }
                    HeroPill(text = "MAIS AUTONOMIA")
                }
            }

            if (topAlert != null) {
                item {
                    NoticeBanner(
                        alert = topAlert,
                        onClick = onAlertsClick,
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Explore a região",
                        style = MaterialTheme.typography.headlineSmall,
                        color = colors.navy,
                    )
                    TextButton(
                        onClick = onAlertsClick,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("explore_view_alerts_button"),
                    ) {
                        Text(
                            text = "Ver alertas",
                            fontWeight = FontWeight.ExtraBold,
                            color = colors.tealDark,
                            fontSize = 12.5.sp,
                        )
                    }
                }
            }

            item {
                IllustrativeMapCard(
                    locais = uiState.locais,
                    onPinClick = onPlaceClick,
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("explore_search_input"),
                    placeholder = {
                        Text(
                            text = "Buscar local ou serviço",
                            color = colors.muted,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                            tint = colors.muted,
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { onSearchChange("") },
                                modifier = Modifier.testTag("explore_clear_search"),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Limpar busca",
                                    tint = colors.muted,
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colors.cardSurface,
                        unfocusedContainerColor = colors.cardSurface,
                        focusedBorderColor = colors.teal,
                        unfocusedBorderColor = colors.border,
                        focusedTextColor = colors.ink,
                        unfocusedTextColor = colors.ink,
                    ),
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AccessibilityFilters.all.forEach { filter ->
                        val selected = uiState.selectedPlaceFilter == filter
                        val label = when (filter) {
                            AccessibilityFilters.ALL -> "Todos os locais"
                            AccessibilityFilters.ACCESSIBLE -> "Acessível"
                            else -> "Acesso parcial"
                        }
                        FilterChip(
                            selected = selected,
                            onClick = { onFilterSelect(filter) },
                            modifier = Modifier.testTag("filter_chip_$filter"),
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colors.navy,
                                selectedLabelColor = Color.White,
                                containerColor = colors.cardSurface,
                                labelColor = colors.muted,
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = colors.border,
                                selectedBorderColor = colors.navy,
                            ),
                            shape = RoundedCornerShape(999.dp),
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Locais próximos",
                        style = MaterialTheme.typography.headlineSmall,
                        color = colors.navy,
                    )
                    Text(
                        text = "${filteredLocais.size} resultados",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.muted,
                    )
                }
            }

            if (filteredLocais.isEmpty()) {
                item {
                    CitizenTechCard {
                        Text(
                            text = "Nenhum local encontrado com esse filtro.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.muted,
                        )
                    }
                }
            } else {
                items(filteredLocais, key = { it.id }) { local ->
                    PlaceCardItem(
                        local = local,
                        onClick = { onPlaceClick(local.id) },
                    )
                }
            }

            item {
                Button(
                    onClick = onNewReportClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("explore_new_report_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.teal,
                        contentColor = Color.White,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Relatar uma barreira",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                    )
                }
            }

            item {
                InfoNoteBox(
                    text = "As informações desta área são demonstrativas. Consulte a data de atualização e os detalhes antes de planejar seu deslocamento."
                )
            }
        }
    }
}

@Composable
private fun NoticeBanner(
    alert: Alerta,
    onClick: () -> Unit,
) {
    val colors = LocalCitizenTechColors.current
    val valorFmt = if (alert.valor % 1.0 == 0.0) {
        alert.valor.roundToInt().toString()
    } else {
        alert.valor.toString()
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .testTag("explore_alert_banner"),
        shape = RoundedCornerShape(16.dp),
        color = colors.amberBg,
        border = BorderStroke(1.dp, colors.amberBorder),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF8E4BD)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.PriorityHigh,
                    contentDescription = "Aviso",
                    tint = colors.amber,
                    modifier = Modifier.size(18.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.titulo,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF73450B),
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${alert.local} · leitura simulada de $valorFmt ${alert.unidade}",
                    fontSize = 11.5.sp,
                    color = Color(0xFF80663F),
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ver alertas",
                tint = Color(0xFF8A641F),
            )
        }
    }
}

@Composable
private fun PlaceCardItem(
    local: LocalAcessivel,
    onClick: () -> Unit,
) {
    val colors = LocalCitizenTechColors.current
    val isPartial = !local.statusAcessibilidade.equals("Acessível", ignoreCase = true)
    val isHighOcc = local.nivelOcupacao.equals("Alta", ignoreCase = true)

    CitizenTechCard(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .testTag("place_card_${local.id}"),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlaceCategoryBadge(categoria = local.categoria)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = local.nome,
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.navy,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = local.endereco,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.muted,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MiniTag(
                        text = local.statusAcessibilidade,
                        isWarning = isPartial,
                    )
                    if (isHighOcc) {
                        MiniTag(
                            text = "Alta ocupação",
                            isWarning = true,
                        )
                    }
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Abrir ${local.nome}",
                tint = colors.muted,
            )
        }
    }
}
