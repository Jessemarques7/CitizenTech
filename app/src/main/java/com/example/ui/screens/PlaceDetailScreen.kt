package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.LocalAcessivel
import com.example.domain.model.Ocorrencia
import com.example.domain.model.PreferenciasUsuario
import com.example.ui.components.CitizenTechCard
import com.example.ui.components.EyebrowText
import com.example.ui.components.InfoNoteBox
import com.example.ui.components.OccupancyCard
import com.example.ui.components.RouteGuidanceDialog
import com.example.ui.components.StatusTag
import com.example.ui.theme.LocalCitizenTechColors

@Composable
fun PlaceDetailScreen(
    local: LocalAcessivel?,
    relatedReports: List<Ocorrencia>,
    preferencias: PreferenciasUsuario,
    isLoading: Boolean,
    onBack: () -> Unit,
    onReportBarrier: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCitizenTechColors.current
    var showGuidanceDialog by remember { mutableStateOf(false) }

    BackHandler(onBack = onBack)

    if (showGuidanceDialog && local != null) {
        RouteGuidanceDialog(
            local = local,
            preferencias = preferencias,
            onDismiss = { showGuidanceDialog = false },
        )
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp)
                .testTag("detail_list"),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                TextButton(
                    onClick = onBack,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("detail_back_button"),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = colors.tealDark,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Voltar para explorar",
                        color = colors.tealDark,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                    )
                }
            }

            if (isLoading && local == null) {
                item {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = colors.teal,
                    )
                }
            } else if (local == null) {
                item {
                    CitizenTechCard {
                        Text(
                            text = "Local não encontrado.",
                            modifier = Modifier.padding(24.dp),
                            color = colors.muted,
                        )
                    }
                }
            } else {
                item {
                    Column {
                        EyebrowText(text = local.categoria)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = local.nome,
                                style = MaterialTheme.typography.headlineMedium,
                                color = colors.navy,
                                modifier = Modifier.weight(1f),
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            StatusTag(text = local.statusAcessibilidade)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = colors.teal,
                                modifier = Modifier.size(15.dp),
                            )
                            Text(
                                text = local.endereco,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.muted,
                            )
                        }
                    }
                }

                item {
                    OccupancyCard(
                        ocupacao = local.ocupacao,
                        nivelOcupacao = local.nivelOcupacao,
                        ultimaLeitura = local.ultimaLeitura,
                    )
                }

                item {
                    CitizenTechCard {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "O que você encontra",
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.navy,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = local.resumo,
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.muted,
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                local.recursos.forEach { recurso ->
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(RoundedCornerShape(7.dp))
                                                .background(colors.greenBg),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = null,
                                                tint = colors.green,
                                                modifier = Modifier.size(14.dp),
                                            )
                                        }
                                        Text(
                                            text = recurso,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = colors.ink,
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            InfoNoteBox(
                                text = "Informação atualizada em ${local.atualizadoEm}. Fonte: ${local.confianca}. A condição pode mudar."
                            )
                        }
                    }
                }

                item {
                    val activeCommunityReports = relatedReports.filter {
                        !it.status.equals("Resolvida", ignoreCase = true)
                    }
                    val totalObstacles = local.obstaculos.size + activeCommunityReports.size

                    CitizenTechCard {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Barreiras relatadas",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = colors.navy,
                                )
                                Text(
                                    text = "$totalObstacles registro(s)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.muted,
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            if (totalObstacles == 0) {
                                Text(
                                    text = "Nenhuma barreira registrada até o momento. Isso não garante ausência de obstáculos.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.muted,
                                )
                            } else {
                                local.obstaculos.forEachIndexed { index, obstaculo ->
                                    if (index > 0) HorizontalDivider(color = colors.border)
                                    ObstacleRow(
                                        title = obstaculo,
                                        subtitle = "Relato comunitário · não confirmado",
                                    )
                                }
                                activeCommunityReports.forEachIndexed { index, report ->
                                    if (local.obstaculos.isNotEmpty() || index > 0) {
                                        HorizontalDivider(color = colors.border)
                                    }
                                    ObstacleRow(
                                        title = "${report.categoria} (${report.id})",
                                        subtitle = "${report.status} · relato comunitário não confirmado",
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Button(
                            onClick = { onReportBarrier(local.id) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("detail_report_barrier_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.mint,
                                contentColor = colors.tealDark,
                            ),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Relatar barreira",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.5.sp,
                            )
                        }

                        Button(
                            onClick = { showGuidanceDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("detail_route_guidance_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.teal,
                                contentColor = Color.White,
                            ),
                        ) {
                            Text(
                                text = "Ver orientação",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.5.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ObstacleRow(
    title: String,
    subtitle: String,
) {
    val colors = LocalCitizenTechColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(colors.amberBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.PriorityHigh,
                contentDescription = null,
                tint = colors.amber,
                modifier = Modifier.size(16.dp),
            )
        }
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = colors.ink,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.muted,
            )
        }
    }
}
